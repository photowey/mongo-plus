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
package io.github.photowey.mongoplus.core.constant;

import java.util.Objects;

/**
 * MongoPlusConstants - Centralized constants for MongoPlus framework.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface MongoPlusConstants {

    String ID = "_id";

    String AND = "$and";
    String OR = "$or";
    String NOT = "$not";

    String EQ = "$eq";
    String NE = "$ne";
    String GT = "$gt";
    String GTE = "$gte";
    String LT = "$lt";
    String LTE = "$lte";

    String IN = "$in";
    String NIN = "$nin";

    String REGEX = "$regex";
    String EXISTS = "$exists";

    String ALL = "$all";
    String SIZE = "$size";
    String ELEM_MATCH = "$elemMatch";

    String GEO_WITHIN = "$geoWithin";
    String GEO_INTERSECTS = "$geoIntersects";
    String NEAR = "$near";
    String NEAR_SPHERE = "$nearSphere";
    String BOX = "$box";
    String POLYGON = "$polygon";
    String GEOMETRY = "$geometry";
    String CENTER_SPHERE = "$centerSphere";

    String MATCH = "$match";
    String PROJECT = "$project";
    String GROUP = "$group";
    String SORT = "$sort";
    String LIMIT = "$limit";
    String SKIP = "$skip";
    String LOOKUP = "$lookup";

    String SET = "$set";
    String INC = "$inc";
    String PUSH = "$push";
    String PULL = "$pull";
    String ADD_TO_SET = "$addToSet";

    String SUM = "$sum";
    String AVG = "$avg";
    String MAX = "$max";
    String MIN = "$min";

    String JAVA_ID = "id";

    String DEFAULT_MARKER_INTERFACE = "io.github.photowey.mongoplus.mapper.MongoMapper";

    interface Configuration {
        String MONGO_PLUS_PREFIX = "spring.data.mongodb.mongoplus";
        String MONGO_PLUS_MAPPER_PREFIX = "spring.data.mongodb.mongoplus.mapper";
        String MONGO_PLUS_IDENTITY_PREFIX = "spring.data.mongodb.mongoplus.identity";
        String MONGO_PLUS_PREFIX_ENV = "SPRING_DATA_MONGODB_MONGO_PLUS";

        static String determineMongoplusPropertyPrefix() {
            String prefix = System.getenv(MONGO_PLUS_PREFIX_ENV);
            if (Objects.nonNull(prefix) && !prefix.trim().isEmpty()) {
                return prefix;
            }

            return System.getProperty(MONGO_PLUS_PREFIX, MONGO_PLUS_PREFIX);
        }
    }
}

