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
package io.github.photowey.mongoplus.mapper.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.github.photowey.mongoplus.core.util.AssertionErrors;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.mapper.MongoMapper;

/**
 * Mappers - Resolves entity type from MongoMapper interface.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public final class Mappers {

    private Mappers() {
        AssertionErrors.throwz(Mappers.class);
    }

    /**
     * Resolve entity class from a MongoMapper interface (e.g. UserMapper extends MongoMapper&lt;User&gt;).
     *
     * @param mapperInterface the mapper interface class
     * @return the entity class, or null if not resolvable
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> resolve(Class<?> mapperInterface) {
        if (Objects.isNull(mapperInterface) || !mapperInterface.isInterface()) {
            return null;
        }
        for (Type genericInterface : mapperInterface.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                if (MongoMapper.class.equals(pt.getRawType())) {
                    Type typeArg = pt.getActualTypeArguments()[0];
                    if (typeArg instanceof Class) {
                        return (Class<T>) typeArg;
                    }
                }
            }
        }

        return null;
    }
}
