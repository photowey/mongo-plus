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
package io.github.photowey.mongoplus.autoconfigure.core.proxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;
import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.MapperMethod;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.mapper.MongoMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MethodHandlerRegistryTest - Tests for MethodHandlerRegistry and AbstractMethod injector support.
 * All methods are stored and dispatched as MapperMethod.
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MethodHandlerRegistryTest")
class MethodHandlerRegistryTest {

    private static final Set<String> BUILTIN_METHOD_NAMES = Set.of(
        "selectById", "selectList", "selectOne", "selectCount", "exists", "selectPage",
        "aggregate",
        "insert", "updateById", "update", "deleteById", "delete",
        "insertBatch", "updateBatch", "deleteBatch"
    );

    @Test
    @DisplayName("Given new registry When getMapperMethods Then contains all fifteen built-in method names")
    @Story("New registry contains all fifteen built-in methods")
    void givenNewRegistry_whenGetMapperMethods_thenContainsAllFifteenBuiltinMethods() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Map<String, MapperMethod> methods = registry.getMapperMethods();
        Set<String> registered = methods.keySet();
        for (String name : BUILTIN_METHOD_NAMES) {
            assertTrue(registered.contains(name), "Missing built-in method: " + name);
        }
        assertEquals(
            BUILTIN_METHOD_NAMES.size(),
            registered.size(),
            "Registry should have exactly " + BUILTIN_METHOD_NAMES.size() + " built-in methods; got: " + registered
        );
    }

    @Test
    @DisplayName(
        "Given new registry When getHandlers Then contains built-in selectById "
            + "selectList insert deleteById aggregate"
    )
    @Story("New registry contains built-in handlers")
    void givenNewRegistry_whenGetHandlers_thenContainsBuiltinMethods() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        assertTrue(registry.getHandlers().containsKey("selectById"));
        assertTrue(registry.getHandlers().containsKey("selectList"));
        assertTrue(registry.getHandlers().containsKey("insert"));
        assertTrue(registry.getHandlers().containsKey("deleteById"));
        assertTrue(registry.getHandlers().containsKey("aggregate"));
    }

    @Test
    @DisplayName("Given registry When getMapperMethod selectById Then returns MapperMethod with SELECT command")
    @Story("Registry returns selectById mapper method with SELECT command")
    void givenRegistry_whenGetMapperMethodSelectById_thenReturnsMapperMethodWithSelectCommand() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Map<String, MapperMethod> methods = registry.getMapperMethods();
        assertTrue(methods.containsKey("selectById"));
        assertEquals(Command.SELECT, methods.get("selectById").getCommand());
    }

    @Test
    @DisplayName("Given registry When getMapperMethod insert Then returns MapperMethod with INSERT command")
    @Story("Registry returns insert mapper method with INSERT command")
    void givenRegistry_whenGetMapperMethodInsert_thenReturnsMapperMethodWithInsertCommand() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Map<String, MapperMethod> methods = registry.getMapperMethods();
        assertTrue(methods.containsKey("insert"));
        assertEquals(Command.INSERT, methods.get("insert").getCommand());
    }

    @Test
    @DisplayName("Given registry When getMapperMethod deleteById Then returns MapperMethod with DELETE command")
    @Story("Registry returns deleteById mapper method with DELETE command")
    void givenRegistry_whenGetMapperMethodDeleteById_thenReturnsMapperMethodWithDeleteCommand() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Map<String, MapperMethod> methods = registry.getMapperMethods();
        assertTrue(methods.containsKey("deleteById"));
        assertEquals(Command.DELETE, methods.get("deleteById").getCommand());
    }

    @Test
    @DisplayName("Given registry When getMapperMethod aggregate Then returns MapperMethod with SELECT command")
    @Story("Registry returns aggregate mapper method with SELECT command")
    void givenRegistry_whenGetMapperMethodAggregate_thenReturnsMapperMethodWithSelectCommand() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Map<String, MapperMethod> methods = registry.getMapperMethods();
        assertTrue(methods.containsKey("aggregate"));
        assertEquals(Command.SELECT, methods.get("aggregate").getCommand());
    }

    @Test
    @DisplayName("Given registry When invoke aggregate handler Then returns aggregation executor result")
    @Story("Invoke aggregate handler returns aggregation executor result")
    void givenRegistry_whenInvokeAggregateHandler_thenReturnsAggregationExecutorResult() throws Exception {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Method aggregateMethod = MongoMapper.class.getMethod("aggregate", AggregationWrapper.class, Class.class);
        assertTrue(registry.hasHandler(aggregateMethod));

        MapperMethod mapperMethod = registry.getMapperMethod(aggregateMethod);
        assertNotNull(mapperMethod);
        assertEquals("aggregate", mapperMethod.getMethodName());
        assertEquals(Command.SELECT, mapperMethod.getCommand());
        assertEquals(aggregateMethod, mapperMethod.getMethod());
        assertTrue(registry.getBoundMapperMethods().containsKey(MapperMethod.signatureKey(aggregateMethod)));

        List<String> expected = Collections.singletonList("agg-result");
        ExecutorGetter getter = new ExecutorGetter() {
            @Override
            public QueryExecutor queryExecutor() {
                return null;
            }

            @Override
            public AggregationExecutor aggregationExecutor() {
                return new AggregationExecutor() {
                    @Override
                    public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
                        return (List<R>) expected;
                    }

                    @Override
                    public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
                        return (List<R>) expected;
                    }

                    @Override
                    public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
                        return (List<R>) expected;
                    }
                };
            }
        };
        AggregationWrapper<Object> wrapper = AggregationWrapper.aggregation(Object.class);
        Object result = mapperMethod.getHandler().execute(
            getter,
            Object.class,
            new Object[] {wrapper, String.class}
        );
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) result;
        assertEquals(expected, list);
    }

    @Test
    @DisplayName("Given name-based custom method When resolved twice Then signature-bound mapper method is cached")
    @Story("Name-based custom method caches signature-bound mapper method")
    void givenNameBasedCustomMethod_whenResolvedTwice_thenSignatureBoundMapperMethodIsCached() throws Exception {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        registry.register(
            "findByStatus",
            Command.SELECT,
            (getter, entityClass, args) -> "ok:" + args[0]
        );
        Method method = CustomMapper.class.getMethod("findByStatus", String.class);

        MapperMethod first = registry.getMapperMethod(method);
        MapperMethod second = registry.getMapperMethod(method);

        assertNotNull(first);
        assertSame(first, second);
        assertEquals(method, first.getMethod());
        assertTrue(registry.getBoundMapperMethods().containsKey(MapperMethod.signatureKey(method)));
    }

    @Test
    @DisplayName(
        "Given overloaded methods When registered by Method Then registry resolves each "
            + "signature independently"
    )
    @Story("Registry resolves overloaded methods by signature independently")
    void givenOverloadedMethods_whenRegisteredByMethod_thenRegistryResolvesEachSignatureIndependently()
        throws Exception {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Method stringMethod = OverloadedMapper.class.getMethod("findByStatus", String.class);
        Method integerMethod = OverloadedMapper.class.getMethod("findByStatus", Integer.class);

        registry.register(
            stringMethod,
            Command.SELECT,
            (getter, entityClass, args) -> "string:" + args[0]
        );
        registry.register(
            integerMethod,
            Command.SELECT,
            (getter, entityClass, args) -> "integer:" + args[0]
        );

        Object stringResult = registry.getHandler(stringMethod).execute(
            null,
            Object.class,
            new Object[] {"active"}
        );
        Object integerResult = registry.getHandler(integerMethod).execute(
            null,
            Object.class,
            new Object[] {7}
        );

        assertEquals("string:active", stringResult);
        assertEquals("integer:7", integerResult);
    }

    @Test
    @DisplayName("Given custom AbstractMethod When addInjector Then registry has MapperMethod and handler executes")
    @Story("Add injector for custom abstract method registers and executes")
    void givenCustomAbstractMethod_whenAddInjector_thenRegistryHasMapperMethodAndHandlerExecutes() throws Exception {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Method customMethod = CustomMapper.class.getMethod("findByStatus", String.class);
        assertNull(registry.getMapperMethod(customMethod));

        registry.addInjector(new AbstractMethod() {
            @Override
            public String getMethodName() {
                return "findByStatus";
            }

            @Override
            protected Command getCommand() {
                return Command.SELECT;
            }

            @Override
            protected MethodHandler createHandler() {
                return (getter, entityClass, args) -> "ok:" + args[0];
            }
        });

        MapperMethod mapperMethod = registry.getMapperMethod(customMethod);
        assertNotNull(mapperMethod);
        assertEquals(Command.SELECT, mapperMethod.getCommand());
        Object result = mapperMethod.getHandler().execute(null, Object.class, new Object[] {"active"});
        assertEquals("ok:active", result);
    }

    @Test
    @DisplayName("Given multiple AbstractMethods When addInjectors Then all are registered")
    @Story("Add injectors for multiple abstract methods registers all")
    void givenMultipleAbstractMethods_whenAddInjectors_thenAllAreRegistered() {
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        registry.addInjectors(List.of(
            new AbstractMethod() {
                @Override
                public String getMethodName() {
                    return "customA";
                }

                @Override
                protected Command getCommand() {
                    return Command.SELECT;
                }

                @Override
                protected MethodHandler createHandler() {
                    return (g, c, a) -> "A";
                }
            },
            new AbstractMethod() {
                @Override
                public String getMethodName() {
                    return "customB";
                }

                @Override
                protected Command getCommand() {
                    return Command.SELECT;
                }

                @Override
                protected MethodHandler createHandler() {
                    return (g, c, a) -> "B";
                }
            }
        ));
        assertTrue(registry.getHandlers().containsKey("customA"));
        assertTrue(registry.getHandlers().containsKey("customB"));
    }

    interface CustomMapper {
        Object findByStatus(String status);
    }

    interface OverloadedMapper {
        Object findByStatus(String status);

        Object findByStatus(Integer status);
    }
}
