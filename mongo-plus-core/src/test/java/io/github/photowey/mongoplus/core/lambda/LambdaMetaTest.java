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
package io.github.photowey.mongoplus.core.lambda;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.exception.MongoPlusException;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LambdaMetaTest - Unit tests for serialized lambda metadata extraction helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("LambdaMetaTest")
class LambdaMetaTest {

    @Test
    @DisplayName(
        "Given a serializable lambda, when extracting lambda metadata, then returns the "
            + "implementation class and method"
    )
    @Story("Extract lambda implementation class and method")
    void givenASerializableLambda_whenExtractingLambdaMetadata_thenReturnsTheImplementationClassAndMethod() {
        // Given
        SFunction<User, String> lambda = User::getName;

        // When
        SerializedLambda serializedLambda = LambdaMeta.extract(lambda);
        String implClassName = LambdaMeta.getImplClassName(serializedLambda);
        String implMethodName = LambdaMeta.getImplMethodName(serializedLambda);

        // Then
        assertTrue(implClassName.contains("LambdaMetaTest"));
        assertTrue(
            "getName".equals(implMethodName)
                || implMethodName.startsWith("lambda$")
        );
    }

    @Test
    @DisplayName(
        "Given a non lambda serializable object, when extracting lambda metadata, then throws a "
            + "MongoPlusException"
    )
    @Story("Throw exception for non-lambda serializable object")
    void givenANonLambdaSerializableObject_whenExtractingLambdaMetadata_thenThrowsAMongoPlusException() {
        // Given
        Serializable object = "not-a-lambda";

        // When
        MongoPlusException error = assertThrows(MongoPlusException.class, () -> LambdaMeta.extract(object));

        // Then
        assertTrue(error.getMessage().contains("Failed to extract SerializedLambda"));
    }

    private static final class User implements Serializable {

        private static final long serialVersionUID = 1L;

        private String name;

        private String getName() {
            return this.name;
        }
    }
}
