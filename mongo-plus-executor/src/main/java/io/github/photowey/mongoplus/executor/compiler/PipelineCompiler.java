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
package io.github.photowey.mongoplus.executor.compiler;

import org.springframework.data.mongodb.core.aggregation.Aggregation;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;

/**
 * PipelineCompiler - Compiles Aggregation Pipeline to MongoDB Aggregation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface PipelineCompiler {

    /**
     * Compile Pipeline to MongoDB Aggregation.
     *
     * @param pipeline the aggregation pipeline
     * @return the compiled MongoDB Aggregation
     */
    Aggregation compile(Pipeline pipeline);
}
