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
import io.github.photowey.mongoplus.examples.spring.boot.b3x.configmapper.OrderMapper;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.core.domain.document.OrderDocument;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * OrderMapperTest - Integration tests for OrderMapper (discovered by property config base-packages).
 * Clears orders collection before each test, consistent with other integration tests.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("OrderMapperTest")
class OrderMapperTest extends AbstractMongoTest {

    @Autowired(required = false)
    private OrderMapper orderMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearOrders() {
        this.mongoTemplate.remove(new Query(), OrderDocument.class);
    }

    @Test
    @DisplayName("Given context When get OrderMapper Then bean is present (config base-packages)")
    @Story("Mapper bean registration via property config")
    void givenContext_whenGetOrderMapper_thenBeanIsPresent() {
        assertNotNull(
            this.orderMapper,
            "OrderMapper should be registered by propertyMongoMapperScanConfigurer (configmapper package)"
        );
    }

    @Test
    @DisplayName("Given inserted order When selectList Then returns list with one item")
    @Story("Select list with empty wrapper")
    void givenOrderMapper_whenSelectListEmptyWrapper_thenReturnsList() {
        assertNotNull(this.orderMapper);
        OrderDocument order = OrderDocument.builder()
            .orderNo("ORD-001")
            .userId(1001L)
            .status("CREATED")
            .build();
        this.orderMapper.insert(order);
        assertNotNull(order.getId());

        List<OrderDocument> list = this.orderMapper.selectList(Wrappers.lambdaQuery(OrderDocument.class));
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("ORD-001", list.get(0).getOrderNo());
        assertEquals(1001L, list.get(0).getUserId());
        assertEquals("CREATED", list.get(0).getStatus());
    }
}
