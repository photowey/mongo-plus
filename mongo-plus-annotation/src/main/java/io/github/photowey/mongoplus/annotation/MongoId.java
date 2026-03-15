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
package io.github.photowey.mongoplus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MongoId - Marks the field as the document ID with custom generation strategy.
 *
 * <p>
 * If the ID field is null before saving, the framework will automatically
 * generate an ID based on the specified type.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoId {

    /**
     * ID generation type.
     * Default is NONE, which means no automatic generation.
     */
    IdType type() default IdType.NONE;

    /**
     * IdType - ID generation strategy types.
     */
    enum IdType {
        /**
         * No automatic ID generation.
         * MongoDB will generate ObjectId if ID is null.
         */
        NONE,
        /**
         * Auto-detect: use ASSIGN_ID for Long/String, NONE for ObjectId
         */
        AUTO,
        /**
         * Assign ID using the configured IdentifyGenerator (default: Snowflake)
         */
        ASSIGN_ID,
        /**
         * Assign UUID as ID
         */
        ASSIGN_UUID,
    }
}
