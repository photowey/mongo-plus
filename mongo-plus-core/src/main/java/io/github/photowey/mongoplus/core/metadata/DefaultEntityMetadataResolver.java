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
package io.github.photowey.mongoplus.core.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;

/**
 * DefaultEntityMetadataResolver - Framework-neutral metadata resolver.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public class DefaultEntityMetadataResolver implements EntityMetadataResolver {

    @Override
    public EntityMetadata resolve(Class<?> entityClass) {
        EntityMetadata.EntityMetadataBuilder builder = EntityMetadata.builder();
        builder.entityClass(entityClass);
        builder.collectionName(this.resolveCollectionName(entityClass));

        EntityMetadata metadata = builder.build();
        for (Field field : entityClass.getDeclaredFields()) {
            if (this.isStaticOrTransient(field)) {
                continue;
            }

            String propertyName = field.getName();
            metadata.addFieldMapping(propertyName, propertyName);
            if (this.isIdField(field)) {
                metadata.setIdField(propertyName);
                metadata.setIdColumn(ID);
            }
        }

        return metadata;
    }

    protected String resolveCollectionName(Class<?> entityClass) {
        return this.firstCharToLowerCase(entityClass.getSimpleName());
    }

    protected boolean isIdField(Field field) {
        return Strings.isNotEmpty(field.getName())
            && "id".equals(field.getName());
    }

    protected boolean isStaticOrTransient(Field field) {
        return Modifier.isStatic(field.getModifiers())
            || Modifier.isTransient(field.getModifiers());
    }

    protected String firstCharToLowerCase(String text) {
        if (Strings.isEmpty(text)) {
            return text;
        }
        if (text.length() == 1) {
            return text.toLowerCase();
        }
        char first = text.charAt(0);
        if (first >= 'A' && first <= 'Z') {
            return Character.toLowerCase(first) + text.substring(1);
        }

        return Objects.defaultIfNull(text, "");
    }
}
