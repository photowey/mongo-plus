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
package io.github.photowey.mongoplus.plugin.context;

import java.io.Serializable;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.plugin.enums.AggregationExecutionOperation;
import io.github.photowey.mongoplus.plugin.enums.InvocationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AggregationExecutionContext - Carries metadata for an aggregation execution intercepted by the framework.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Pipeline pipeline = context.getPipeline();
 * AggregationExecutionOperation operation = context.getOperation();
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationExecutionContext implements InvocationContext, Serializable {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private InvocationType invocationType = InvocationType.AGGREGATION;

    private AggregationExecutionOperation operation;
    private Pipeline pipeline;
    private Class<?> inputType;
    private Class<?> outputType;
    private String collectionName;
}
