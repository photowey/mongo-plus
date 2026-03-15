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
package io.github.photowey.mongoplus.dsl.ast.enums;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;

/**
 * Operator - Query operators for condition construction.
 * Unified operator definition for all query types (MongoDB, SQL, etc.)
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public enum Operator {

    /**
     * Comparison
     */
    EQ("eq", MongoPlusConstants.EQ, "="),
    NE("ne", MongoPlusConstants.NE, "!="),
    GT("gt", MongoPlusConstants.GT, ">"),
    GTE("gte", MongoPlusConstants.GTE, ">="),
    LT("lt", MongoPlusConstants.LT, "<"),
    LTE("lte", MongoPlusConstants.LTE, "<="),

    /**
     * Range
     */
    BETWEEN("between", null, "BETWEEN"),
    NOT_BETWEEN("notBetween", null, "NOT BETWEEN"),

    /**
     * In
     */
    IN("in", MongoPlusConstants.IN, "IN"),
    NIN("nin", MongoPlusConstants.NIN, "NOT IN"),

    /**
     * Like
     */
    LIKE("like", MongoPlusConstants.REGEX, "LIKE"),
    LIKE_LEFT("likeLeft", MongoPlusConstants.REGEX, "LIKE"),
    LIKE_RIGHT("likeRight", MongoPlusConstants.REGEX, "LIKE"),
    REGEX("regex", MongoPlusConstants.REGEX, "~"),

    /**
     * Null
     */
    IS_NULL("isNull", null, "IS NULL"),
    IS_NOT_NULL("isNotNull", null, "IS NOT NULL"),

    /**
     * Exists
     */
    EXISTS("exists", MongoPlusConstants.EXISTS, null),

    /**
     * Array
     */
    ALL("all", MongoPlusConstants.ALL, null),
    SIZE("size", MongoPlusConstants.SIZE, null),
    ELEM_MATCH("elemMatch", MongoPlusConstants.ELEM_MATCH, null),

    /**
     * Set operations
     */
    SET("set", MongoPlusConstants.SET, null),
    INC("inc", MongoPlusConstants.INC, null),
    PUSH("push", MongoPlusConstants.PUSH, null),
    PULL("pull", MongoPlusConstants.PULL, null),
    ADD_TO_SET("addToSet", MongoPlusConstants.ADD_TO_SET, null),

    /**
     * Geo operations
     */
    NEAR("near", MongoPlusConstants.NEAR, null),
    GEO_WITHIN("geoWithin", MongoPlusConstants.GEO_WITHIN, null),
    GEO_INTERSECTS("geoIntersects", MongoPlusConstants.GEO_INTERSECTS, null),
    NEAR_SPHERE("nearSphere", MongoPlusConstants.NEAR_SPHERE, null);

    private final String name;
    private final String mongoOperator;
    private final String sqlOperator;

    Operator(String name, String mongoOperator, String sqlOperator) {
        this.name = name;
        this.mongoOperator = mongoOperator;
        this.sqlOperator = sqlOperator;
    }

    public String displayName() {
        return name;
    }

    public String mongoOperator() {
        return mongoOperator;
    }

    public String sqlOperator() {
        return sqlOperator;
    }
}

