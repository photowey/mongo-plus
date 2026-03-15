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

import org.bson.Document;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * ProjectSimplifyRule - Removes or simplifies $project stages.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class ProjectSimplifyRule implements OptimizerRule {

    @Override
    public boolean matches(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();

        for (Stage stage : stages) {
            if (stage.type() == StageType.PROJECT) {
                Document projectDoc = stage
                    .toBson()
                    .get(MongoPlusConstants.PROJECT, Document.class);
                if (this.isRedundantProject(projectDoc)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Pipeline apply(Pipeline pipeline) {
        Pipeline.Builder builder = Pipeline.builder();

        for (Stage stage : pipeline.getStages()) {
            if (stage.type() == StageType.PROJECT) {
                Document projectDoc = stage
                    .toBson()
                    .get(MongoPlusConstants.PROJECT, Document.class);
                // Skip redundant projects
                if (!this.isRedundantProject(projectDoc)) {
                    builder.add(stage);
                }
            } else {
                builder.add(stage);
            }
        }

        return builder.build();
    }

    /**
     * Check if project is redundant (projects all fields with 1).
     */
    private boolean isRedundantProject(Document projectDoc) {
        if (Objects.isNull(projectDoc) || projectDoc.isEmpty()) {
            return true;
        }

        // If _id is the only projected field and it is excluded, the stage is redundant.
        if (projectDoc.containsKey(MongoPlusConstants.ID)
            && Integer.valueOf(0).equals(projectDoc.get(MongoPlusConstants.ID))
            && projectDoc.size() == 1) {
            return true;
        }

        // Check if all values are 1 (meaning include all)
        for (Object value : projectDoc.values()) {
            if (!Integer.valueOf(1).equals(value)) {
                return false;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return "ProjectSimplifyRule";
    }

    @Override
    public int getPriority() {
        return 30;
    }
}
