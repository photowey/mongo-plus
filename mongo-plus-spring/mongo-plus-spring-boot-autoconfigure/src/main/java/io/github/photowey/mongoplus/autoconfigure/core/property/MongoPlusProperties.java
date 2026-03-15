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
package io.github.photowey.mongoplus.autoconfigure.core.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoPlusProperties - Configuration properties for MongoPlus.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = MongoPlusConstants.Configuration.MONGO_PLUS_PREFIX)
public class MongoPlusProperties implements Serializable {

    private static final long serialVersionUID = -4321736127058717362L;

    /**
     * Enable MongoPlus auto-configuration.
     */
    private boolean enabled = true;

    /**
     * Mapper scan configuration.
     */
    private final Mapper mapper = new Mapper();

    /**
     * Optimizer configuration.
     */
    private final Optimizer optimizer = new Optimizer();

    private final Option option = new Option();

    /**
     * ID generation configuration.
     */
    private final Identity identity = new Identity();

    /**
     * Mapper configuration.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mapper implements Serializable {

        private static final long serialVersionUID = -8146465425392012529L;

        /**
         * Base packages to scan for mapper interfaces.
         * Spring Boot binds comma-separated values to List by default (e.g. base-packages: pkg1,pkg2).
         */
        private List<String> basePackages = new ArrayList<>();

        /**
         * Marker interface to identify mappers.
         */
        private String markerInterface = MongoPlusConstants.DEFAULT_MARKER_INTERFACE;

        private boolean processPropertyPlaceHolders = false;
        private String lazyInitialization = "false";

        // ----------------------------------------------------------------

        public List<String> basePackages() {
            return basePackages;
        }

        public String markerInterface() {
            return markerInterface;
        }

        public boolean processPropertyPlaceHolders() {
            return processPropertyPlaceHolders;
        }

        public String lazyInitialization() {
            return Objects.defaultIfNull(lazyInitialization, "false");
        }
    }

    /**
     * Optimizer configuration.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Optimizer implements Serializable {

        private static final long serialVersionUID = 8620430525663006188L;

        /**
         * Enable query optimization.
         */
        private boolean enabled = true;

        /**
         * Enable match merge rule.
         */
        private boolean matchMerge = true;

        /**
         * Enable match push down rule.
         */
        private boolean matchPushDown = true;

        /**
         * Enable project simplify rule.
         */
        private boolean projectSimplify = true;

        /**
         * Enable sort limit optimization rule.
         */
        private boolean sortLimitOptimization = true;
    }

    // ----------------------------------------------------------------

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option implements Serializable {

        private static final long serialVersionUID = -6441788028339022818L;

        private boolean enabled = false;

        private Integer minConnectionPerHost = 0;
        private Integer maxConnectionPerHost = 100;
        private Integer threadsAllowedToBlockForConnectionMultiplier = 5;
        private Integer serverSelectionTimeout = 30000;
        private Integer maxWaitTime = 120000;
        private Integer maxConnectionIdleTime = 0;
        private Integer maxConnectionLifeTime = 0;
        private Integer connectTimeout = 10000;
        private Integer socketTimeout = 0;
        private Boolean socketKeepAlive = false;
        private Boolean sslEnabled = false;
        private Boolean sslInvalidHostNameAllowed = false;
        private Boolean alwaysUseMBeans = false;
        private Integer heartbeatFrequency = 10000;
        private Integer minHeartbeatFrequency = 500;
        private Integer heartbeatConnectTimeout = 20000;
        private Integer heartbeatSocketTimeout = 20000;
        private Integer localThreshold = 15;

        // ----------------------------------------------------------------

        public boolean enabled() {
            return enabled;
        }

        public Integer minConnectionPerHost() {
            return minConnectionPerHost;
        }

        public Integer maxConnectionPerHost() {
            return maxConnectionPerHost;
        }

        public Integer threadsAllowedToBlockForConnectionMultiplier() {
            return threadsAllowedToBlockForConnectionMultiplier;
        }

        public Integer serverSelectionTimeout() {
            return serverSelectionTimeout;
        }

        public Integer maxWaitTime() {
            return maxWaitTime;
        }

        public Integer maxConnectionIdleTime() {
            return maxConnectionIdleTime;
        }

        public Integer maxConnectionLifeTime() {
            return maxConnectionLifeTime;
        }

        public Integer connectTimeout() {
            return connectTimeout;
        }

        public Integer socketTimeout() {
            return socketTimeout;
        }

        public Boolean socketKeepAlive() {
            return socketKeepAlive;
        }

        public Boolean sslEnabled() {
            return sslEnabled;
        }

        public Boolean sslInvalidHostNameAllowed() {
            return sslInvalidHostNameAllowed;
        }

        public Boolean alwaysUseMBeans() {
            return alwaysUseMBeans;
        }

        public Integer heartbeatFrequency() {
            return heartbeatFrequency;
        }

        public Integer minHeartbeatFrequency() {
            return minHeartbeatFrequency;
        }

        public Integer heartbeatConnectTimeout() {
            return heartbeatConnectTimeout;
        }

        public Integer heartbeatSocketTimeout() {
            return heartbeatSocketTimeout;
        }

        public Integer localThreshold() {
            return localThreshold;
        }
    }

    /**
     * ID generation configuration.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identity implements Serializable {

        private static final long serialVersionUID = 8620430525663006189L;

        /**
         * Enable ID generation.
         * Default is true.
         */
        private boolean enabled = true;

        /**
         * Snowflake worker ID (0-31).
         * Auto-detected if not specified.
         */
        private Long workerId;

        /**
         * Snowflake data center ID (0-31).
         * Auto-detected if not specified.
         */
        private Long dataCenterId;

        // ----------------------------------------------------------------

        public boolean enabled() {
            return enabled;
        }

        public Long workerId() {
            return workerId;
        }

        public Long dataCenterId() {
            return dataCenterId;
        }
    }

    // ----------------------------------------------------------------

    public Mapper mapper() {
        return mapper;
    }

    public Optimizer optimizer() {
        return optimizer;
    }

    public Identity identity() {
        return identity;
    }
}
