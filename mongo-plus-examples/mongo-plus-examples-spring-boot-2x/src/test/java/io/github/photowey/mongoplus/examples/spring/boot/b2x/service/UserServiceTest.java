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
package io.github.photowey.mongoplus.examples.spring.boot.b2x.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UserServiceTest - Integration tests with Testcontainers.
 * Each test runs with a clean users collection for isolation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("UserServiceTest")
class UserServiceTest extends AbstractMongoTest {

    @Autowired
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearUsers() {
        this.mongoTemplate.remove(new Query(), UserDocument.class);
    }

    @Test
    @DisplayName("Given new user When save and getById Then returns same data")
    @Description("Integration: UserService.save + getById against Testcontainers MongoDB. Core CRUD path.")
    @Story("Save and retrieve document by id")
    @Severity(SeverityLevel.CRITICAL)
    void givenNewUser_whenSaveAndGetById_thenReturnsSameData() {
        UserDocument user = new UserDocument("Tom", "tom@example.com", 28);
        boolean inserted = this.userService.save(user);
        assertTrue(inserted);
        assertNotNull(user.getId());

        UserDocument found = this.userService.getById(user.getId());
        assertNotNull(found);
        assertEquals("Tom", found.getName());
        assertEquals("tom@example.com", found.getEmail());
        assertEquals(28, found.getAge());
    }

    @Test
    @DisplayName("Given users with same name When findByName Then returns list")
    @Description("Integration: UserService.findByName against MongoDB.")
    @Story("Find documents by name")
    @Severity(SeverityLevel.NORMAL)
    void givenUsersWithSameName_whenFindByName_thenReturnsList() {
        this.userService.save(new UserDocument("Alice", "alice@example.com", 25));
        this.userService.save(new UserDocument("Alice", "alice2@example.com", 30));

        List<UserDocument> list = this.userService.findByName("Alice");
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Given 15 users When page first 10 Then total and pages correct")
    @Description("Integration: UserService.page pagination with total count against MongoDB.")
    @Story("Paginate documents with total count")
    @Severity(SeverityLevel.CRITICAL)
    void given15Users_whenPageFirst10_thenTotalAndPagesCorrect() {
        for (int i = 0; i < 15; i++) {
            this.userService.save(new UserDocument("User" + i, "user" + i + "@example.com", 20 + i));
        }
        Page<UserDocument> page = this.userService.page(1, 10);
        assertEquals(1, page.getCurrent());
        assertEquals(10, page.getSize());
        assertEquals(15, page.getTotal());
        assertEquals(2, page.getPages());
        assertEquals(10, page.getRecords().size());
    }

    @Test
    @DisplayName("Given batch users When insertBatch and deleteBatch Then counts match")
    @Description("Integration: UserService.insertBatch and deleteBatch batch operations against MongoDB.")
    @Story("Batch insert and delete documents")
    @Severity(SeverityLevel.CRITICAL)
    void givenBatchUsers_whenInsertBatchAndDeleteBatch_thenCountsMatch() {
        List<UserDocument> users = Arrays.asList(
            new UserDocument("Batch1", "b1@example.com", 20),
            new UserDocument("Batch2", "b2@example.com", 21),
            new UserDocument("Batch3", "b3@example.com", 22)
        );
        BatchResult insertResult = this.userService.insertBatch(users);
        assertTrue(insertResult.isSuccess());
        assertEquals(3, insertResult.getInsertedCount());

        List<Long> ids = Arrays.asList(users.get(0).getId(), users.get(1).getId(), users.get(2).getId());
        BatchResult deleteResult = this.userService.deleteBatch(ids);
        assertTrue(deleteResult.isSuccess());
        assertEquals(3, deleteResult.getDeletedCount());
    }

    @Test
    @DisplayName("Given users with ages When findByAgeBetween Then returns matching")
    @Description("Integration: UserService.findByAgeBetween range query against MongoDB.")
    @Story("Find documents by age range")
    @Severity(SeverityLevel.NORMAL)
    void givenUsersWithAges_whenFindByAgeBetween_thenReturnsMatching() {
        this.userService.save(new UserDocument("Young", "y@example.com", 18));
        this.userService.save(new UserDocument("Mid", "m@example.com", 25));
        this.userService.save(new UserDocument("Older", "o@example.com", 35));
        List<UserDocument> list = this.userService.findByAgeBetween(20, 30);
        assertEquals(1, list.size());
        assertEquals("Mid", list.get(0).getName());
    }

    @Test
    @DisplayName("Given saved user When updateById Then mapped document is updated")
    @Description("Integration: UserService.updateById full document update against MongoDB.")
    @Story("Update document by id")
    @Severity(SeverityLevel.CRITICAL)
    void givenSavedUser_whenUpdateById_thenMappedDocumentIsUpdated() {
        UserDocument user = new UserDocument("Before", "before@example.com", 18);
        boolean inserted = this.userService.save(user);

        assertTrue(inserted);
        assertNotNull(user.getId());

        user.setName("After");
        user.setEmail("after@example.com");
        user.setAge(20);
        boolean updated = this.userService.updateById(user);

        assertTrue(updated);
        UserDocument found = this.userService.getById(user.getId());
        assertNotNull(found);
        assertEquals("After", found.getName());
        assertEquals("after@example.com", found.getEmail());
        assertEquals(20, found.getAge());
    }

    @Test
    @DisplayName("Given empty then one user When count Then returns 0 then 1")
    @Description("Integration: UserService.count against MongoDB.")
    @Story("Count documents")
    @Severity(SeverityLevel.NORMAL)
    void givenEmptyThenOneUser_whenCount_thenReturns0Then1() {
        assertEquals(0, this.userService.count());
        this.userService.save(new UserDocument("CountUser", "c@example.com", 22));
        assertEquals(1, this.userService.count());
    }
}
