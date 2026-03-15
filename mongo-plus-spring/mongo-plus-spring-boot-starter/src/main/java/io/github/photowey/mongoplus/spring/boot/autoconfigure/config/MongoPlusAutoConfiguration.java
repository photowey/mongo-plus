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
package io.github.photowey.mongoplus.spring.boot.autoconfigure.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import io.github.photowey.mongoplus.autoconfigure.config.AbstractMongoPlusConfiguration;

// @formatter:off

/**
 * MongoPlusAutoConfiguration - Auto configuration for Spring Boot 2.7+ and 3.x.
 * Uses @AutoConfiguration instead of @Configuration.
 *
 * <p>
 * This configuration is activated when @AutoConfiguration is available (Spring Boot >= 2.7).
 * MongoPlusProperties is provided and bound via parent's @Bean + @ConfigurationProperties.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@AutoConfiguration(after = MongoAutoConfiguration.class)
@ConditionalOnClass(AutoConfiguration.class)
public class MongoPlusAutoConfiguration extends AbstractMongoPlusConfiguration { }

// @formatter:on
