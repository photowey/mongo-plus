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
package io.github.photowey.mongoplus.core.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.photowey.mongoplus.core.util.Collections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Page - Pagination result container.
 * Compatible with MyBatis-Plus Page API.
 *
 * @param <T> the record type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Data
@Builder
@AllArgsConstructor
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -3701288689088886273L;

    /**
     * Current page number (1-based).
     */
    private long current;

    /**
     * Page size (records per page).
     */
    private long size;

    /**
     * Total records count.
     * Set to -1 when searchCount=false.
     */
    private long total;

    /**
     * Total pages count.
     */
    private long pages;

    /**
     * Records in current page.
     */
    private List<T> records;

    /**
     * Whether to execute count query.
     * Set to false to skip count for performance.
     */
    private boolean searchCount;

    /**
     * Default constructor with current=1, size=10.
     */
    public Page() {
        this(1, 10);
    }

    /**
     * Constructor with current and size.
     *
     * @param current the current page (1-based)
     * @param size    the page size
     */
    public Page(long current, long size) {
        this(current, size, true);
    }

    /**
     * Constructor with all pagination parameters.
     *
     * @param current     the current page (1-based)
     * @param size        the page size
     * @param searchCount whether to execute count query
     */
    public Page(long current, long size, boolean searchCount) {
        this.current = Math.max(current, 1);
        this.size = Math.max(size, 1);
        this.searchCount = searchCount;
        this.total = 0;
        this.pages = 0;
        this.records = new ArrayList<>();
    }

    /**
     * Calculate pages from total and size.
     */
    public void calculatePages() {
        if (this.size > 0) {
            this.pages = (this.total + this.size - 1) / this.size;
        } else {
            this.pages = 0;
        }
    }

    /**
     * Get the offset for skip (0-based).
     *
     * @return skip offset
     */
    public long getOffset() {
        return (this.current - 1) * this.size;
    }

    /**
     * Check if has previous page.
     *
     * @return true if current > 1
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }

    /**
     * Check if has next page.
     *
     * @return true if current &lt; pages
     */
    public boolean hasNext() {
        return this.current < this.pages;
    }

    /**
     * Create an empty Page with given current and size.
     *
     * @param current the current page
     * @param size    the page size
     * @param <T>     the record type
     * @return empty Page instance
     */
    public static <T> Page<T> empty(long current, long size) {
        Page<T> page = new Page<>(current, size);
        page.setRecords(Collections.emptyList());
        page.setTotal(0);
        page.setPages(0);

        return page;
    }
}
