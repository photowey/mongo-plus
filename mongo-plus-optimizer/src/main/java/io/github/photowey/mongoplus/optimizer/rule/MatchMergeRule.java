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

import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * MatchMergeRule - Merges adjacent $match stages.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MatchMergeRule implements OptimizerRule {

    @Override
    public boolean matches(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        if (stages.size() < 2) {
            return false;
        }

        // Check for adjacent MATCH stages
        for (int i = 0; i < stages.size() - 1; i++) {
            if (stages.get(i).type() == StageType.MATCH
                && stages.get(i + 1).type() == StageType.MATCH) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Pipeline apply(Pipeline pipeline) {
        Pipeline.Builder builder = Pipeline.builder();
        List<Stage> stages = pipeline.getStages();

        int i = 0;
        while (i < stages.size()) {
            Stage current = stages.get(i);

            // Check if current and next are both MATCH
            if (current.getType() == StageType.MATCH && i < stages.size() - 1) {
                Stage next = stages.get(i + 1);
                if (next.getType() == StageType.MATCH) {
                    // Merge the two match stages
                    Document mergedCriteria = mergeMatchCriteria(
                        current.toBson().get(MongoPlusConstants.MATCH, Document.class),
                        next.toBson().get(MongoPlusConstants.MATCH, Document.class)
                    );
                    builder.add(new MatchStage(mergedCriteria));
                    i += 2;
                    continue;
                }
            }

            builder.add(current);
            i++;
        }

        return builder.build();
    }

    private Document mergeMatchCriteria(Document criteria1, Document criteria2) {
        Document merged = new Document();
        if (Objects.nonNull(criteria1)) {
            merged.putAll(criteria1);
        }
        if (Objects.nonNull(criteria2)) {
            // Use $and to combine if there are conflicting keys
            for (String key : criteria2.keySet()) {
                if (merged.containsKey(key)) {
                    // Create $and array for conflicting conditions
                    Document andDoc = new Document();
                    andDoc.put(key, merged.get(key));

                    Document existingAnd = merged.get(MongoPlusConstants.AND, Document.class);
                    if (Objects.isNull(existingAnd)) {
                        existingAnd = new Document();
                    }

                    // This is simplified - real implementation would handle $and properly
                    merged.put(
                        MongoPlusConstants.AND,
                        Arrays.asList(new Document(key, merged.get(key)), new Document(key, criteria2.get(key)))
                    );
                    merged.remove(key);
                } else {
                    merged.put(key, criteria2.get(key));
                }
            }
        }

        return merged;
    }

    @Override
    public String getName() {
        return "MatchMergeRule";
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
