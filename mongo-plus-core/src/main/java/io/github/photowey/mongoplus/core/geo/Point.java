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
 * Point - Geographic point with longitude and latitude.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point implements GeoShape {

    private static final long serialVersionUID = 5979030888587807480L;

    /**
     * Longitude (x-axis).
     */
    private double longitude;

    /**
     * Latitude (y-axis).
     */
    private double latitude;

    @Override
    public GeoShapeType getType() {
        return GeoShapeType.POINT;
    }

    @Override
    public Object toGeoJson() {
        Map<String, Object> geo = new HashMap<>();
        geo.put("type", "Point");
        geo.put("coordinates", Arrays.asList(this.longitude, this.latitude));

        return geo;
    }

    /**
     * Create a Point from longitude and latitude.
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     * @return a new Point
     */
    public static Point of(double longitude, double latitude) {
        return new Point(longitude, latitude);
    }
}
