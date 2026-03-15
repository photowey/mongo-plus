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
package io.github.photowey.mongoplus.wrapper.tool;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaUpdateWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WrappersTest - Tests for Wrappers utility class.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("WrappersTest")
class WrappersTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 6213139872200932559L;

        private String name;
        private Integer age;
    }

    @Test
    @DisplayName("Given no args or class When query Then returns empty or typed wrapper")
    @Story("Create empty or typed query wrapper")
    void givenNoArgsOrClass_whenQuery_thenReturnsEmptyOrTypedWrapper() {
        QueryWrapper<User> wrapper = Wrappers.query();
        assertNotNull(wrapper);
        assertTrue(Wrappers.isEmpty(wrapper));

        QueryWrapper<User> wrapper2 = Wrappers.query(User.class);
        assertNotNull(wrapper2);
        assertEquals(User.class, wrapper2.getEntityClass());
    }

    @Test
    @DisplayName("Given no args or class When lambdaQuery Then returns empty or typed wrapper")
    @Story("Create empty or typed lambda query wrapper")
    void givenNoArgsOrClass_whenLambdaQuery_thenReturnsEmptyOrTypedWrapper() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        assertNotNull(wrapper);
        assertTrue(Wrappers.isEmpty(wrapper));

        LambdaQueryWrapper<User> wrapper2 = Wrappers.lambdaQuery(User.class);
        assertNotNull(wrapper2);
        assertEquals(User.class, wrapper2.getEntityClass());
    }

    @Test
    @DisplayName("Given no args When update Then returns empty update wrapper")
    @Story("Create empty update wrapper")
    void givenNoArgs_whenUpdate_thenReturnsEmptyUpdateWrapper() {
        UpdateWrapper<User> wrapper = Wrappers.update();
        assertNotNull(wrapper);
        assertTrue(Wrappers.isEmpty(wrapper));
    }

    @Test
    @DisplayName("Given no args When lambdaUpdate Then returns empty lambda update wrapper")
    @Story("Create empty lambda update wrapper")
    void givenNoArgs_whenLambdaUpdate_thenReturnsEmptyLambdaUpdateWrapper() {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        assertNotNull(wrapper);
        assertTrue(Wrappers.isEmpty(wrapper));
    }

    @Test
    @DisplayName("Given emptyWrapper When get or modify Then isEmpty and modification throws")
    @Story("Empty wrapper getOrModify throws on modification")
    void givenEmptyWrapper_whenGetOrModify_thenIsEmptyAndModificationThrows() {
        QueryWrapper<User> empty = Wrappers.emptyWrapper();
        assertNotNull(empty);
        assertTrue(Wrappers.isEmpty(empty));

        // Empty wrapper should throw on modification
        assertThrows(UnsupportedOperationException.class, () ->
            empty.eq("name", "test")
        );
    }

    @Test
    @DisplayName("Given wrapper When isEmpty or isNotEmpty Then returns correct state")
    @Story("Check wrapper empty or not empty state")
    void givenWrapper_whenIsEmptyOrIsNotEmpty_thenReturnsCorrectState() {
        QueryWrapper<User> wrapper = Wrappers.query();
        assertTrue(Wrappers.isEmpty(wrapper));
        assertFalse(Wrappers.isNotEmpty(wrapper));

        wrapper.eq("name", "test");
        assertFalse(Wrappers.isEmpty(wrapper));
        assertTrue(Wrappers.isNotEmpty(wrapper));
    }

    @Test
    @DisplayName("Given entity When query with entity Then returns wrapper with entity class")
    @Story("Query wrapper with entity class")
    void givenEntity_whenQueryWithEntity_thenReturnsWrapperWithEntityClass() {
        User user = new User();
        user.setName("test");
        user.setAge(18);

        QueryWrapper<User> wrapper = Wrappers.query(user);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }

    @Test
    @DisplayName("Given entity When lambdaQuery with entity Then returns wrapper with entity class")
    @Story("Lambda query wrapper with entity class")
    void givenEntity_whenLambdaQueryWithEntity_thenReturnsWrapperWithEntityClass() {
        User user = new User();
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(user);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }

    @Test
    @DisplayName("Given class When update with class Then returns wrapper with entity class")
    @Story("Update wrapper with entity class")
    void givenClass_whenUpdateWithClass_thenReturnsWrapperWithEntityClass() {
        UpdateWrapper<User> wrapper = Wrappers.update(User.class);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }

    @Test
    @DisplayName("Given entity When update with entity Then returns wrapper with entity class")
    @Story("Update wrapper with entity instance")
    void givenEntity_whenUpdateWithEntity_thenReturnsWrapperWithEntityClass() {
        User user = new User();
        user.setName("test");
        UpdateWrapper<User> wrapper = Wrappers.update(user);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }

    @Test
    @DisplayName("Given class When lambdaUpdate with class Then returns wrapper with entity class")
    @Story("Lambda update wrapper with entity class")
    void givenClass_whenLambdaUpdateWithClass_thenReturnsWrapperWithEntityClass() {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }

    @Test
    @DisplayName("Given entity When lambdaUpdate with entity Then returns wrapper with entity class")
    @Story("Lambda update wrapper with entity instance")
    void givenEntity_whenLambdaUpdateWithEntity_thenReturnsWrapperWithEntityClass() {
        User user = new User();
        user.setAge(18);
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(user);
        assertNotNull(wrapper);
        assertEquals(User.class, wrapper.getEntityClass());
    }
}
