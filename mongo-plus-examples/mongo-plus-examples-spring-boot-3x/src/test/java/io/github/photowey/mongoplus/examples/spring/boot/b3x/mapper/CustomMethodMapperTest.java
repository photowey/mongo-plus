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
package io.github.photowey.mongoplus.examples.spring.boot.b3x.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.github.photowey.mongoplus.examples.spring.boot.b3x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.annotated.CustomMethodMapper;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.config.TestMongoPlusCustomizerConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CustomMethodMapperTest - Integration tests for custom mapper method via MethodHandlerRegistryCustomizer.
 * and AbstractMethod injector (findByStatus).
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SpringBootTest
@Import(TestMongoPlusCustomizerConfig.class)
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("CustomMethodMapperTest")
class CustomMethodMapperTest extends AbstractMongoTest {

    @Autowired(required = false)
    private CustomMethodMapper customMethodMapper;

    @Test
    @DisplayName("Given customizer When get CustomMethodMapper Then bean is present")
    @Story("Custom mapper bean registration via MethodHandlerRegistryCustomizer")
    void givenCustomizer_whenGetCustomMethodMapper_thenBeanIsPresent() {
        assertNotNull(this.customMethodMapper, "CustomMethodMapper should be registered");
    }

    @Test
    @DisplayName("Given custom findByStatus When call Then returns handler result")
    @Story("Invoke custom method and return handler result")
    void givenCustomFindByStatus_whenCall_thenReturnsHandlerResult() {
        assertNotNull(this.customMethodMapper);
        String out = this.customMethodMapper.findByStatus("active");
        assertEquals("ok:active", out);
    }

    @Test
    @DisplayName("Given null arg When call findByStatus Then returns ok:null")
    @Story("Custom method with null argument returns OK null")
    void givenNullArg_whenCallFindByStatus_thenReturnsOkNull() {
        assertNotNull(this.customMethodMapper);
        String out = this.customMethodMapper.findByStatus(null);
        assertEquals("ok:null", out);
    }
}
