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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.photowey.mongoplus.core.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Box - Geographic bounding box (rectangle).
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Box implements GeoShape {

    private static final long serialVersionUID = 2765029859088824480L;

    /**
     * Lower left corner point.
     */
    private Point lowerLeft;

    /**
     * Upper right corner point.
     */
    private Point upperRight;

    @Override
    public GeoShapeType getType() {
        return GeoShapeType.BOX;
    }

    @Override
    public Object toGeoJson() {
        Map<String, Object> geo = new HashMap<>();
        geo.put("type", "Polygon");
        List<List<Double>> coordinates = Arrays.asList(
            Arrays.asList(this.lowerLeft.getLongitude(), this.lowerLeft.getLatitude()),
            Arrays.asList(this.upperRight.getLongitude(), this.lowerLeft.getLatitude()),
            Arrays.asList(this.upperRight.getLongitude(), this.upperRight.getLatitude()),
            Arrays.asList(this.lowerLeft.getLongitude(), this.upperRight.getLatitude()),
            Arrays.asList(this.lowerLeft.getLongitude(), this.lowerLeft.getLatitude())
        );
        geo.put("coordinates", Arrays.asList(coordinates));

        return geo;
    }

    /**
     * Get coordinates for MongoDB $box operator.
     * Format: [[lowerLeft.lon, lowerLeft.lat], [upperRight.lon, upperRight.lat]]
     *
     * @return coordinates array
     */
    public List<List<Double>> toBoxCoordinates() {
        return Arrays.asList(
            Arrays.asList(this.lowerLeft.getLongitude(), this.lowerLeft.getLatitude()),
            Arrays.asList(this.upperRight.getLongitude(), this.upperRight.getLatitude())
        );
    }

    /**
     * Create a Box with corner coordinates.
     *
     * @param lowerLeftLongitude  lower left longitude
     * @param lowerLeftLatitude   lower left latitude
     * @param upperRightLongitude upper right longitude
     * @param upperRightLatitude  upper right latitude
     * @return a new Box
     */
    public static Box of(
        double lowerLeftLongitude, double lowerLeftLatitude,
        double upperRightLongitude, double upperRightLatitude
    ) {
        return new Box(
            Point.of(lowerLeftLongitude, lowerLeftLatitude),
            Point.of(upperRightLongitude, upperRightLatitude)
        );
    }

    /**
     * Create a Box with corner points.
     *
     * @param lowerLeft  lower left point
     * @param upperRight upper right point
     * @return a new Box
     */
    public static Box of(Point lowerLeft, Point upperRight) {
        return new Box(lowerLeft, upperRight);
    }
}
