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

import io.github.photowey.mongoplus.core.util.AssertionErrors;

/**
 * GeoShapes - Factory class for creating geographic shapes.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class GeoShapes {

    private GeoShapes() {
        AssertionErrors.throwz(GeoShapes.class);
    }

    /**
     * Create a Point.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     * @return a new Point
     */
    public static Point point(double longitude, double latitude) {
        return Point.of(longitude, latitude);
    }

    /**
     * Create a Circle.
     *
     * @param centerLongitude the center longitude
     * @param centerLatitude  the center latitude
     * @param radiusMeters    the radius in meters
     * @return a new Circle
     */
    public static Circle circle(double centerLongitude, double centerLatitude, double radiusMeters) {
        return Circle.of(centerLongitude, centerLatitude, radiusMeters);
    }

    /**
     * Create a Circle.
     *
     * @param center       the center point
     * @param radiusMeters the radius in meters
     * @return a new Circle
     */
    public static Circle circle(Point center, double radiusMeters) {
        return Circle.of(center, radiusMeters);
    }

    /**
     * Create a Box.
     *
     * @param lowerLeftLongitude  lower left longitude
     * @param lowerLeftLatitude   lower left latitude
     * @param upperRightLongitude upper right longitude
     * @param upperRightLatitude  upper right latitude
     * @return a new Box
     */
    public static Box box(
        double lowerLeftLongitude, double lowerLeftLatitude,
        double upperRightLongitude, double upperRightLatitude
    ) {
        return Box.of(lowerLeftLongitude, lowerLeftLatitude, upperRightLongitude, upperRightLatitude);
    }

    /**
     * Create a Box.
     *
     * @param lowerLeft  lower left point
     * @param upperRight upper right point
     * @return a new Box
     */
    public static Box box(Point lowerLeft, Point upperRight) {
        return Box.of(lowerLeft, upperRight);
    }

    /**
     * Create a Polygon.
     *
     * @param points the exterior ring points
     * @return a new Polygon
     */
    public static Polygon polygon(Point... points) {
        return Polygon.of(points);
    }

    /**
     * Create a Polygon.
     *
     * @param points the exterior ring points
     * @return a new Polygon
     */
    public static Polygon polygon(List<Point> points) {
        return Polygon.of(points);
    }

    /**
     * Create a Polygon from coordinate pairs.
     *
     * @param coordinates array of [lon, lat] pairs
     * @return a new Polygon
     */
    public static Polygon polygon(double[]... coordinates) {
        return Polygon.of(coordinates);
    }
}
