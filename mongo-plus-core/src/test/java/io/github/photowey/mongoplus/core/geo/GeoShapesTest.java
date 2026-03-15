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

import java.util.Arrays;
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

/**
 * GeoShapesTest - Unit tests for GeoShapes factories and GeoJSON helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("GeoShapesTest")
class GeoShapesTest {

    @Test
    @DisplayName("Given lng lat When point Then returns Point with correct coordinates and GeoJSON")
    @Story("Create point from lng/lat with GeoJSON")
    void givenLngLat_whenPoint_thenReturnsPointWithCorrectCoordinatesAndGeoJson() {
        Point point = GeoShapes.point(120.0, 30.0);

        assertEquals(120.0, point.getLongitude());
        assertEquals(30.0, point.getLatitude());
        assertEquals(GeoShapeType.POINT, point.getType());

        Map<String, Object> geoJson = (Map<String, Object>) point.toGeoJson();
        assertEquals("Point", geoJson.get("type"));
        List<Double> coords = (List<Double>) geoJson.get("coordinates");
        assertEquals(120.0, coords.get(0));
        assertEquals(30.0, coords.get(1));
    }

    @Test
    @DisplayName("Given center and radius When circle Then returns Circle with radiusInRadians")
    @Story("Create circle from center and radius in radians")
    void givenCenterAndRadius_whenCircle_thenReturnsCircleWithRadiusInRadians() {
        Circle circle = GeoShapes.circle(120.0, 30.0, 5000.0);

        assertEquals(120.0, circle.getCenter().getLongitude());
        assertEquals(30.0, circle.getCenter().getLatitude());
        assertEquals(5000.0, circle.getRadius());
        assertEquals(GeoShapeType.CIRCLE, circle.getType());

        // Test radius in radians (Earth radius ~ 6371000m)
        double expectedRadians = 5000.0 / 6371000.0;
        assertEquals(expectedRadians, circle.getRadiusInRadians(), 0.0001);
    }

    @Test
    @DisplayName("Given bounds When box Then returns Box with lowerLeft upperRight and box coordinates")
    @Story("Create box from bounds")
    void givenBounds_whenBox_thenReturnsBoxWithLowerLeftUpperRightAndBoxCoordinates() {
        Box box = GeoShapes.box(119.0, 29.0, 121.0, 31.0);

        assertEquals(119.0, box.getLowerLeft().getLongitude());
        assertEquals(29.0, box.getLowerLeft().getLatitude());
        assertEquals(121.0, box.getUpperRight().getLongitude());
        assertEquals(31.0, box.getUpperRight().getLatitude());
        assertEquals(GeoShapeType.BOX, box.getType());

        // Test box coordinates for MongoDB $box
        List<List<Double>> coords = box.toBoxCoordinates();
        assertEquals(2, coords.size());
        assertEquals(119.0, coords.get(0).get(0));
        assertEquals(29.0, coords.get(0).get(1));
        assertEquals(121.0, coords.get(1).get(0));
        assertEquals(31.0, coords.get(1).get(1));
    }

    @Test
    @DisplayName("Given list of points When polygon Then returns Polygon with exterior and coordinates")
    @Story("Create polygon from list of points")
    void givenListOfPoints_whenPolygon_thenReturnsPolygonWithExteriorAndCoordinates() {
        List<Point> points = Arrays.asList(
            Point.of(119.0, 29.0),
            Point.of(121.0, 29.0),
            Point.of(121.0, 31.0),
            Point.of(119.0, 31.0),
            // Close the polygon
            Point.of(119.0, 29.0)
        );
        Polygon polygon = GeoShapes.polygon(points);

        assertEquals(5, polygon.getExterior().size());
        assertEquals(GeoShapeType.POLYGON, polygon.getType());

        // Test polygon coordinates
        List<List<Double>> coords = polygon.toPolygonCoordinates();
        assertEquals(5, coords.size());
    }

    @Test
    @DisplayName("Given coordinate arrays When polygon Then returns Polygon with exterior size")
    @Story("Create polygon from coordinate arrays")
    void givenCoordinateArrays_whenPolygon_thenReturnsPolygonWithExteriorSize() {
        Polygon polygon = GeoShapes.polygon(
            new double[] {119.0, 29.0},
            new double[] {121.0, 29.0},
            new double[] {121.0, 31.0}
        );

        assertEquals(3, polygon.getExterior().size());
    }

    @Test
    @DisplayName("Given lng lat When point or Point.of Then same coordinates")
    @Story("Point and pointOf return same coordinates")
    void givenLngLat_whenPointOrPointOf_thenSameCoordinates() {
        Point p1 = GeoShapes.point(120.0, 30.0);
        Point p2 = Point.of(120.0, 30.0);

        assertEquals(p1.getLongitude(), p2.getLongitude());
        assertEquals(p1.getLatitude(), p2.getLatitude());
    }

    @Test
    @DisplayName("Given center and radius When circle or Circle.of Then same radius")
    @Story("Circle and circleOf return same radius")
    void givenCenterAndRadius_whenCircleOrCircleOf_thenSameRadius() {
        Circle c1 = GeoShapes.circle(120.0, 30.0, 5000);
        Circle c2 = Circle.of(120.0, 30.0, 5000);

        assertEquals(c1.getRadius(), c2.getRadius());
    }
}
