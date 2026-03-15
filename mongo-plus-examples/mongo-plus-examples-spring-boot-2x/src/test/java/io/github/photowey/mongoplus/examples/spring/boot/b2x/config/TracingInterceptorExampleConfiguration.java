/*
 * Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.photowey.mongoplus.examples.spring.boot.b2x.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.photowey.mongoplus.plugin.context.AggregationExecutionContext;
import io.github.photowey.mongoplus.plugin.context.InvocationContext;
import io.github.photowey.mongoplus.plugin.context.MapperInvocationContext;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.interceptor.Invocation;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;

/**
 * TracingInterceptorExampleConfiguration - Example configuration that registers a tracing interceptor for tests and sample usage.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@Configuration
public class TracingInterceptorExampleConfiguration {

    /**
     * Creates a thread-safe event sink used by the tracing interceptor.
     *
     * @return the tracing event sink
     */
    @Bean
    public List<String> tracingEvents() {
        return new CopyOnWriteArrayList<>();
    }

    /**
     * Creates a tracing interceptor that records mapper, query, and aggregation execution events.
     *
     * @param tracingEvents the shared tracing event sink
     * @return the tracing interceptor bean
     */
    @Bean
    public MongoPlusInterceptor tracingInterceptor(List<String> tracingEvents) {
        return new MongoPlusInterceptor() {
            @Override
            public int getOrder() {
                return 100;
            }

            @Override
            public boolean supports(InvocationContext context) {
                return context instanceof MapperInvocationContext
                    || context instanceof QueryExecutionContext
                    || context instanceof AggregationExecutionContext;
            }

            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                String label = this.label(invocation.getContext());
                tracingEvents.add(label + ":before");
                try {
                    return invocation.proceed();
                } finally {
                    tracingEvents.add(label + ":after");
                }
            }

            private String label(InvocationContext context) {
                if (context instanceof MapperInvocationContext) {
                    MapperInvocationContext mapperContext = (MapperInvocationContext) context;
                    return "MAPPER:" + mapperContext.getMapperMethod().getName();
                }
                if (context instanceof QueryExecutionContext) {
                    QueryExecutionContext queryContext = (QueryExecutionContext) context;
                    return "QUERY:" + queryContext.getOperation().name();
                }
                if (context instanceof AggregationExecutionContext) {
                    AggregationExecutionContext aggregationContext = (AggregationExecutionContext) context;
                    return "AGGREGATION:" + aggregationContext.getOperation().name();
                }

                return context.getInvocationType().name();
            }
        };
    }
}
