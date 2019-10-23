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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.Scheduler;
import org.saga.compensation.annotation.Compensate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.time.temporal.ChronoUnit;

/**
 * @author luiz.toscano.menezes
 */
@Aspect
@Configuration
public class CompensationAspect {
    private static Logger logger = LoggerFactory.getLogger(CompensationAspect.class);

    @Autowired
    private Scheduler scheduler;

    private Object evaluate(int index, Object[] args, String el) {
        if (el.isEmpty()) {
            return args[index];
        }
        else {
            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(el, new TemplateParserContext());

            return (args != null && args.length > 0) ? expression.getValue(args[index]) : expression.getValue();
        }
    }

    @Around("@annotation(annotation)")
    public Object compensate(ProceedingJoinPoint joinPoint, Compensate annotation) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int index = annotation.index();
        String el = annotation.el();
        int ttl = annotation.ttl();
        ChronoUnit unit = annotation.unit();
        String fallbackName = annotation.fallbackBean();
        boolean trace = annotation.traceEnabled();

        String key = (String) evaluate(index, args, el);

        Compensation compensation = new CompensationBuilder()
                .withKey(key)
                .withArgs(args)
                .withFallbackBean(fallbackName)
                .withTtl(ttl)
                .withUnit(unit)
                .withScheduler(scheduler)
                .withTraceEnabled(trace)
                .build();

        compensation.compensate();

        return joinPoint.proceed();
    }
}
