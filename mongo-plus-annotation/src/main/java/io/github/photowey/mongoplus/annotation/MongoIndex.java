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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MongoIndex - Defines an index for the collection.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface MongoIndex {
    /**
     * Index fields. Format: "fieldName" or "fieldName:direction" (ASC/DESC).
     * Example: {"userName", "createdAt:DESC"}
     * Default is empty array, which means use the annotated field name.
     */
    String[] fields() default {};

    /**
     * Whether this is a unique index.
     */
    boolean unique() default false;

    /**
     * Index name (optional).
     */
    String name() default "";

    /**
     * Whether this is a sparse index.
     */
    boolean sparse() default false;
}
