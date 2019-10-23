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

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * @author luiz.toscano.menezes
 */
public class Compensation {
    private static Logger logger = LoggerFactory.getLogger(Compensation.class);

    private Scheduler scheduler;
    private CompensationContext ctx;

    private JobDetail buildJobDetail(String key) {
        return JobBuilder.newJob(CompensationJob.class)
                .withIdentity(UUID.randomUUID().toString(), "compensation-jobs")
                .withDescription("compensation-" + key)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, CompensationContext context) {
        LocalDateTime ldt = LocalDateTime.now().plus(context.getTtl(), context.getUnit());
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "compensation-triggers")
                .withDescription("compensation-" + jobDetail.getDescription())
                .startAt(date)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    protected Compensation(Scheduler scheduler, CompensationContext ctx) {
        this.scheduler = scheduler;
        this.ctx = ctx;
    }

    public void compensate() throws CompensationException {
        try {
            logger.info("Scheduling compensation for transaction {} to be triggered in {} {}", ctx.getKey(), ctx.getTtl(), ctx.getUnit());

            JobDetail jobDetail = buildJobDetail(ctx.getKey());
            Trigger trigger = buildTrigger(jobDetail, ctx);
            JobDataMap map = jobDetail.getJobDataMap();
            map.put("context", ctx);
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            throw new CompensationException(e);
        }
    }
}
