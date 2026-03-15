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
package io.github.photowey.mongoplus.aggregation.stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;

import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * Pipeline - Stores an immutable ordered list of aggregation {@link Stage stages}.
 *
 * <p>Use this class as the canonical pipeline value object when compiling, optimizing, or executing
 * aggregation requests.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Pipeline pipeline = Pipeline.builder()
 *     .add(new MatchStage(new Document("status", "ACTIVE")))
 *     .add(new SortStage("createdAt", -1))
 *     .add(new LimitStage(10))
 *     .build();
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class Pipeline {

    private final List<Stage> stages;

    private Pipeline(List<Stage> stages) {
        this.stages = List.copyOf(stages);
    }

    /**
     * Creates a builder for assembling a pipeline step by step.
     *
     * @return a new pipeline builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a pipeline containing a single stage.
     *
     * @param stage the stage to include
     * @return a pipeline containing the supplied stage
     */
    public static Pipeline of(Stage stage) {
        return builder().add(stage).build();
    }

    /**
     * Creates a pipeline from an array of stages.
     *
     * @param stages the stages to include
     * @return a pipeline containing the supplied stages
     */
    public static Pipeline of(Stage... stages) {
        return builder().add(stages).build();
    }

    /**
     * Creates a pipeline from a collection of stages.
     *
     * @param stages the stages to include
     * @return a pipeline containing the supplied stages
     */
    public static Pipeline of(Collection<? extends Stage> stages) {
        return builder().add(stages).build();
    }

    /**
     * Mutable builder used to collect stages before creating an immutable pipeline.
     *
     * @author photowey
     * @version 2026.1.0.0
     * @since 2026/03/12
     */
    public static class Builder {

        private final List<Stage> stages = new ArrayList<>();

        /**
         * Appends a single stage to the builder.
         *
         * @param stage the stage to append
         * @return this builder
         */
        public Builder add(Stage stage) {
            Objects.requireNonNull(stage, "stage must not be null");
            this.stages.add(stage);

            return this;
        }

        /**
         * Appends multiple stages to the builder.
         *
         * @param stages the stages to append
         * @return this builder
         */
        public Builder add(Stage... stages) {
            if (Arrays.isNotEmpty(stages)) {
                for (Stage stage : stages) {
                    this.add(stage);
                }
            }
            return this;
        }

        /**
         * Appends all stages from the provided collection.
         *
         * @param stages the stages to append
         * @return this builder
         */
        public Builder add(Collection<? extends Stage> stages) {
            if (Collections.isNotEmpty(stages)) {
                for (Stage stage : stages) {
                    this.add(stage);
                }
            }

            return this;
        }

        /**
         * Builds an immutable pipeline from the accumulated stages.
         *
         * @return the constructed pipeline
         */
        public Pipeline build() {
            return new Pipeline(this.stages);
        }
    }

    /**
     * Converts the pipeline to the BSON array expected by MongoDB aggregation APIs.
     *
     * @return the ordered stage document list
     */
    public List<Document> toBsonArray() {
        List<Document> documents = new ArrayList<>(this.stages.size());
        for (Stage stage : this.stages) {
            documents.add(stage.toBson());
        }

        return documents;
    }

    /**
     * Determines whether the pipeline contains no stages.
     *
     * @return {@code true} when the pipeline is empty
     */
    public boolean isEmpty() {
        return this.stages.isEmpty();
    }

    /**
     * Returns the immutable stage list using a JavaBean-style accessor.
     *
     * @return the ordered stage list
     */
    public List<Stage> getStages() {
        return this.stages;
    }

    /**
     * Returns the immutable stage list using a fluent accessor.
     *
     * @return the ordered stage list
     */
    public List<Stage> stages() {
        return this.stages;
    }
}
