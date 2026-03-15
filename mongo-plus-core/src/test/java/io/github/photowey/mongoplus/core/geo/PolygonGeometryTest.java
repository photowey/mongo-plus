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
package io.github.photowey.mongoplus.core.geo;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * PolygonGeometryTest - Unit tests for polygon geometry conversion helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("PolygonGeometryTest")
class PolygonGeometryTest {

    @Test
    @DisplayName(
        "Given polygon factories, when converting to mongo and geojson coordinates, then "
            + "preserves the point order"
    )
    @Story("Polygon factory preserves point order in Mongo and GeoJSON")
    void givenPolygonFactories_whenConvertingToMongoAndGeojsonCoordinates_thenPreservesThePointOrder() {
        // Given
        Polygon polygon = Polygon.of(
            new double[] {120.0, 30.0},
            new double[] {121.0, 30.0},
            new double[] {121.0, 31.0},
            new double[] {120.0, 30.0}
        );

        // When
        List<List<Double>> mongoCoordinates = polygon.toPolygonCoordinates();
        Map<?, ?> geoJson = (Map<?, ?>) polygon.toGeoJson();

        // Then
        assertEquals(GeoShapeType.POLYGON, polygon.getType());
        assertEquals(4, mongoCoordinates.size());
        assertEquals(List.of(120.0, 30.0), mongoCoordinates.get(0));
        assertEquals("Polygon", geoJson.get("type"));
        assertInstanceOf(List.class, geoJson.get("coordinates"));
    }
}
