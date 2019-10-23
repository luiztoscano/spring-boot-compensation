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

import org.quartz.Scheduler;
import org.slf4j.MDC;

import java.time.temporal.ChronoUnit;

/**
 * @author luiz.toscano.menezes
 */
public class CompensationBuilder {
    private Object[] args;
    private String key;
    private int ttl;
    private ChronoUnit unit = ChronoUnit.MILLIS;
    private String fallbackBean;
    private Scheduler scheduler;
    private boolean traceEnabled;

    public CompensationBuilder withArgs(Object[] args) {
        this.args = args;
        return this;
    }

    public CompensationBuilder withKey(String key) {
        this.key = key;
        return this;
    }

    public CompensationBuilder withTtl(int ttl) {
        this.ttl = ttl;
        return this;
    }

    public CompensationBuilder withUnit(ChronoUnit unit) {
        this.unit = unit;
        return this;
    }

    public CompensationBuilder withFallbackBean(String fallbackBean) {
        this.fallbackBean = fallbackBean;
        return this;
    }

    public CompensationBuilder withScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public CompensationBuilder withTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
        return this;
    }

    public Compensation build() {
        CompensationContext ctx = new CompensationContext(this.key,
                this.ttl,
                this.unit,
                this.fallbackBean,
                this.args,
                MDC.getCopyOfContextMap(),
                this.traceEnabled);

        return new Compensation(this.scheduler, ctx);
    }
}
