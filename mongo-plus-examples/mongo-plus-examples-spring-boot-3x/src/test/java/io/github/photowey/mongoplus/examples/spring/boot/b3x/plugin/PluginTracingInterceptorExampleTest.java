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
package io.github.photowey.mongoplus.examples.spring.boot.b3x.plugin;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.photowey.mongoplus.examples.spring.boot.b3x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.service.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PluginTracingInterceptorExampleTest - Demonstrates the tracing interceptor example in the Spring Boot sample module.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@SpringBootTest
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("PluginTracingInterceptorExampleTest")
class PluginTracingInterceptorExampleTest extends AbstractMongoTest {

    @Autowired
    private UserService userService;

    @Autowired
    private List<String> tracingEvents;

    @BeforeEach
    void clearTracingEvents() {
        this.tracingEvents.clear();
    }

    @Test
    @DisplayName(
        "Given tracing interceptor example, "
            + "when saving and counting users, then mapper and query events are recorded"
    )
    @Story("Tracing interceptor records mapper and query events")
    void givenTracingInterceptorExample_whenSavingAndCountingUsers_thenMapperAndQueryEventsAreRecorded() {
        // Given
        UserDocument user = new UserDocument("Trace", "trace@example.com", 28);

        // When
        this.userService.save(user);
        this.userService.count();

        // Then
        assertTrue(this.tracingEvents.stream().anyMatch(it -> it.equals("MAPPER:insert:before")));
        assertTrue(this.tracingEvents.stream().anyMatch(it -> it.equals("QUERY:INSERT:before")));
        assertTrue(this.tracingEvents.stream().anyMatch(it -> it.equals("MAPPER:selectCount:before")));
        assertTrue(this.tracingEvents.stream().anyMatch(it -> it.equals("QUERY:SELECT_COUNT:before")));
    }
}
