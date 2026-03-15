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
package io.github.photowey.mongoplus.examples.spring.boot.b2x.mapper;

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

import io.github.photowey.mongoplus.examples.spring.boot.b2x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.GeneratedUserDocument;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.GeneratedUserDocumentAutoMapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GeneratedAutoMapperTest - Verifies generated auto mapper integration for Boot 2.x.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("GeneratedAutoMapperTest")
class GeneratedAutoMapperTest extends AbstractMongoTest {

    @Autowired
    private GeneratedUserDocumentAutoMapper generatedUserDocumentAutoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearGeneratedUsers() {
        this.mongoTemplate.remove(new Query(), GeneratedUserDocument.class);
    }

    @Test
    @DisplayName("Given generated auto mapper When insert and selectById Then generated bean works")
    @Story("Generated mapper insert and select by id")
    void givenGeneratedAutoMapper_whenInsertAndSelectById_thenGeneratedBeanWorks() {
        GeneratedUserDocument document = GeneratedUserDocument.builder()
            .name("generated-boot2")
            .age(18)
            .build();

        boolean inserted = this.generatedUserDocumentAutoMapper.insert(document);

        assertTrue(inserted);
        assertNotNull(document.getId());

        GeneratedUserDocument found = this.generatedUserDocumentAutoMapper.selectById(document.getId());
        assertNotNull(found);
        assertEquals("generated-boot2", found.getName());
        assertEquals(18, found.getAge());
    }
}
