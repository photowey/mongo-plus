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
package io.github.photowey.mongoplus.autoconfigure.core.proxy.method;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.StringJoiner;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

import lombok.Getter;

/**
 * MapperMethod - Binds a mapper interface method to its execution handler and metadata.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@Getter
public class MapperMethod implements Serializable {

    private static final long serialVersionUID = 9057870512778888541L;

    private final String methodName;
    private final Method method;
    private final String methodKey;
    private final Command command;
    private final MethodHandler handler;

    /**
     * Create by method name (used at inject/register time when Method is not available).
     */
    public MapperMethod(String methodName, Command command, MethodHandler handler) {
        this.methodName = methodName;
        this.method = null;
        this.methodKey = methodName;
        this.command = command;
        this.handler = handler;
    }

    /**
     * Create by Method (used when binding at runtime).
     */
    public MapperMethod(Method method, Command command, MethodHandler handler) {
        this.methodName = Objects.nonNull(method) ? method.getName() : null;
        this.method = method;
        this.methodKey = Objects.nonNull(method) ? signatureKey(method) : this.methodName;
        this.command = command;
        this.handler = handler;
    }

    public MapperMethod bind(Method method) {
        return new MapperMethod(method, this.command, this.handler);
    }

    public static String signatureKey(Method method) {
        StringJoiner joiner = new StringJoiner(",", method.getName() + "(", ")");
        for (Class<?> parameterType : method.getParameterTypes()) {
            joiner.add(parameterType.getName());
        }

        return method.getDeclaringClass().getName() + "#" + joiner;
    }

    // @formatter:off
    
    public String getMethodName() {
        return Strings.isNotEmpty(this.methodName)
            ? this.methodName
            : Objects.nonNull(this.method)
                ? this.method.getName()
                : null;
    }
    // @formatter:on


    public String getMethodKey() {
        return Strings.isNotEmpty(this.methodKey)
            ? this.methodKey
            : this.getMethodName();
    }

    // ----------------------------------------------------------------

    public Method method() {
        return method;
    }

    public Command command() {
        return command;
    }

    public MethodHandler handler() {
        return handler;
    }
}
