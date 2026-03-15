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
package io.github.photowey.mongoplus.autoconfigure.core.property;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MongoPlusPropertiesTest - Unit tests for MongoPlus properties option accessors.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoPlusPropertiesTest")
class MongoPlusPropertiesTest {

    @Test
    @DisplayName(
        "Given configured option values, when reading fluent accessors, then returns the stored "
            + "configuration"
    )
    @Story("Configured options return stored configuration via fluent accessors")
    void givenConfiguredOptionValues_whenReadingFluentAccessors_thenReturnsTheStoredConfiguration() {
        // Given
        MongoPlusProperties.Option option = MongoPlusProperties.Option.builder()
            .enabled(true)
            .minConnectionPerHost(1)
            .maxConnectionPerHost(200)
            .threadsAllowedToBlockForConnectionMultiplier(8)
            .serverSelectionTimeout(40000)
            .maxWaitTime(130000)
            .maxConnectionIdleTime(1000)
            .maxConnectionLifeTime(2000)
            .connectTimeout(3000)
            .socketTimeout(4000)
            .socketKeepAlive(true)
            .sslEnabled(true)
            .sslInvalidHostNameAllowed(true)
            .alwaysUseMBeans(true)
            .heartbeatFrequency(5000)
            .minHeartbeatFrequency(600)
            .heartbeatConnectTimeout(7000)
            .heartbeatSocketTimeout(8000)
            .localThreshold(20)
            .build();

        // When
        boolean enabled = option.enabled();

        // Then
        assertTrue(enabled);
        assertEquals(1, option.minConnectionPerHost());
        assertEquals(200, option.maxConnectionPerHost());
        assertEquals(8, option.threadsAllowedToBlockForConnectionMultiplier());
        assertEquals(40000, option.serverSelectionTimeout());
        assertEquals(130000, option.maxWaitTime());
        assertEquals(1000, option.maxConnectionIdleTime());
        assertEquals(2000, option.maxConnectionLifeTime());
        assertEquals(3000, option.connectTimeout());
        assertEquals(4000, option.socketTimeout());
        assertTrue(option.socketKeepAlive());
        assertTrue(option.sslEnabled());
        assertTrue(option.sslInvalidHostNameAllowed());
        assertTrue(option.alwaysUseMBeans());
        assertEquals(5000, option.heartbeatFrequency());
        assertEquals(600, option.minHeartbeatFrequency());
        assertEquals(7000, option.heartbeatConnectTimeout());
        assertEquals(8000, option.heartbeatSocketTimeout());
        assertEquals(20, option.localThreshold());
    }

    @Test
    @DisplayName(
        "Given default option values, when reading the enabled flag, then reports disabled by "
            + "default"
    )
    @Story("Default enabled flag reports disabled")
    void givenDefaultOptionValues_whenReadingTheEnabledFlag_thenReportsDisabledByDefault() {
        // Given
        MongoPlusProperties.Option option = new MongoPlusProperties.Option();

        // When
        boolean enabled = option.enabled();

        // Then
        assertFalse(enabled);
    }
}
