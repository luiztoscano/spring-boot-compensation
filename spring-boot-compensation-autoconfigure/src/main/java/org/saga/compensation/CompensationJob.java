/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.saga.compensation;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.saga.compensation.serializer.ContextDeserializer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * @author luiz.toscano.menezes
 */
public class CompensationJob implements Job {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Tracing tracing;

    @Autowired
    private Tracer tracer;

    private CompensationFallback getFallbackBean(String fallbackName) throws CompensationException {
        CompensationFallback fallback = applicationContext.getBean(fallbackName, CompensationFallback.class);

        if (fallback == null) {
            throw new CompensationException(String.format("No fallback bean (%s) found", fallbackName));
        }

        return fallback;
    }

    private CompensationContext getContext(JobDataMap map) throws IOException {
        Object obj = map.get("context");
        CompensationContext context = null;

        if (obj instanceof String) {
            ContextDeserializer deserializer = new ContextDeserializer();
            context = deserializer.deserialize((String) obj);
        } else {
            context = (CompensationContext) obj;
        }

        return context;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
            CompensationContext ctx = getContext(map);
            CompensationFallback fallback = getFallbackBean(ctx.getFallbackBean());
            MDC.setContextMap(ctx.getMdc());

            if (ctx.isTrace()) {
                TraceContext.Extractor extractor = tracing.propagation().extractor(Map<String, String>::get);
                Span span = tracer.newChild(extractor.extract(ctx.getMdc()).context()).name("compensation").start();
                span.tag("compensation-key", ctx.getKey());
                fallback.compensate(ctx);
                span.finish();
            }
            else {
                fallback.compensate(ctx);
            }
        } catch (IOException | CompensationException e) {
            throw new JobExecutionException(e);
        }
    }
}
