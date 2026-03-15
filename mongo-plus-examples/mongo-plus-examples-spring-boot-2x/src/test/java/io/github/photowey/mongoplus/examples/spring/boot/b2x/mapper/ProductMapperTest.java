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

import java.math.BigDecimal;
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

import io.github.photowey.mongoplus.examples.spring.boot.b2x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.annotated.ProductMapper;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.ProductDocument;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ProductMapperTest - Unit/Integration tests for ProductMapper discovered by property config
 * and annotated with {@code @MongoMapper}.
 * Clears products collection before each test, consistent with other integration tests.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ProductMapperTest")
class ProductMapperTest extends AbstractMongoTest {

    @Autowired(required = false)
    private ProductMapper productMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearProducts() {
        this.mongoTemplate.remove(new Query(), ProductDocument.class);
    }

    @Test
    @DisplayName("Given context When get ProductMapper Then bean is present (annotated package)")
    @Story("ProductMapper bean is registered")
    void givenContext_whenGetProductMapper_thenBeanIsPresent() {
        assertNotNull(
            this.productMapper,
            "ProductMapper should be registered by propertyMongoMapperScanConfigurer (annotated package)"
        );
    }

    @Test
    @DisplayName("Given inserted product When selectList Then returns list with one item")
    @Story("ProductMapper selectList returns list")
    void givenProductMapper_whenSelectListEmptyWrapper_thenReturnsList() {
        assertNotNull(this.productMapper);
        ProductDocument product = ProductDocument.builder()
            .name("TestProduct")
            .sku("SKU-001")
            .price(new BigDecimal("99.00"))
            .build();
        this.productMapper.insert(product);
        assertNotNull(product.getId());

        List<ProductDocument> list = this.productMapper.selectList(Wrappers.lambdaQuery(ProductDocument.class));
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("TestProduct", list.get(0).getName());
        assertEquals("SKU-001", list.get(0).getSku());
    }
}
