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
package io.github.photowey.mongoplus.executor.compiler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.compiler.PipelineCompiler;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.MATCH;
import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.PROJECT;

/**
 * MongoPipelineCompiler - Implementation of PipelineCompiler for MongoDB.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MongoPipelineCompiler implements PipelineCompiler {

    @Override
    public Aggregation compile(Pipeline pipeline) {
        if (Objects.isNull(pipeline) || pipeline.isEmpty()) {
            return EmptyAggregation.empty();
        }

        List<AggregationOperation> operations = new ArrayList<>();
        for (Stage stage : pipeline.getStages()) {
            AggregationOperation op = this.compileStage(stage);
            if (Objects.nonNull(op)) {
                operations.add(op);
            }
        }

        if (operations.isEmpty()) {
            return EmptyAggregation.empty();
        }

        return Aggregation.newAggregation(operations);
    }

    private AggregationOperation compileStage(Stage stage) {
        if (Objects.isNull(stage)) {
            return null;
        }

        StageType type = stage.getType();
        Document bson = stage.toBson();

        // Convert BSON Document to AggregationOperation
        // This is a simplified version - real implementation would need more detail
        switch (type) {
            case MATCH:
                return this.compileMatch(bson);
            case GROUP:
                return this.compileGroup(bson);
            case PROJECT:
                return this.compileProject(bson);
            case SORT:
                return this.compileSort(bson);
            case LIMIT:
                return this.compileLimit(bson);
            case SKIP:
                return this.compileSkip(bson);
            case LOOKUP:
                return this.compileLookup(bson);
            default:
                return null;
        }
    }

    private AggregationOperation compileMatch(Document bson) {
        Document matchDoc = bson.get(MATCH, Document.class);
        if (Objects.isNull(matchDoc)) {
            return null;
        }

        return context -> new Document(MATCH, matchDoc);
    }

    private AggregationOperation compileGroup(Document bson) {
        Document groupDoc = bson.get(MongoPlusConstants.GROUP, Document.class);
        if (Objects.isNull(groupDoc)) {
            return null;
        }

        return context -> new Document(MongoPlusConstants.GROUP, groupDoc);
    }

    private AggregationOperation compileProject(Document bson) {
        Document projectDoc = bson.get(PROJECT, Document.class);
        if (Objects.isNull(projectDoc)) {
            return null;
        }

        return context -> new Document(PROJECT, projectDoc);
    }

    private AggregationOperation compileSort(Document bson) {
        Document sortDoc = bson.get(MongoPlusConstants.SORT, Document.class);
        if (Objects.isNull(sortDoc)) {
            return null;
        }

        return context -> new Document(MongoPlusConstants.SORT, sortDoc);
    }

    private AggregationOperation compileLimit(Document bson) {
        Integer limit = bson.getInteger(MongoPlusConstants.LIMIT);
        if (Objects.isNull(limit)) {
            return null;
        }

        return Aggregation.limit(limit);
    }

    private AggregationOperation compileSkip(Document bson) {
        Integer skip = bson.getInteger(MongoPlusConstants.SKIP);
        if (Objects.isNull(skip)) {
            return null;
        }

        return Aggregation.skip(skip.longValue());
    }

    private AggregationOperation compileLookup(Document bson) {
        Document lookupDoc = bson.get(MongoPlusConstants.LOOKUP, Document.class);
        if (Objects.isNull(lookupDoc)) {
            return null;
        }

        String from = lookupDoc.getString("from");
        String localField = lookupDoc.getString("localField");
        String foreignField = lookupDoc.getString("foreignField");
        String as = lookupDoc.getString("as");

        return Aggregation.lookup(from, localField, foreignField, as);
    }

    private static final class EmptyAggregation extends Aggregation {

        private EmptyAggregation() {
            super(Collections.emptyList());
        }

        private static Aggregation empty() {
            return new EmptyAggregation();
        }
    }
}
