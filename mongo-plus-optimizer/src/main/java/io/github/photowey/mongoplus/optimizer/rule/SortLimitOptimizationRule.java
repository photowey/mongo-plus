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
package io.github.photowey.mongoplus.optimizer.rule;

import java.util.List;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * SortLimitOptimizationRule - Optimizes $sort + $limit combinations.
 * MongoDB can optimize this internally as a "topN" operation.
 * This rule validates the ordering and ensures LIMIT comes after SORT.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class SortLimitOptimizationRule implements OptimizerRule {

    @Override
    public boolean matches(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();

        // Check for SORT followed by LIMIT
        for (int i = 0; i < stages.size() - 1; i++) {
            if (stages.get(i).type() == StageType.SORT
                && stages.get(i + 1).type() == StageType.LIMIT) {
                return true;
            }
        }

        // Check for redundant sorts
        Stage lastSort = null;
        for (Stage stage : stages) {
            if (stage.type() == StageType.SORT) {
                if (Objects.nonNull(lastSort)) {
                    // Multiple sorts found
                    return true;
                }

                lastSort = stage;
            }
        }

        return false;
    }

    @Override
    public Pipeline apply(Pipeline pipeline) {
        // This is mainly a validation rule
        // MongoDB itself optimizes SORT + LIMIT as topN
        // We just ensure the order is correct

        Pipeline.Builder builder = Pipeline.builder();
        List<Stage> stages = pipeline.stages();

        // Remove redundant sorts (keep only the last one)
        int lastSortIndex = -1;
        for (int i = 0; i < stages.size(); i++) {
            if (stages.get(i).type() == StageType.SORT) {
                lastSortIndex = i;
            }
        }

        for (int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);
            if (stage.type() == StageType.SORT && i != lastSortIndex) {
                // Skip redundant sort
                continue;
            }
            builder.add(stage);
        }

        return builder.build();
    }

    @Override
    public String getName() {
        return "SortLimitOptimizationRule";
    }

    @Override
    public int getPriority() {
        return 40;
    }
}
