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

import io.github.photowey.mongoplus.examples.spring.boot.b3x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.annotated.AnnotationOnlyMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * AnnotationOnlyMapperTest - Integration tests for AnnotationOnlyMapper (@MongoMapper only, no MongoMapper extend).
 * Verifies annotation-only interfaces are discovered and registered.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AnnotationOnlyMapperTest")
class AnnotationOnlyMapperTest extends AbstractMongoTest {

    @Autowired(required = false)
    private AnnotationOnlyMapper annotationOnlyMapper;

    @Test
    @DisplayName("Given context When get AnnotationOnlyMapper Then bean is present")
    @Story("Annotation-only mapper bean registration")
    void givenContext_whenGetAnnotationOnlyMapper_thenBeanIsPresent() {
        assertNotNull(
            this.annotationOnlyMapper,
            "AnnotationOnlyMapper should be registered (annotation-only, no CRUD methods)"
        );
    }
}
