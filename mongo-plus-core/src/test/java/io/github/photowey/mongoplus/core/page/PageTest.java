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

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PageTest - Unit tests for Page pagination behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("PageTest")
class PageTest {

    @Test
    @DisplayName("Given no args When new Page Then has default current size total and empty records")
    @Story("Create default page with empty records")
    void givenNoArgs_whenNewPage_thenHasDefaultCurrentSizeTotalAndEmptyRecords() {
        Page<String> page = new Page<>();

        assertEquals(1, page.getCurrent());
        assertEquals(10, page.getSize());
        assertEquals(0, page.getTotal());
        assertEquals(0, page.getPages());
        assertTrue(page.getRecords().isEmpty());
        assertTrue(page.isSearchCount());
    }

    @Test
    @DisplayName("Given current and size When new Page Then sets current size and searchCount true")
    @Story("Create page with current and size")
    void givenCurrentAndSize_whenNewPage_thenSetsCurrentSizeAndSearchCountTrue() {
        Page<String> page = new Page<>(2, 20);

        assertEquals(2, page.getCurrent());
        assertEquals(20, page.getSize());
        assertTrue(page.isSearchCount());
    }

    @Test
    @DisplayName("Given searchCount false When new Page Then isSearchCount is false")
    @Story("Page with search count disabled")
    void givenSearchCountFalse_whenNewPage_thenIsSearchCountFalse() {
        Page<String> page = new Page<>(1, 10, false);

        assertFalse(page.isSearchCount());
    }

    @Test
    @DisplayName("Given negative current and size When new Page Then normalizes to at least 1")
    @Story("Normalize negative current and size to at least one")
    void givenNegativeCurrentAndSize_whenNewPage_thenNormalizesToAtLeastOne() {
        Page<String> page = new Page<>(-1, -5);

        assertEquals(1, page.getCurrent());
        assertEquals(1, page.getSize());
    }

    @Test
    @DisplayName("Given total When calculatePages Then sets pages correctly")
    @Story("Calculate total pages from total count")
    void givenTotal_whenCalculatePages_thenSetsPagesCorrectly() {
        Page<String> page = new Page<>(1, 10);
        page.setTotal(95);
        page.calculatePages();

        assertEquals(10, page.getPages());
    }

    @Test
    @DisplayName("Given current and size When getOffset Then returns (current-1)*size")
    @Story("Compute page offset from current and size")
    void givenCurrentAndSize_whenGetOffset_thenReturnsCurrentMinusOneTimesSize() {
        Page<String> page = new Page<>(3, 10);

        assertEquals(20, page.getOffset());
    }

    @Test
    @DisplayName("Given current page When hasPrevious Then returns true only when current > 1")
    @Story("Check has previous page")
    void givenCurrentPage_whenHasPrevious_thenReturnsTrueOnlyWhenCurrentGreaterThanOne() {
        Page<String> page1 = new Page<>(1, 10);
        assertFalse(page1.hasPrevious());

        Page<String> page2 = new Page<>(2, 10);
        assertTrue(page2.hasPrevious());
    }

    @Test
    @DisplayName("Given total and current When hasNext Then returns true when current < pages")
    @Story("Check has next page")
    void givenTotalAndCurrent_whenHasNext_thenReturnsTrueWhenCurrentLessThanPages() {
        Page<String> page = new Page<>(1, 10);
        page.setTotal(95);
        page.calculatePages();

        assertTrue(page.hasNext());

        page.setCurrent(10);
        assertFalse(page.hasNext());
    }

    @Test
    @DisplayName("Given current and size When Page.empty Then returns empty page with zero total")
    @Story("Create empty page with zero total")
    void givenCurrentAndSize_whenPageEmpty_thenReturnsEmptyPageWithZeroTotal() {
        Page<String> page = Page.empty(5, 10);

        assertEquals(5, page.getCurrent());
        assertEquals(10, page.getSize());
        assertEquals(0, page.getTotal());
        assertEquals(0, page.getPages());
        assertTrue(page.getRecords().isEmpty());
    }

    @Test
    @DisplayName("Given records When setRecords Then getRecords returns same list")
    @Story("Set and get page records")
    void givenRecords_whenSetRecords_thenGetRecordsReturnsSameList() {
        Page<String> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList("a", "b", "c"));

        assertEquals(3, page.getRecords().size());
    }
}
