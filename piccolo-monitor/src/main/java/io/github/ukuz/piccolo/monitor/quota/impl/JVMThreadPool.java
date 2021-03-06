/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ukuz.piccolo.monitor.quota.impl;

import io.github.ukuz.piccolo.api.PiccoloContext;
import io.github.ukuz.piccolo.api.external.common.Assert;
import io.github.ukuz.piccolo.monitor.MonitorExecutorFactory;
import io.github.ukuz.piccolo.monitor.quota.ThreadPoolQuota;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ukuz90
 */
public class JVMThreadPool implements ThreadPoolQuota {

    private final PiccoloContext context;

    public JVMThreadPool(PiccoloContext context) {
        Assert.notNull(context, "context must not be null");
        this.context = context;
    }

    @Override
    public Object monitor(Object... args) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (context.getExecutorFactory() instanceof MonitorExecutorFactory) {

            MonitorExecutorFactory executorFactory = (MonitorExecutorFactory) context.getExecutorFactory();

            executorFactory.getAllThreadPool().forEach((name, executor) -> {

                if (executor instanceof TimedExecutorService) {

                    try {
                        Field field = TimedExecutorService.class.getDeclaredField("delegate");
                        field.setAccessible(true);
                        Executor delegate = (Executor) field.get(executor);
                        monitorExectorService(delegate, result, name);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }


                } else {

                    monitorExectorService(executor, result, name);

                }


            });

        }
        return result;
    }

    private void monitorExectorService(Executor executor, Map<String, Object> result, String name) {

        if (executor instanceof ThreadPoolExecutor) {

            result.put(name, getPoolInfo((ThreadPoolExecutor) executor));

        } else if (executor instanceof EventLoopGroup) {

            result.put(name, getPoolInfo((EventLoopGroup) executor));

        }

    }

    private Map<String, Object> getPoolInfo(ThreadPoolExecutor executor) {
        Map<String, Object> result = new LinkedHashMap<>(5);
        result.put("corePoolSize", executor.getCorePoolSize());
        result.put("maxPoolSize", executor.getMaximumPoolSize());
        result.put("activeCount(workingThread)", executor.getActiveCount());
        result.put("poolSize(workingThread)", executor.getPoolSize());
        result.put("queueSize(workingThread)", executor.getQueue().size());
        return result;
    }

    private Map<String, Object> getPoolInfo(EventLoopGroup executors) {
        Map<String, Object> result = new LinkedHashMap<>(3);
        int activeCount = 0;
        int poolSize = 0;
        int queueSize = 0;
        for (EventExecutor e : executors) {
            poolSize++;
            if (e instanceof SingleThreadEventExecutor) {
                SingleThreadEventExecutor executor = (SingleThreadEventExecutor) e;
                queueSize += executor.pendingTasks();
                if (executor.threadProperties().state() == Thread.State.RUNNABLE) {
                    activeCount++;
                }
            }
        }
        result.put("activeCount(workingThread)", activeCount);
        result.put("poolSize(workingThread)", poolSize);
        result.put("queueSize(workingThread)", queueSize);
        return result;
    }
}
