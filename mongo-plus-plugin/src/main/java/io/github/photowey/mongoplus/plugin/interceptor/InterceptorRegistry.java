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
package io.github.photowey.mongoplus.plugin.interceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.plugin.context.InvocationContext;

/**
 * InterceptorRegistry - Stores ordered interceptors and executes them as a reusable invocation chain.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * InterceptorRegistry registry = new InterceptorRegistry(interceptors);
 * Object result = registry.invoke(context, () -> delegate.execute());
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
public final class InterceptorRegistry {

    private final List<MongoPlusInterceptor> interceptors;

    /**
     * Creates a registry from the supplied interceptor list.
     *
     * @param interceptors the interceptors to register
     */
    public InterceptorRegistry(List<? extends MongoPlusInterceptor> interceptors) {
        this.interceptors = this.prepare(interceptors);
    }

    /**
     * Creates an empty registry.
     *
     * @return an empty registry
     */
    public static InterceptorRegistry empty() {
        return new InterceptorRegistry(Collections.emptyList());
    }

    /**
     * Returns the registered interceptors in execution order.
     *
     * @return the ordered interceptor list
     */
    public List<MongoPlusInterceptor> getInterceptors() {
        return this.interceptors;
    }

    /**
     * Determines whether the registry contains no interceptors.
     *
     * @return {@code true} when the registry is empty
     */
    public boolean isEmpty() {
        return Collections.isEmpty(this.interceptors);
    }

    /**
     * Invokes the interceptor chain for the supplied context and terminal target.
     *
     * @param context the invocation context
     * @param target  the terminal action
     * @return the invocation result
     * @throws Throwable when execution fails
     */
    public Object invoke(InvocationContext context, InvocationTarget target) throws Throwable {
        if (Objects.isNull(target)) {
            throw new IllegalArgumentException("Invocation target must not be null");
        }

        List<MongoPlusInterceptor> matched = new ArrayList<>();
        for (MongoPlusInterceptor interceptor : this.interceptors) {
            if (interceptor.supports(context)) {
                matched.add(interceptor);
            }
        }

        return new DefaultInvocation(context, target, matched).proceed();
    }

    private List<MongoPlusInterceptor> prepare(List<? extends MongoPlusInterceptor> interceptors) {
        if (Collections.isEmpty(interceptors)) {
            return Collections.unmodifiableList(Collections.emptyList());
        }

        List<MongoPlusInterceptor> ordered = new ArrayList<>();
        for (MongoPlusInterceptor interceptor : interceptors) {
            if (Objects.nonNull(interceptor)) {
                ordered.add(interceptor);
            }
        }
        ordered.sort(Comparator.comparingInt(MongoPlusInterceptor::getOrder));

        return Collections.unmodifiableList(ordered);
    }

    private static final class DefaultInvocation implements Invocation {

        private final InvocationContext context;
        private final InvocationTarget target;
        private final List<MongoPlusInterceptor> interceptors;
        private int cursor;

        private DefaultInvocation(
            InvocationContext context,
            InvocationTarget target,
            List<MongoPlusInterceptor> interceptors
        ) {
            this.context = context;
            this.target = target;
            this.interceptors = interceptors;
        }

        @Override
        public InvocationContext getContext() {
            return this.context;
        }

        @Override
        public Object proceed() throws Throwable {
            if (this.cursor >= this.interceptors.size()) {
                return this.target.proceed();
            }

            MongoPlusInterceptor interceptor = this.interceptors.get(this.cursor++);
            return interceptor.intercept(this);
        }
    }
}
