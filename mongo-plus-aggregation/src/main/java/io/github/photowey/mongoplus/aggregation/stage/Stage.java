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

import org.bson.Document;

import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;

/**
 * Stage - Represents a single aggregation pipeline stage that can be appended to a {@link Pipeline}.
 *
 * <p>Each implementation maps to one MongoDB aggregation operator and knows how to render itself
 * as a BSON document.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Stage matchStage = new MatchStage(new Document("status", "ACTIVE"));
 * Stage sortStage = new SortStage("createdAt", -1);
 * Pipeline pipeline = Pipeline.of(matchStage, sortStage);
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface Stage {

    /**
     * Returns the logical stage type represented by this instance.
     *
     * @return the stage type metadata
     */
    StageType getType();

    /**
     * Renders this stage as a MongoDB aggregation document.
     *
     * @return the BSON representation of this stage
     */
    Document toBson();

    /**
     * Returns the stage type using a shorter fluent alias.
     *
     * @return the stage type metadata
     */
    default StageType type() {
        return this.getType();
    }
}
