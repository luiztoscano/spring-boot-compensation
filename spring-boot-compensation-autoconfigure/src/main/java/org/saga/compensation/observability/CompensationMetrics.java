package org.saga.compensation.observability;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class CompensationMetrics implements MeterBinder {
    private static Logger logger = LoggerFactory.getLogger(CompensationMetrics.class);

    @Autowired
    private Scheduler scheduler;

    private double safeDouble(Callable<Object> callable) {
        try {
            return Double.parseDouble(callable.call().toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        Gauge.builder("compensation-jobs",
                scheduler,
                s -> safeDouble(() -> s.getJobKeys(GroupMatcher.jobGroupEquals("compensation-jobs")).size()))
                .description("Compensation jobs total")
                .register(meterRegistry);

        Gauge.builder("compensation-triggers",
                scheduler,
                s -> safeDouble(() -> s.getTriggerKeys(GroupMatcher.triggerGroupEquals("compensation-triggers")).size()))
                .description("Active compensation triggers total")
                .register(meterRegistry);
    }
}
