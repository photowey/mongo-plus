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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.github.photowey.mongoplus.core.exception.MongoPlusException;
import io.github.photowey.mongoplus.core.util.AssertionErrors;

/**
 * LambdaMeta - Utility to extract SerializedLambda from serializable lambda.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class LambdaMeta {

    private LambdaMeta() {
        AssertionErrors.throwz(LambdaMeta.class);
    }

    /**
     * Extract SerializedLambda from a serializable lambda.
     *
     * @param lambda the serializable lambda
     * @return SerializedLambda containing metadata
     * @throws MongoPlusException if extraction fails
     */
    public static SerializedLambda extract(Serializable lambda) {
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            return (SerializedLambda) writeReplace.invoke(lambda);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MongoPlusException("Failed to extract SerializedLambda: " + e.getMessage(), e);
        }
    }

    /**
     * Get the implementation class name from SerializedLambda.
     *
     * @param lambda the serialized lambda
     * @return the implementation class name
     */
    public static String getImplClassName(SerializedLambda lambda) {
        return lambda.getImplClass().replace('/', '.');
    }

    /**
     * Get the implementation method name from SerializedLambda.
     *
     * @param lambda the serialized lambda
     * @return the implementation method name (e.g., "getName")
     */
    public static String getImplMethodName(SerializedLambda lambda) {
        return lambda.getImplMethodName();
    }
}

