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

import io.github.photowey.mongoplus.examples.spring.boot.b3x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * UserMapperTest - Integration tests for UserMapper (discovered by @MongoMapperScan).
 * Clears users collection before each test, consistent with other integration tests.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("UserMapperTest")
class UserMapperTest extends AbstractMongoTest {

    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearUsers() {
        this.mongoTemplate.remove(new Query(), UserDocument.class);
    }

    @Test
    @DisplayName("Given context When get UserMapper Then bean is present (@MongoMapperScan)")
    @Description("Integration: verifies UserMapper bean is registered via @MongoMapperScan.")
    @Story("Mapper bean registration via scanner")
    @Severity(SeverityLevel.CRITICAL)
    void givenContext_whenGetUserMapper_thenBeanIsPresent() {
        assertNotNull(
            this.userMapper,
            "UserMapper should be registered by @MongoMapperScan (mapper package)"
        );
    }

    @Test
    @DisplayName("Given inserted user When selectList Then returns list with one item")
    @Description("Integration: UserMapper.insert + selectList against MongoDB. Core mapper CRUD path.")
    @Story("Insert and select list via mapper")
    @Severity(SeverityLevel.CRITICAL)
    void givenInsertedUser_whenSelectList_thenReturnsListWithOneItem() {
        assertNotNull(this.userMapper);
        UserDocument user = new UserDocument("MapperUser", "mapper@example.com", 30);
        this.userMapper.insert(user);
        assertNotNull(user.getId());

        List<UserDocument> list = this.userMapper.selectList(Wrappers.lambdaQuery(UserDocument.class));
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("MapperUser", list.get(0).getName());
        assertEquals("mapper@example.com", list.get(0).getEmail());
        assertEquals(30, list.get(0).getAge());
    }
}
