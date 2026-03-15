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
package io.github.photowey.mongoplus.autoconfigure.core.proxy.handler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.injector.method.BuiltinMethodInjector;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.MapperMethod;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

/**
 * MethodHandlerRegistry - Registry for mapper methods.
 * All methods (built-in and custom) are stored as {@link MapperMethod}.
 * Supports built-in via {@link BuiltinMethodInjector} and custom via {@link AbstractMethod}.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MethodHandlerRegistry {

    private final Map<String, MapperMethod> mapperMethodTemplates = new ConcurrentHashMap<>();
    private final Map<String, MapperMethod> boundMapperMethods = new ConcurrentHashMap<>();
    private final BuiltinMethodInjector methodInjector;

    public MethodHandlerRegistry() {
        this.methodInjector = new BuiltinMethodInjector();
        this.methodInjector.inject(this);
    }

    /**
     * Register a mapper method (unified entry for all methods).
     */
    public void register(MapperMethod mapperMethod) {
        if (Objects.isNull(mapperMethod)) {
            return;
        }
        if (Objects.nonNull(mapperMethod.getMethod())) {
            this.boundMapperMethods.put(mapperMethod.getMethodKey(), mapperMethod);
            return;
        }
        if (Strings.isNotEmpty(mapperMethod.getMethodName())) {
            this.mapperMethodTemplates.put(mapperMethod.getMethodName(), mapperMethod);
        }
    }

    /**
     * Register by name + command + handler (compatibility path).
     */
    public void register(String methodName, Command command, MethodHandler handler) {
        if (Strings.isNotEmpty(methodName) && Objects.nonNull(handler)) {
            this.register(new MapperMethod(methodName, Objects.defaultIfNull(command, Command.SELECT), handler));
        }
    }

    /**
     * Register by name + handler (compatibility path).
     */
    public void register(String methodName, MethodHandler handler) {
        this.register(methodName, Command.SELECT, handler);
    }

    /**
     * Register by reflected Method + command + handler.
     */
    public void register(Method method, Command command, MethodHandler handler) {
        if (Objects.nonNull(method) && Objects.nonNull(handler)) {
            this.register(new MapperMethod(method, Objects.defaultIfNull(command, Command.SELECT), handler));
        }
    }

    /**
     * Register by reflected Method + handler.
     */
    public void register(Method method, MethodHandler handler) {
        this.register(method, Command.SELECT, handler);
    }

    /**
     * Get mapper method for reflected method.
     */
    public MapperMethod getMapperMethod(Method method) {
        if (Objects.isNull(method) || method.isDefault()) {
            return null;
        }

        String methodKey = MapperMethod.signatureKey(method);
        MapperMethod bound = this.boundMapperMethods.get(methodKey);
        if (Objects.nonNull(bound)) {
            return bound;
        }

        MapperMethod template = this.mapperMethodTemplates.get(method.getName());
        if (Objects.isNull(template)) {
            return null;
        }

        MapperMethod resolved = template.bind(method);
        MapperMethod existing = this.boundMapperMethods.putIfAbsent(methodKey, resolved);
        return Objects.nonNull(existing) ? existing : resolved;
    }

    /**
     * Get handler for method (delegates to MapperMethod).
     */
    public MethodHandler getHandler(Method method) {
        MapperMethod mapperMethod = this.getMapperMethod(method);
        return Objects.nonNull(mapperMethod) ? mapperMethod.handler() : null;
    }

    /**
     * Check if a mapper method exists.
     */
    public boolean hasHandler(Method method) {
        return Objects.nonNull(this.getMapperMethod(method));
    }

    /**
     * Get registered mapper method templates by method name.
     */
    public Map<String, MapperMethod> getMapperMethods() {
        return new HashMap<>(this.mapperMethodTemplates);
    }

    /**
     * Get runtime-bound mapper methods by signature.
     */
    public Map<String, MapperMethod> getBoundMapperMethods() {
        return new HashMap<>(this.boundMapperMethods);
    }

    /**
     * Get all handlers (name -> handler) for backward compatibility.
     */
    public Map<String, MethodHandler> getHandlers() {
        Map<String, MethodHandler> out = new HashMap<>();
        for (Map.Entry<String, MapperMethod> entry : this.mapperMethodTemplates.entrySet()) {
            if (Objects.nonNull(entry.getValue().handler())) {
                out.put(entry.getKey(), entry.getValue().handler());
            }
        }
        return out;
    }

    /**
     * Add a custom method injector (MyBatis-Plus style). The injector will register its handler(s) into this registry.
     *
     * @param injector the abstract method injector
     */
    public void addInjector(AbstractMethod injector) {
        if (Objects.nonNull(injector)) {
            injector.inject(this);
        }
    }

    /**
     * Add multiple custom method injectors.
     *
     * @param injectors the injectors to add
     */
    public void addInjectors(Collection<? extends AbstractMethod> injectors) {
        if (Objects.nonNull(injectors) && Collections.isNotEmpty(injectors)) {
            for (AbstractMethod injector : injectors) {
                this.addInjector(injector);
            }
        }
    }
}
