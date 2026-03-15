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
package io.github.photowey.mongoplus.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ContractMongoMapperTest")
class ContractMongoMapperTest {

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends Mapper")
    @Story("MongoMapper extends base Mapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsMapper() {
        assertTrue(Mapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends SelectMapper")
    @Story("MongoMapper extends SelectMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsSelectMapper() {
        assertTrue(SelectMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends InsertMapper")
    @Story("MongoMapper extends InsertMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsInsertMapper() {
        assertTrue(InsertMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends UpdateMapper")
    @Story("MongoMapper extends UpdateMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsUpdateMapper() {
        assertTrue(UpdateMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends DeleteMapper")
    @Story("MongoMapper extends DeleteMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsDeleteMapper() {
        assertTrue(DeleteMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends BatchMapper")
    @Story("MongoMapper extends BatchMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsBatchMapper() {
        assertTrue(BatchMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given MongoMapper When check inheritance Then extends AggregateMapper")
    @Story("MongoMapper extends AggregateMapper interface")
    void givenMongoMapper_whenCheckInheritance_thenExtendsAggregateMapper() {
        assertTrue(AggregateMapper.class.isAssignableFrom(MongoMapper.class));
    }

    @Test
    @DisplayName("Given Mapper interface When getDeclaredMethods Then has no methods")
    @Story("Mapper interface has no declared methods")
    void givenMapperInterface_whenGetDeclaredMethods_thenHasNoMethods() {
        assertTrue(Mapper.class.getDeclaredMethods().length == 0);
    }
}
