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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Polygon - Geographic polygon shape.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Polygon implements GeoShape {

    private static final long serialVersionUID = -6600225312467712734L;

    /**
     * Exterior ring points (must be closed - first point == last point).
     */
    private List<Point> exterior;

    /**
     * Interior rings (holes) - optional.
     */
    private List<List<Point>> holes;

    public Polygon(List<Point> exterior) {
        this.exterior = exterior;
        this.holes = new ArrayList<>();
    }

    @Override
    public GeoShapeType getType() {
        return GeoShapeType.POLYGON;
    }

    @Override
    public Object toGeoJson() {
        Map<String, Object> geo = new HashMap<>();
        geo.put("type", "Polygon");

        List<List<List<Double>>> coordinates = new ArrayList<>();

        // Exterior ring
        coordinates.add(ringToCoordinates(exterior));

        // Interior rings (holes)
        for (List<Point> hole : holes) {
            coordinates.add(ringToCoordinates(hole));
        }

        geo.put("coordinates", coordinates);
        return geo;
    }

    private List<List<Double>> ringToCoordinates(List<Point> ring) {
        return ring.stream()
            .map(p -> Arrays.asList(p.getLongitude(), p.getLatitude()))
            .collect(Collectors.toList());
    }

    /**
     * Get coordinates for MongoDB $polygon operator.
     * Format: [[lon1, lat1], [lon2, lat2], ...]
     *
     * @return coordinates list
     */
    public List<List<Double>> toPolygonCoordinates() {
        return ringToCoordinates(exterior);
    }

    /**
     * Create a Polygon from list of points.
     *
     * @param points the exterior ring points
     * @return a new Polygon
     */
    public static Polygon of(Point... points) {
        return new Polygon(Arrays.asList(points));
    }

    /**
     * Create a Polygon from list of points.
     *
     * @param points the exterior ring points
     * @return a new Polygon
     */
    public static Polygon of(List<Point> points) {
        return new Polygon(points);
    }

    /**
     * Create a Polygon from coordinate pairs.
     * Each pair is [longitude, latitude].
     *
     * @param coordinates array of [lon, lat] pairs
     * @return a new Polygon
     */
    public static Polygon of(double[]... coordinates) {
        List<Point> points = new ArrayList<>();
        for (double[] coord : coordinates) {
            if (coord.length >= 2) {
                points.add(Point.of(coord[0], coord[1]));
            }
        }
        return new Polygon(points);
    }
}
