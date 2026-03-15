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
package io.github.photowey.mongoplus.executor.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.photowey.mongoplus.core.util.Collections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BatchResult - Result of batch operations.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult implements Serializable {

    private static final long serialVersionUID = 3674036273088823510L;

    /**
     * Number of documents inserted.
     */
    @Builder.Default
    private int insertedCount = 0;

    /**
     * Number of documents modified.
     */
    @Builder.Default
    private int modifiedCount = 0;

    /**
     * Number of documents deleted.
     */
    @Builder.Default
    private int deletedCount = 0;

    /**
     * Number of documents matched.
     */
    @Builder.Default
    private int matchedCount = 0;

    /**
     * Whether the operation was acknowledged by the server.
     */
    private boolean acknowledged;

    /**
     * List of errors during batch operation.
     */
    @Builder.Default
    private List<BatchError> errors = new ArrayList<>();

    /**
     * Check if the batch operation was successful (no errors).
     *
     * @return true if no errors occurred
     */
    public boolean isSuccess() {
        return this.errors.isEmpty();
    }

    /**
     * Get total affected count (inserted + modified + deleted).
     *
     * @return total affected documents
     */
    public int getTotalAffected() {
        return this.insertedCount + this.modifiedCount + this.deletedCount;
    }

    /**
     * Add an error to the result.
     *
     * @param index   the index in the batch where error occurred
     * @param message the error message
     */
    public BatchResult addError(int index, String message) {
        this.errors.add(new BatchError(index, message));
        return this;
    }

    /**
     * Create a batch result with a single error.
     *
     * @param index   the index in the batch where error occurred
     * @param message the error message
     * @return batch result with one error (caller can use builder() for more)
     */
    public static BatchResult withError(int index, String message) {
        return BatchResult.builder()
            .errors(Collections.singletonList(new BatchError(index, message)))
            .build();
    }

    /**
     * BatchError - Error information for a single failed operation.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchError implements Serializable {

        private static final long serialVersionUID = -5102514184643550384L;

        /**
         * Index in the original batch list.
         */
        private int index;

        /**
         * Error message.
         */
        private String message;
    }
}
