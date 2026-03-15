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
package io.github.photowey.mongoplus.autoconfigure.core.metadata;

import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.github.photowey.mongoplus.core.metadata.DefaultEntityMetadataResolver;
import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityMetadataResolver;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;

/**
 * SpringDataEntityMetadataResolver - Spring Data aware metadata resolver.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public class SpringDataEntityMetadataResolver extends DefaultEntityMetadataResolver
    implements EntityMetadataResolver {

    @Override
    public EntityMetadata resolve(Class<?> entityClass) {
        EntityMetadata metadata = super.resolve(entityClass);
        metadata.setCollectionName(this.resolveCollectionName(entityClass));

        for (Field field : entityClass.getDeclaredFields()) {
            if (this.isStaticOrTransient(field)) {
                continue;
            }

            String propertyName = field.getName();
            String columnName = propertyName;
            org.springframework.data.mongodb.core.mapping.Field fieldAnnotation =
                field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
            if (Objects.nonNull(fieldAnnotation) && Strings.isNotEmpty(fieldAnnotation.value())) {
                columnName = fieldAnnotation.value();
            }
            metadata.addFieldMapping(propertyName, columnName);
            if (field.isAnnotationPresent(Id.class)) {
                metadata.setIdField(propertyName);
                metadata.setIdColumn(ID);
            }
        }

        return metadata;
    }

    @Override
    protected String resolveCollectionName(Class<?> entityClass) {
        Document document = entityClass.getAnnotation(Document.class);
        if (Objects.nonNull(document) && Strings.isNotEmpty(document.collection())) {
            return document.collection();
        }

        return super.resolveCollectionName(entityClass);
    }
}
