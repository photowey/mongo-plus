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
package io.github.photowey.mongoplus.examples.spring.boot.b2x.wrapper;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.query.builder.QueryBuilder;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaUpdateWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WrapperIntegrationTest - Integration tests for all Wrapper types under Testcontainers MongoDB.
 * Covers LambdaQueryWrapper, QueryWrapper, LambdaUpdateWrapper, UpdateWrapper.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("WrapperIntegrationTest")
class WrapperIntegrationTest extends AbstractMongoTest {

    static final String COLLECTION = "users";

    @Autowired
    private QueryExecutor queryExecutor;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearUsers() {
        this.mongoTemplate.remove(new Query(), UserDocument.class);
    }

    @Nested
    @DisplayName("LambdaQueryWrapper")
    @Feature("LambdaQueryWrapperTests")
    class LambdaQueryWrapperTests {

        @Test
        @DisplayName("Given one user When eq by name Then selectList returns one")
        @Story("Filter by field equality")
        void givenOneUser_whenEqByName_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 20));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "A");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("A", list.get(0).getName());
        }

        @Test
        @DisplayName("Given two users When ne name Then selectList returns the other")
        @Story("Filter by field inequality")
        void givenTwoUsers_whenNeName_thenSelectListReturnsTheOther() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 20));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 21));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.ne(UserDocument::getName, "A");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("B", list.get(0).getName());
        }

        @Test
        @DisplayName("Given three users When gte and lte age Then selectList returns three")
        @Story("Filter by range (gte, lte)")
        void givenThreeUsers_whenGteAndLteAge_thenSelectListReturnsThree() {
            mongoTemplate.insert(new UserDocument("X", "x@e.com", 25));
            mongoTemplate.insert(new UserDocument("Y", "y@e.com", 30));
            mongoTemplate.insert(new UserDocument("Z", "z@e.com", 35));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.gte(UserDocument::getAge, 25).lte(UserDocument::getAge, 35);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Given three users When gt and lt age Then selectList returns one")
        @Story("Filter by range (gt, lt)")
        void givenThreeUsers_whenGtAndLtAge_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("X", "x@e.com", 25));
            mongoTemplate.insert(new UserDocument("Y", "y@e.com", 30));
            mongoTemplate.insert(new UserDocument("Z", "z@e.com", 35));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.gt(UserDocument::getAge, 25).lt(UserDocument::getAge, 35);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals(30, list.get(0).getAge());
        }

        @Test
        @DisplayName("Given three users When in names Then selectList returns two")
        @Story("Filter with IN")
        void givenThreeUsers_whenInNames_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 10));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 20));
            mongoTemplate.insert(new UserDocument("C", "c@e.com", 30));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.in(UserDocument::getName, Arrays.asList("A", "C"));
            assertEquals(2, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given three users When nin name Then selectList returns two")
        @Story("Filter with NOT IN")
        void givenThreeUsers_whenNinName_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 10));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 20));
            mongoTemplate.insert(new UserDocument("C", "c@e.com", 30));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.nin(UserDocument::getName, Arrays.asList("B"));
            assertEquals(2, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given three users When between age 20 30 Then selectList returns one")
        @Story("Filter with between")
        void givenThreeUsers_whenBetweenAge20_30_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 18));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 25));
            mongoTemplate.insert(new UserDocument("C", "c@e.com", 40));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.between(UserDocument::getAge, 20, 30);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals(25, list.get(0).getAge());
        }

        @Test
        @DisplayName("Given two names matching pattern When like Then selectList returns two")
        @Story("Filter with LIKE pattern")
        void givenTwoNamesMatchingPattern_whenLike_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("Alice", "alice@e.com", 22));
            mongoTemplate.insert(new UserDocument("Alicia", "alicia@e.com", 23));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.like(UserDocument::getName, "Alic");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Given five users When orderByAsc skip 1 limit 2 Then selectList returns second and third")
        @Story("Sort, skip and limit")
        void givenFiveUsers_whenOrderByAscSkip1Limit2_thenSelectListReturnsSecondAndThird() {
            for (int i = 0; i < 5; i++) {
                mongoTemplate.insert(new UserDocument("U" + i, "u" + i + "@e.com", 20 + i));
            }
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.orderByAsc(UserDocument::getAge).skip(1).limit(2);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(21, list.get(0).getAge());
            assertEquals(22, list.get(1).getAge());
        }

        @Test
        @DisplayName("Given one user When selectOne by eq Then returns document")
        @Story("Select single document")
        void givenOneUser_whenSelectOneByEq_thenReturnsDocument() {
            mongoTemplate.insert(new UserDocument("One", "one@e.com", 1));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "One");
            UserDocument one = queryExecutor.selectOne(w, UserDocument.class);
            assertNotNull(one);
        }

        @Test
        @DisplayName("Given one user When exists with eq Then true")
        @Story("Check document exists")
        void givenOneUser_whenExistsWithEq_thenTrue() {
            mongoTemplate.insert(new UserDocument("One", "one@e.com", 1));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "One");
            assertTrue(queryExecutor.exists(w, UserDocument.class));
        }

        @Test
        @DisplayName("Given one user When selectCount with eq Then one")
        @Story("Count documents by query")
        void givenOneUser_whenSelectCountWithEq_thenOne() {
            mongoTemplate.insert(new UserDocument("One", "one@e.com", 1));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "One");
            assertEquals(1, queryExecutor.selectCount(w, COLLECTION));
        }

        @Test
        @DisplayName("Given 12 users When selectPage first 5 Then total 12 and 5 records")
        @Description("Integration: pagination via QueryExecutor.selectPage with total count and page records against MongoDB.")
        @Story("Paginate with total count and page records")
        @Severity(SeverityLevel.CRITICAL)
        void given12Users_whenSelectPageFirst5_thenTotal12And5Records() {
            for (int i = 0; i < 12; i++) {
                mongoTemplate.insert(new UserDocument("U" + i, "u" + i + "@e.com", 20 + i));
            }
            Page<UserDocument> page = new Page<>(1, 5, true);
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.orderByAsc(UserDocument::getAge);
            queryExecutor.selectPage(page, w, UserDocument.class);
            assertEquals(1, page.getCurrent());
            assertEquals(5, page.getSize());
            assertEquals(12, page.getTotal());
            assertEquals(5, page.getRecords().size());
        }

        @Test
        @DisplayName("Given two users When eq name and eq age Then selectList returns one")
        @Story("Filter with multiple conditions")
        void givenTwoUsers_whenEqNameAndEqAge_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 25));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 30));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "A").eq(UserDocument::getAge, 25);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Given two names When likeLeft Then selectList returns two")
        @Story("Filter with left LIKE")
        void givenTwoNames_whenLikeLeft_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("Tom", "tom@e.com", 20));
            mongoTemplate.insert(new UserDocument("Tommy", "t@e.com", 21));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.likeLeft(UserDocument::getName, "Tom");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Given two users When likeRight email Then selectList returns two")
        @Story("Filter with right LIKE pattern")
        void givenTwoUsers_whenLikeRightEmail_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("a", "a@e.com", 1));
            mongoTemplate.insert(new UserDocument("b", "b@e.com", 2));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.likeRight(UserDocument::getEmail, "@e.com");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Given two names matching regex When regex Then selectList returns two")
        @Story("Filter with regex pattern")
        void givenTwoNamesMatchingRegex_whenRegex_thenSelectListReturnsTwo() {
            mongoTemplate.insert(new UserDocument("Tom", "tom@e.com", 20));
            mongoTemplate.insert(new UserDocument("Tommy", "t@e.com", 21));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.regex(UserDocument::getName, ".*Tom.*");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Given one null email When isNull email Then selectList returns one")
        @Story("Filter by null field")
        void givenOneNullEmail_whenIsNullEmail_thenSelectListReturnsOne() {
            UserDocument noEmail = new UserDocument("NoEmail", null, 10);
            mongoTemplate.insert(noEmail);
            mongoTemplate.insert(new UserDocument("WithEmail", "x@e.com", 11));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.isNull(UserDocument::getEmail);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertNull(list.get(0).getEmail());
        }

        @Test
        @DisplayName("Given one with email one null When isNotNull email Then selectList returns one")
        @Story("Filter by non-null field")
        void givenOneWithEmailOneNull_whenIsNotNullEmail_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 1));
            UserDocument noEmail = new UserDocument("B", null, 2);
            mongoTemplate.insert(noEmail);
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.isNotNull(UserDocument::getEmail);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("A", list.get(0).getName());
        }

        @Test
        @DisplayName("Given one user When exists field true Then selectList returns one")
        @Story("Filter by field existence")
        void givenOneUser_whenExistsFieldTrue_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 1));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.exists(UserDocument::getName, true);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Given one with tags When all tags Then selectList returns one")
        @Description("Integration: array $all query. Verifies LambdaQueryWrapper.all() for embedded array match.")
        @Story("Filter array contains all elements")
        @Severity(SeverityLevel.NORMAL)
        void givenOneWithTags_whenAllTags_thenSelectListReturnsOne() {
            UserDocument withTags = new UserDocument("T", "t@e.com", 1);
            withTags.setTags(Arrays.asList("a", "b"));
            mongoTemplate.insert(withTags);
            mongoTemplate.insert(new UserDocument("NoTags", "n@e.com", 2));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.all(UserDocument::getTags, Arrays.asList("a", "b"));
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals(Arrays.asList("a", "b"), list.get(0).getTags());
        }

        @Test
        @DisplayName("Given one with two tags When size 2 Then selectList returns one")
        @Story("Filter array by size")
        void givenOneWithTwoTags_whenSize2_thenSelectListReturnsOne() {
            UserDocument withTags = new UserDocument("S", "s@e.com", 1);
            withTags.setTags(Arrays.asList("x", "y"));
            mongoTemplate.insert(withTags);
            mongoTemplate.insert(new UserDocument("Other", "o@e.com", 2));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.size(UserDocument::getTags, 2);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals(2, list.get(0).getTags().size());
        }

        @Test
        @DisplayName("Given two users When orderBy age asc Then first is younger")
        @Story("Sort ascending")
        void givenTwoUsers_whenOrderByAgeAsc_thenFirstIsYounger() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 30));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 20));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.orderBy(UserDocument::getAge, true);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(20, list.get(0).getAge());
            assertEquals(30, list.get(1).getAge());
        }

        @Test
        @DisplayName("Given one user When select name age Then result has name and age")
        @Story("Project selected fields")
        void givenOneUser_whenSelectNameAge_thenResultHasNameAndAge() {
            mongoTemplate.insert(new UserDocument("Sel", "sel@e.com", 25));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "Sel").select(UserDocument::getName, UserDocument::getAge);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("Sel", list.get(0).getName());
            assertEquals(25, list.get(0).getAge());
        }

        @Test
        @DisplayName("Given one user When exclude email Then result email null")
        @Story("Exclude field from result")
        void givenOneUser_whenExcludeEmail_thenResultEmailNull() {
            mongoTemplate.insert(new UserDocument("Ex", "ex@e.com", 10));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "Ex").exclude(UserDocument::getEmail);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertNull(list.get(0).getEmail());
        }

        @Test
        @DisplayName("Given 10 users When page 2 size 3 Then selectList returns 3")
        @Story("Page 2 size 3 returns 3 records")
        void given10Users_whenPage2Size3_thenSelectListReturns3() {
            for (int i = 0; i < 10; i++) {
                mongoTemplate.insert(new UserDocument("P" + i, "p@e.com", 20 + i));
            }
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.page(2, 3);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Given two users When or eq name and age Then selectList returns one")
        @Description("Integration: OR condition grouping. Verifies LambdaQueryWrapper.or() compiles correctly.")
        @Story("OR eq name and age returns one")
        @Severity(SeverityLevel.NORMAL)
        void givenTwoUsers_whenOrEqNameAndAge_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("O1", "o1@e.com", 10));
            mongoTemplate.insert(new UserDocument("O2", "o2@e.com", 20));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.or(o -> ((LambdaQueryWrapper<UserDocument>) o)
                .eq(UserDocument::getName, "O1")
                .eq(UserDocument::getAge, 10));
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("O1", list.get(0).getName());
        }

        @Test
        @DisplayName("Given wrapper with eq and orderBy When clone Then distinct instance same structure")
        @Story("Clone wrapper with eq and orderBy")
        void givenWrapperWithEqAndOrderBy_whenClone_thenDistinctInstanceSameStructure() {
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "X").orderByAsc(UserDocument::getAge);
            LambdaQueryWrapper<UserDocument> c = w.clone();
            assertNotSame(w, c);
            assertEquals(w.getConditions().size(), c.getConditions().size());
            assertEquals(w.getSorts().size(), c.getSorts().size());
        }
    }

    @Nested
    @DisplayName("QueryWrapper")
    @Feature("QueryWrapperTests")
    class QueryWrapperTests {

        @Test
        @DisplayName("Given one user When eq and gte lte Then selectList returns one")
        @Story("Eq and gte lte returns one")
        void givenOneUser_whenEqAndGteLte_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Q", "q@e.com", 28));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.eq("name", "Q").gte("age", 20).lte("age", 30);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Given four users When in names and limit 1 Then selectList returns one")
        @Story("In names with limit 1 returns one")
        void givenFourUsers_whenInNamesAndLimit1_thenSelectListReturnsOne() {
            for (int i = 0; i < 4; i++) {
                mongoTemplate.insert(new UserDocument("Q" + i, "q" + i + "@e.com", 20 + i));
            }
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.in("name", Arrays.asList("Q0", "Q2")).limit(1);
            assertEquals(1, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given six users When selectPage page 2 size 2 Then total 6 and 2 records")
        @Story("Select page 2 size 2 returns total 6 and 2 records")
        void givenSixUsers_whenSelectPagePage2Size2_thenTotal6And2Records() {
            for (int i = 0; i < 6; i++) {
                mongoTemplate.insert(new UserDocument("Q" + i, "q@e.com", 20 + i));
            }
            Page<UserDocument> page = new Page<>(2, 2, true);
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            queryExecutor.selectPage(page, w, UserDocument.class);
            assertEquals(6, page.getTotal());
            assertEquals(2, page.getRecords().size());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper between age Then selectList returns one")
        @Story("QueryWrapper between age filter")
        void givenOneUser_whenQueryWrapperBetweenAge_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 25));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.between("age", 20, 30);
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals(25, list.get(0).getAge().intValue());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper like name Then selectList returns one")
        @Story("QueryWrapper like name filter")
        void givenOneUser_whenQueryWrapperLikeName_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Like", "like@e.com", 1));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.like("name", "Lik");
            assertEquals(1, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given two users When QueryWrapper notIn name Then selectList returns one")
        @Story("QueryWrapper notIn name filter")
        void givenTwoUsers_whenQueryWrapperNotInName_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("N1", "n1@e.com", 1));
            mongoTemplate.insert(new UserDocument("N2", "n2@e.com", 2));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.notIn("name", Arrays.asList("N2"));
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("N1", list.get(0).getName());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper likeLeft and likeRight Then selectList returns one")
        @Story("QueryWrapper likeLeft and likeRight")
        void givenOneUser_whenQueryWrapperLikeLeftAndLikeRight_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Left", "left@e.com", 1));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.likeLeft("name", "Le").likeRight("email", "@e.com");
            assertEquals(1, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper regex name Then selectList returns one")
        @Story("Filter with regex on name")
        void givenOneUser_whenQueryWrapperRegexName_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Regex", "r@e.com", 1));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.regex("name", ".*Re.*");
            assertEquals(1, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one with email one null When QueryWrapper isNull email Then selectList returns one")
        @Story("Filter by null email")
        void givenOneWithEmailOneNull_whenQueryWrapperIsNullEmail_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Has", "has@e.com", 1));
            UserDocument noEmail = new UserDocument("No", null, 2);
            mongoTemplate.insert(noEmail);
            QueryWrapper<UserDocument> wNull = Wrappers.query(UserDocument.class);
            wNull.isNull("email");
            assertEquals(1, queryExecutor.selectList(wNull, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one with email one null When QueryWrapper isNotNull email Then selectList returns one")
        @Story("Filter by non-null email")
        void givenOneWithEmailOneNull_whenQueryWrapperIsNotNullEmail_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("Has", "has@e.com", 1));
            UserDocument noEmail = new UserDocument("No", null, 2);
            mongoTemplate.insert(noEmail);
            QueryWrapper<UserDocument> wNotNull = Wrappers.query(UserDocument.class);
            wNotNull.isNotNull("email");
            assertEquals(1, queryExecutor.selectList(wNotNull, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper exists name true Then selectList returns one")
        @Story("Filter by exists name")
        void givenOneUser_whenQueryWrapperExistsNameTrue_thenSelectListReturnsOne() {
            mongoTemplate.insert(new UserDocument("E", "e@e.com", 1));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.exists("name", true);
            assertEquals(1, queryExecutor.selectList(w, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given two users When QueryWrapper orderByAsc age Then first is younger")
        @Story("Order by age ascending")
        void givenTwoUsers_whenQueryWrapperOrderByAscAge_thenFirstIsYounger() {
            mongoTemplate.insert(new UserDocument("D1", "d@e.com", 30));
            mongoTemplate.insert(new UserDocument("D2", "d@e.com", 20));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.orderByAsc("age");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(20, list.get(0).getAge().intValue());
            assertEquals(30, list.get(1).getAge().intValue());
        }

        @Test
        @DisplayName("Given one user When QueryWrapper select name age Then result has name and age")
        @Story("Select name and age fields")
        void givenOneUser_whenQueryWrapperSelectNameAge_thenResultHasNameAndAge() {
            mongoTemplate.insert(new UserDocument("S", "s@e.com", 5));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.eq("name", "S").select("name", "age");
            List<UserDocument> list = queryExecutor.selectList(w, UserDocument.class);
            assertEquals(1, list.size());
            assertNotNull(list.get(0).getName());
            assertNotNull(list.get(0).getAge());
        }

        @Test
        @DisplayName("Given QueryWrapper with eq orderBy When clone Then distinct instance same structure")
        @Story("Clone QueryWrapper with eq and orderBy")
        void givenQueryWrapperWithEqOrderBy_whenClone_thenDistinctInstanceSameStructure() {
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.eq("name", "C").orderByAsc("age");
            QueryWrapper<UserDocument> c = w.clone();
            assertNotSame(w, c);
            assertEquals(w.getConditions().size(), c.getConditions().size());
            assertEquals(w.getSorts().size(), c.getSorts().size());
        }
    }

    @Nested
    @DisplayName("LambdaUpdateWrapper")
    @Feature("LambdaUpdateWrapperTests")
    class LambdaUpdateWrapperTests {

        @Test
        @DisplayName("Given one user When LambdaUpdate set and inc by id Then document updated")
        @Description("Integration: LambdaUpdateWrapper set/inc with QueryExecutor.update against MongoDB.")
        @Story("Lambda update set and inc by id")
        @Severity(SeverityLevel.CRITICAL)
        void givenOneUser_whenLambdaUpdateSetAndIncById_thenDocumentUpdated() {
            UserDocument u = new UserDocument("Old", "old@e.com", 10);
            mongoTemplate.insert(u);
            assertNotNull(u.getId());
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getName, "New")
                .inc(UserDocument::getAge, 5)
                .eq(UserDocument::getId, u.getId());
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = mongoTemplate.findById(u.getId(), UserDocument.class);
            assertNotNull(found);
            assertEquals("New", found.getName());
            assertEquals(15, found.getAge().intValue());
        }

        @Test
        @DisplayName("Given two same name When LambdaUpdate set age by name Then both updated")
        @Story("Lambda update set age by name for multiple docs")
        void givenTwoSameName_whenLambdaUpdateSetAgeByName_thenBothUpdated() {
            mongoTemplate.insert(new UserDocument("X", "x@e.com", 100));
            mongoTemplate.insert(new UserDocument("X", "x2@e.com", 100));
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getAge, 99)
                .eq(UserDocument::getName, "X");
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            LambdaQueryWrapper<UserDocument> q = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            q.eq(UserDocument::getAge, 99);
            assertEquals(2, queryExecutor.selectList(q, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one user When LambdaUpdate setNull email by id Then email null")
        @Story("Lambda update set null email by id")
        void givenOneUser_whenLambdaUpdateSetNullEmailById_thenEmailNull() {
            UserDocument u = new UserDocument("LNull", "remove@e.com", 1);
            mongoTemplate.insert(u);
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .setNull(UserDocument::getEmail)
                .eq(UserDocument::getId, u.getId());
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = mongoTemplate.findById(u.getId(), UserDocument.class);
            assertNotNull(found);
            assertNull(found.getEmail());
        }

        @Test
        @DisplayName("Given one user When LambdaUpdate set with ne gt lt Then one updated")
        @Story("Lambda update with ne gt lt conditions")
        void givenOneUser_whenLambdaUpdateSetWithNeGtLt_thenOneUpdated() {
            mongoTemplate.insert(new UserDocument("N", "n@e.com", 50));
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getAge, 51)
                .ne(UserDocument::getName, "Other")
                .gt(UserDocument::getAge, 40)
                .lt(UserDocument::getAge, 60);
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            LambdaQueryWrapper<UserDocument> q = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            q.eq(UserDocument::getAge, 51);
            assertEquals(1, queryExecutor.selectList(q, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one user When LambdaUpdate set age by like name Then updated")
        @Story("Lambda update set age by like name")
        void givenOneUser_whenLambdaUpdateSetAgeByLikeName_thenUpdated() {
            mongoTemplate.insert(new UserDocument("LikeU", "like@e.com", 1));
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getAge, 2)
                .like(UserDocument::getName, "Like");
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = queryExecutor.selectOne(
                Wrappers.<UserDocument>lambdaQuery(UserDocument.class).eq(UserDocument::getName, "LikeU"),
                UserDocument.class);
            assertNotNull(found);
            assertEquals(2, found.getAge().intValue());
        }

        @Test
        @DisplayName("Given one null email When LambdaUpdate set age by isNull email Then updated")
        @Story("Lambda update set age by isNull email")
        void givenOneNullEmail_whenLambdaUpdateSetAgeByIsNullEmail_thenUpdated() {
            UserDocument noEmail = new UserDocument("NoE", null, 1);
            mongoTemplate.insert(noEmail);
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getAge, 2)
                .isNull(UserDocument::getEmail);
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = mongoTemplate.findById(noEmail.getId(), UserDocument.class);
            assertEquals(2, found.getAge().intValue());
        }

        @Test
        @DisplayName("Given LambdaUpdateWrapper with set eq When clone Then distinct instance same structure")
        @Story("Clone LambdaUpdateWrapper with set and eq")
        void givenLambdaUpdateWrapperWithSetEq_whenClone_thenDistinctInstanceSameStructure() {
            LambdaUpdateWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
                .set(UserDocument::getName, "Y")
                .eq(UserDocument::getId, 1L);
            LambdaUpdateWrapper<UserDocument> c = w.clone();
            assertNotSame(w, c);
            assertEquals(w.getSetValues().size(), c.getSetValues().size());
            assertEquals(w.getConditions().size(), c.getConditions().size());
        }
    }

    @Nested
    @DisplayName("UpdateWrapper")
    @Feature("UpdateWrapperTests")
    class UpdateWrapperTests {

        @Test
        @DisplayName("Given one user When UpdateWrapper set and inc by id Then document updated")
        @Description("Integration: UpdateWrapper set/inc with QueryExecutor.update against MongoDB.")
        @Story("UpdateWrapper set and inc by id")
        @Severity(SeverityLevel.CRITICAL)
        void givenOneUser_whenUpdateWrapperSetAndIncById_thenDocumentUpdated() {
            UserDocument u = new UserDocument("U", "u@e.com", 1);
            mongoTemplate.insert(u);
            assertNotNull(u.getId());
            UpdateWrapper<UserDocument> w = Wrappers.update(UserDocument.class);
            w.set("name", "Updated").inc("age", 10).eq(ID, u.getId());
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = mongoTemplate.findById(u.getId(), UserDocument.class);
            assertNotNull(found);
            assertEquals("Updated", found.getName());
            assertEquals(11, found.getAge().intValue());
        }

        @Test
        @DisplayName("Given one user When UpdateWrapper setNull email by id Then email null")
        @Story("UpdateWrapper set null email by id")
        void givenOneUser_whenUpdateWrapperSetNullEmailById_thenEmailNull() {
            UserDocument u = new UserDocument("NullEmail", "remove@e.com", 1);
            mongoTemplate.insert(u);
            UpdateWrapper<UserDocument> w = Wrappers.update(UserDocument.class);
            w.setNull("email").eq(ID, u.getId());
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            UserDocument found = mongoTemplate.findById(u.getId(), UserDocument.class);
            assertNotNull(found);
            assertNull(found.getEmail());
        }

        @Test
        @DisplayName("Given one user When UpdateWrapper set with and condition Then one updated")
        @Story("UpdateWrapper set with AND condition")
        void givenOneUser_whenUpdateWrapperSetWithAndCondition_thenOneUpdated() {
            mongoTemplate.insert(new UserDocument("And", "and@e.com", 10));
            UpdateWrapper<UserDocument> w = Wrappers.update(UserDocument.class);
            w.set("age", 99).and(inner -> inner.eq("name", "And").eq("age", 10));
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            LambdaQueryWrapper<UserDocument> q = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            q.eq(UserDocument::getAge, 99);
            assertEquals(1, queryExecutor.selectList(q, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given one user When UpdateWrapper set with or condition Then one updated")
        @Story("UpdateWrapper set with OR condition")
        void givenOneUser_whenUpdateWrapperSetWithOrCondition_thenOneUpdated() {
            mongoTemplate.insert(new UserDocument("Or1", "o@e.com", 1));
            UpdateWrapper<UserDocument> w = Wrappers.update(UserDocument.class);
            w.set("age", 88).or(inner -> inner.eq("name", "Or1"));
            boolean updated = queryExecutor.update(w, UserDocument.class);
            assertTrue(updated);
            LambdaQueryWrapper<UserDocument> q = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            q.eq(UserDocument::getAge, 88);
            assertEquals(1, queryExecutor.selectList(q, UserDocument.class).size());
        }

        @Test
        @DisplayName("Given empty UpdateWrapper When set Then hasUpdates true")
        @Story("Empty UpdateWrapper hasUpdates after set")
        void givenEmptyUpdateWrapper_whenSet_thenHasUpdatesTrue() {
            UpdateWrapper<UserDocument> empty = Wrappers.update(UserDocument.class);
            assertFalse(empty.hasUpdates());
            empty.set("name", "X");
            assertTrue(empty.hasUpdates());
        }

        @Test
        @DisplayName("Given UpdateWrapper with set inc eq When clone Then distinct instance same structure")
        @Story("UpdateWrapper clone preserves structure")
        void givenUpdateWrapperWithSetIncEq_whenClone_thenDistinctInstanceSameStructure() {
            UpdateWrapper<UserDocument> w = Wrappers.update(UserDocument.class);
            w.set("name", "C").inc("age", 1).eq(ID, 1L);
            UpdateWrapper<UserDocument> c = w.clone();
            assertNotSame(w, c);
            assertEquals(w.getSetValues().size(), c.getSetValues().size());
            assertEquals(w.getIncValues().size(), c.getIncValues().size());
        }
    }

    @Nested
    @DisplayName("Delete")
    @Feature("DeleteTests")
    class DeleteTests {

        @Test
        @DisplayName("Given one user When delete by LambdaQueryWrapper eq Then count zero")
        @Description("Integration: delete via QueryBuilder + MongoTemplate.remove, then verify count is zero.")
        @Story("Delete by LambdaQueryWrapper eq")
        @Severity(SeverityLevel.CRITICAL)
        void givenOneUser_whenDeleteByLambdaQueryWrapperEq_thenCountZero() {
            mongoTemplate.insert(new UserDocument("D", "d@e.com", 1));
            LambdaQueryWrapper<UserDocument> w = Wrappers.<UserDocument>lambdaQuery(UserDocument.class);
            w.eq(UserDocument::getName, "D");
            mongoTemplate.remove(QueryBuilder.build(w), UserDocument.class);
            assertEquals(0, queryExecutor.selectCount(w, COLLECTION));
        }

        @Test
        @DisplayName("Given one user When delete by QueryWrapper eq Then count zero")
        @Story("Delete by QueryWrapper eq")
        void givenOneUser_whenDeleteByQueryWrapperEq_thenCountZero() {
            mongoTemplate.insert(new UserDocument("D2", "d2@e.com", 2));
            QueryWrapper<UserDocument> w = Wrappers.query(UserDocument.class);
            w.eq("name", "D2");
            mongoTemplate.remove(QueryBuilder.build(w), UserDocument.class);
            assertEquals(0, queryExecutor.selectCount(w, COLLECTION));
        }
    }
}

