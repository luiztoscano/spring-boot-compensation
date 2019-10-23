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

import org.saga.compensation.serializer.ContextSerializer;

import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * @author luiz.toscano.menezes
 */
public class CompensationContext implements Serializable {
    private String key;
    private Integer ttl;
    private ChronoUnit unit;
    private String fallbackBean;
    private Object[] args;
    private Map<String, String> mdc;
    private boolean traceEnabled;

    public CompensationContext(String key, Integer ttl, ChronoUnit unit,
                               String fallbackBean,
                               Object[] args,
                               Map<String, String> mdc,
                               boolean traceEnabled) {
        this.key = key;
        this.ttl = ttl;
        this.unit = unit;
        this.fallbackBean = fallbackBean;
        this.args = args;
        this.mdc = mdc;
        this.traceEnabled = traceEnabled;
    }

    public String getKey() {
        return key;
    }

    public Integer getTtl() {
        return ttl;
    }

    public ChronoUnit getUnit() {
        return unit;
    }

    public String getFallbackBean() {
        return fallbackBean;
    }

    public Object[] getArgs() {
        return args;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }

    public boolean isTrace() {
        return traceEnabled;
    }

    @Override
    public String toString() {
        try {
            ContextSerializer serializer = new ContextSerializer();
            return serializer.serialize(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize context");
        }
    }
}
