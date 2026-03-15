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
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Circle - Geographic circle with center point and radius.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Circle implements GeoShape {

    private static final long serialVersionUID = 7003866374893047851L;

    /**
     * Center point.
     */
    private Point center;

    /**
     * Radius in meters (for $centerSphere, radius is in radians).
     */
    private double radius;

    @Override
    public GeoShapeType getType() {
        return GeoShapeType.CIRCLE;
    }

    @Override
    public Object toGeoJson() {
        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "Circle");
        geoJson.put("coordinates", Arrays.asList(center.getLongitude(), center.getLatitude()));
        geoJson.put("radius", radius);
        return geoJson;
    }

    /**
     * Convert radius to radians for $centerSphere.
     *
     * @return radius in radians
     */
    public double getRadiusInRadians() {
        // Earth's radius is approximately 6,371,000 meters
        return radius / 6371000.0;
    }

    /**
     * Create a Circle with center coordinates and radius.
     *
     * @param centerLongitude the center longitude
     * @param centerLatitude  the center latitude
     * @param radiusMeters    the radius in meters
     * @return a new Circle
     */
    public static Circle of(double centerLongitude, double centerLatitude, double radiusMeters) {
        return new Circle(Point.of(centerLongitude, centerLatitude), radiusMeters);
    }

    /**
     * Create a Circle with center point and radius.
     *
     * @param center       the center point
     * @param radiusMeters the radius in meters
     * @return a new Circle
     */
    public static Circle of(Point center, double radiusMeters) {
        return new Circle(center, radiusMeters);
    }
}
