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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GeoFactoriesTest - Unit tests for geo factory helpers and shape conversions.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("GeoFactoriesTest")
class GeoFactoriesTest {

    @Test
    @DisplayName(
        "Given point based factory methods, when creating circle and box shapes, then returns the "
            + "expected geometry data"
    )
    @Story("Create circle and box from point-based factory methods")
    void givenPointBasedFactoryMethods_whenCreatingCircleAndBoxShapes_thenReturnsTheExpectedGeometryData() {
        // Given
        Point center = Point.of(120.0, 30.0);
        Point lowerLeft = Point.of(119.0, 29.0);
        Point upperRight = Point.of(121.0, 31.0);

        // When
        Circle circle = GeoShapes.circle(center, 1000.0);
        Box box = GeoShapes.box(lowerLeft, upperRight);
        Map<?, ?> circleGeoJson = (Map<?, ?>) circle.toGeoJson();
        Map<?, ?> boxGeoJson = (Map<?, ?>) box.toGeoJson();

        // Then
        assertEquals(GeoShapeType.CIRCLE, circle.getType());
        assertEquals(1000.0 / 6371000.0, circle.getRadiusInRadians());
        assertEquals("Circle", circleGeoJson.get("type"));
        assertEquals(GeoShapeType.BOX, box.getType());
        assertEquals(2, box.toBoxCoordinates().size());
        assertEquals("Polygon", boxGeoJson.get("type"));
        assertInstanceOf(List.class, boxGeoJson.get("coordinates"));
    }

    @Test
    @DisplayName(
        "Given additional GeoShapes factory overloads, when invoking them, then returns the "
            + "expected concrete shapes"
    )
    @Story("GeoShapes factory overloads return expected concrete shapes")
    void givenAdditionalGeoShapesFactoryOverloads_whenInvokingThem_thenReturnsTheExpectedConcreteShapes() {
        // Given
        Point point = GeoShapes.point(118.0, 32.0);
        List<Point> polygonPoints = List.of(
            Point.of(120.0, 30.0),
            Point.of(121.0, 30.0),
            Point.of(121.0, 31.0),
            Point.of(120.0, 30.0)
        );

        // When
        Circle circle = GeoShapes.circle(118.0, 32.0, 500.0);
        Box box = GeoShapes.box(117.0, 31.0, 119.0, 33.0);
        Polygon fromPoints = GeoShapes.polygon(polygonPoints);
        Polygon fromCoordinates = GeoShapes.polygon(
            new double[] {120.0, 30.0},
            new double[] {121.0, 30.0},
            new double[] {121.0, 31.0},
            new double[] {120.0, 30.0}
        );

        // Then
        assertEquals(118.0, point.getLongitude());
        assertEquals(32.0, point.getLatitude());
        assertEquals(500.0, circle.getRadius());
        assertEquals(GeoShapeType.BOX, box.getType());
        assertEquals(4, fromPoints.toPolygonCoordinates().size());
        assertEquals(4, fromCoordinates.toPolygonCoordinates().size());
    }

    @Test
    @DisplayName(
        "Given the GeoShapes utility constructor, when invoking it reflectively, then throws an "
            + "AssertionError"
    )
    @Story("Utility class rejects reflection instantiation")
    void givenTheGeoShapesUtilityConstructor_whenInvokingItReflectively_thenThrowsAnAssertionError() throws Exception {
        // Given
        Constructor<GeoShapes> constructor = GeoShapes.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When
        InvocationTargetException error = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // Then
        assertInstanceOf(AssertionError.class, error.getCause());
    }
}
