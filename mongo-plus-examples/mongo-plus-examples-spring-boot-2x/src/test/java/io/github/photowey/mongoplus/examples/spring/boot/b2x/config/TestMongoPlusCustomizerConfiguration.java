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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandler;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistryCustomizer;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.method.AbstractMethod;

/**
 * TestMongoPlusCustomizerConfiguration - Test config that registers custom mapper method "findByStatus"
 * via MethodHandlerRegistryCustomizer for CustomMethodMapper integration test.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@Configuration
public class TestMongoPlusCustomizerConfiguration {

    @Bean
    public MethodHandlerRegistryCustomizer findByStatusCustomizer() {
        return registry -> registry.addInjector(new AbstractMethod() {
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
                return (getter, entityClass, args) -> {
                    if (args != null && args.length > 0 && args[0] != null) {
                        return "ok:" + args[0];
                    }
                    return "ok:null";
                };
            }
        });
    }
}
