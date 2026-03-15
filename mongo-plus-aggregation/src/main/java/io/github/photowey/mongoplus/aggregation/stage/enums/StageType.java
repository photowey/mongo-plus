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
package io.github.photowey.mongoplus.aggregation.stage.enums;

/**
 * StageType - Types of aggregation pipeline stages.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public enum StageType {

    MATCH,
    GROUP,
    PROJECT,
    SORT,
    LIMIT,
    SKIP,
    LOOKUP,
    UNWIND
}
