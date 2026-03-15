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
package io.github.photowey.mongoplus.optimizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.optimizer.rule.MatchMergeRule;
import io.github.photowey.mongoplus.optimizer.rule.MatchPushDownRule;
import io.github.photowey.mongoplus.optimizer.rule.OptimizerRule;
import io.github.photowey.mongoplus.optimizer.rule.ProjectSimplifyRule;
import io.github.photowey.mongoplus.optimizer.rule.SortLimitOptimizationRule;

/**
 * QueryOptimizer - Applies optimization rules to aggregation pipelines.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class QueryOptimizer {

    private final List<OptimizerRule> rules = new ArrayList<>();

    /**
     * Create optimizer with default rules.
     */
    public QueryOptimizer() {
        this(true);
    }

    /**
     * Create optimizer.
     *
     * @param addDefaultRules whether to add default rules
     */
    public QueryOptimizer(boolean addDefaultRules) {
        if (addDefaultRules) {
            this.addDefaultRules();
        }
    }

    /**
     * Add default optimization rules.
     */
    private void addDefaultRules() {
        // Add rules in priority order
        this.rules.add(new MatchMergeRule());
        this.rules.add(new MatchPushDownRule());
        this.rules.add(new ProjectSimplifyRule());
        this.rules.add(new SortLimitOptimizationRule());

        // Sort by priority
        this.rules.sort(Comparator.comparingInt(OptimizerRule::getPriority));
    }

    /**
     * Add a custom optimization rule.
     */
    public QueryOptimizer addRule(OptimizerRule rule) {
        this.rules.add(rule);
        this.rules.sort(Comparator.comparingInt(OptimizerRule::getPriority));

        return this;
    }

    /**
     * Remove a rule by class.
     */
    public QueryOptimizer removeRule(Class<? extends OptimizerRule> ruleClass) {
        this.rules.removeIf(r -> r.getClass().equals(ruleClass));

        return this;
    }

    /**
     * Enable/disable a rule.
     */
    public QueryOptimizer setRuleEnabled(Class<? extends OptimizerRule> ruleClass, boolean enabled) {
        if (!enabled) {
            this.removeRule(ruleClass);
        }
        return this;
    }

    /**
     * Optimize the pipeline by applying all matching rules.
     */
    public Pipeline optimize(Pipeline pipeline) {
        if (Objects.isNull(pipeline) || Collections.isEmpty(pipeline.getStages())) {
            return pipeline;
        }

        Pipeline optimized = pipeline;
        for (OptimizerRule rule : rules) {
            if (rule.matches(optimized)) {
                optimized = rule.apply(optimized);
            }
        }

        return optimized;
    }

    /**
     * Get all registered rules.
     */
    public List<OptimizerRule> getRules() {
        return new ArrayList<>(this.rules);
    }

    /**
     * Clear all rules.
     */
    public void clearRules() {
        this.rules.clear();
    }
}
