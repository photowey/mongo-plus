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
package io.github.photowey.mongoplus.examples.spring.boot.b2x;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

/**
 * AppTests - Smoke tests for the Spring Boot 2.x example application context.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@SpringBootTest
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AppTests")
class AppTests extends AbstractMongoTest {

    @Test
    @DisplayName("Given Spring Boot app When context loads Then succeeds")
    @Story("Spring Boot context loads successfully")
    void givenSpringBootApp_whenContextLoads_thenSucceeds() {
        Assertions.assertTrue(true);
    }
}
