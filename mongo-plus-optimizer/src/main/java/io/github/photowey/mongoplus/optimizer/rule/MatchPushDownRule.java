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

/**
 * MatchPushDownRule - Pushes $match stages earlier in the pipeline.
 * E.g., [project, match] -> [match, project]
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MatchPushDownRule implements OptimizerRule {

    @Override
    public boolean matches(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        if (stages.size() < 2) {
            return false;
        }

        // Check for MATCH after PROJECT or LOOKUP
        for (int i = 0; i < stages.size() - 1; i++) {
            StageType current = stages.get(i).type();
            StageType next = stages.get(i + 1).type();

            if ((current == StageType.PROJECT
                || current == StageType.LOOKUP)
                && next == StageType.MATCH) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Pipeline apply(Pipeline pipeline) {
        Pipeline.Builder builder = Pipeline.builder();
        List<Stage> stages = pipeline.getStages();

        // Simple implementation: swap adjacent PROJECT-MATCH pairs
        int i = 0;
        while (i < stages.size()) {
            if (i < stages.size() - 1) {
                Stage current = stages.get(i);
                Stage next = stages.get(i + 1);

                if ((current.type() == StageType.PROJECT
                    || current.type() == StageType.LOOKUP)
                    && next.type() == StageType.MATCH) {
                    // Swap: add MATCH first, then PROJECT/LOOKUP
                    builder.add(next);
                    builder.add(current);
                    i += 2;
                    continue;
                }
            }
            builder.add(stages.get(i));
            i++;
        }

        return builder.build();
    }

    @Override
    public String getName() {
        return "MatchPushDownRule";
    }

    @Override
    public int getPriority() {
        return 20;
    }
}
