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
 * BusinessId - Marks a field whose value will be copied to the document's _id.
 *
 * <p>
 * When this annotation is present on a field, its value will be automatically
 * copied to the ID field before saving, if the ID is not already set.
 *
 * <p>
 * <strong>Important:</strong> Only one field can be annotated with &#64;BusinessId
 * per entity class. Multiple &#64;BusinessId annotations will cause an error.
 *
 * <p>
 * Usage example:
 * <pre>
 * public class Order {
 *     &#64;Id
 *     private String id;
 *
 *     &#64;BusinessId
 *     private String orderNo; // orderNo value will be copied to id
 * }
 * </pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessId {

    /**
     * Whether to copy the value to the ID field.
     * Default is true.
     */
    boolean copyToId() default true;
}
