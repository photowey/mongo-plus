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
package io.github.photowey.mongoplus.core.id;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * SnowflakeIdGeneratorTest - Tests for Snowflake ID generator.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("SnowflakeIdGeneratorTest")
class SnowflakeIdGeneratorTest {

    @Test
    @DisplayName("Given generator When nextId Then returns monotonic non-null ids")
    @Story("Generate monotonic non-null IDs")
    void givenGenerator_whenNextId_thenReturnsMonotonicNonNullIds() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();

        Long id1 = generator.nextId();
        Long id2 = generator.nextId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(id2 > id1);
    }

    @Test
    @DisplayName("Given many nextId calls When collect Then all ids unique")
    @Story("All IDs unique across many calls")
    void givenManyNextIdCalls_whenCollect_thenAllIdsUnique() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            Long id = generator.nextId();
            assertTrue(ids.add(id), "Duplicate ID generated: " + id);
        }

        assertEquals(10000, ids.size());
    }

    @Test
    @DisplayName("Given concurrent nextId When multiple threads Then no duplicate ids")
    @Story("No duplicate IDs under concurrent access")
    void givenConcurrentNextId_whenMultipleThreads_thenNoDuplicateIds() throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        Set<Long> ids = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        // 10 threads, each generating 1000 IDs
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        Long id = generator.nextId();
                        if (!ids.add(id)) {
                            fail("Duplicate ID in concurrent test: " + id);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(10000, ids.size());
    }

    @Test
    @DisplayName("Given generated id When parseTimestamp Then returns recent LocalDateTime")
    @Story("Parse timestamp from generated ID")
    void givenGeneratedId_whenParseTimestamp_thenReturnsRecentLocalDateTime() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();

        Long id = generator.nextId();
        LocalDateTime timestamp = SnowflakeIdGenerator.parseTimestamp(id);

        assertNotNull(timestamp);
        // Should be recent (within last minute)
        assertTrue(timestamp.isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(timestamp.isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    @DisplayName("Given worker and datacenter id When new generator Then nextId returns positive id")
    @Story("Custom worker and datacenter ID yields positive IDs")
    void givenWorkerAndDatacenterId_whenNewGenerator_thenNextIdReturnsPositiveId() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1L, 1L);

        Long id = generator.nextId();
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("Given invalid worker id When new SnowflakeIdGenerator Then throws IllegalArgumentException")
    @Story("Invalid worker ID throws exception")
    void givenInvalidWorkerId_whenNewSnowflakeIdGenerator_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(0L, 32L);
        });
    }

    @Test
    @DisplayName("Given invalid datacenter id When new SnowflakeIdGenerator Then throws IllegalArgumentException")
    @Story("Invalid datacenter ID throws exception")
    void givenInvalidDatacenterId_whenNewSnowflakeIdGenerator_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SnowflakeIdGenerator(32L, 0L);
        });
    }

    @Test
    @DisplayName("Given 100k nextId When run Then completes within 5 seconds")
    @Story("100k IDs generated within five seconds")
    void given100kNextId_whenRun_thenCompletesWithinFiveSeconds() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        int count = 100000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            generator.nextId();
        }
        long duration = System.currentTimeMillis() - start;

        // Should generate 100k IDs in less than 5 seconds
        assertTrue(duration < 5000);
    }
}
