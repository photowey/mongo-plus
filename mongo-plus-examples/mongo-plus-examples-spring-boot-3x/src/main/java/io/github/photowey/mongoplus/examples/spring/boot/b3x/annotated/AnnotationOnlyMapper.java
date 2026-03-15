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
package io.github.photowey.mongoplus.examples.spring.boot.b3x.annotated;

import io.github.photowey.mongoplus.annotation.MongoMapper;

/**
 * AnnotationOnlyMapper - Only annotated with &#64;MongoMapper, does NOT extend MongoMapper.
 * Used to verify: within a scanned package, any interface with &#64;MongoMapper is discovered
 * and registered in IoC even without extending MongoMapper (it simply has no CRUD methods).
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/09
 */
@MongoMapper
public interface AnnotationOnlyMapper {
}
