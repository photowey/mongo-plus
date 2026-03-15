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
package io.github.photowey.mongoplus.executor.template.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.compiler.QueryCompiler;
import io.github.photowey.mongoplus.executor.compiler.impl.MongoQueryCompiler;
import io.github.photowey.mongoplus.executor.template.MongoTemplateExecutor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;

/**
 * DefaultMongoTemplateExecutor - Default implementation of QueryExecutor using this.mongoTemplate.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class DefaultMongoTemplateExecutor extends AbstractMongoTemplateExecutor implements MongoTemplateExecutor {

    private final QueryCompiler queryCompiler;

    /**
     * Creates an executor backed by the supplied query compiler.
     *
     * @param queryCompiler the query compiler used to translate wrappers to MongoDB queries
     */
    public DefaultMongoTemplateExecutor(QueryCompiler queryCompiler) {
        this.queryCompiler = queryCompiler;
    }

    /**
     * Creates an executor backed by the default {@link MongoQueryCompiler}.
     */
    public DefaultMongoTemplateExecutor() {
        this(new MongoQueryCompiler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        Query query = this.buildQuery(wrapper);

        return this.mongoTemplate.find(query, entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        Query query = this.buildQuery(wrapper);

        return this.mongoTemplate.findOne(query, entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
        Query query = this.buildQuery(wrapper);

        return this.mongoTemplate.count(query, collectionName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        Query query = this.buildQuery(wrapper);

        return this.mongoTemplate.exists(query, entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        Query query = this.buildQuery(wrapper);
        this.mongoTemplate.remove(query, entityClass);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T selectById(Serializable id, Class<T> entityClass) {
        return this.mongoTemplate.findById(id, entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean insert(T entity, String collectionName) {
        this.mongoTemplate.insert(entity, collectionName);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean updateById(T entity, String collectionName) {
        Query query = this.buildIdQuery(entity);
        if (Objects.isNull(query)) {
            return false;
        }

        Update update = this.buildUpdateFromEntity(entity);
        if (update.getUpdateObject().isEmpty()) {
            return false;
        }

        UpdateResult result = this.mongoTemplate.updateFirst(query, update, collectionName);

        return result.getModifiedCount() > 0 || result.getMatchedCount() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteById(Serializable id, String collectionName) {
        Query query = new Query(Criteria.where(ID).is(id));
        DeleteResult result = this.mongoTemplate.remove(query, collectionName);

        return result.getDeletedCount() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass) {
        if (!updateWrapper.hasUpdates()) {
            return false;
        }
        Query query = this.buildQuery(updateWrapper);
        Update update = this.buildUpdate(updateWrapper);
        UpdateResult result = this.mongoTemplate.updateMulti(query, update, entityClass);

        return result.getModifiedCount() > 0 || result.getMatchedCount() > 0;
    }

    /**
     * Builds a Spring Data {@link Update} object from an {@link UpdateWrapper}.
     *
     * @param updateWrapper the update wrapper carrying set and increment operations
     * @param <T>           the entity type
     * @return the translated update object
     */
    protected <T> Update buildUpdate(UpdateWrapper<T> updateWrapper) {
        Update update = new Update();

        updateWrapper
            .getSetValues()
            .forEach((field, value) -> {
                if (Objects.isNull(value)) {
                    update.unset(field);
                } else {
                    update.set(field, value);
                }
            });

        updateWrapper
            .getIncValues()
            .forEach(update::inc);

        return update;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass) {
        if (page.isSearchCount()) {
            Query countQuery = this.buildQuery(wrapper);
            long total = this.mongoTemplate.count(countQuery, entityClass);
            page.setTotal(total);
            page.calculatePages();
        } else {
            page.setTotal(-1);
            page.setPages(page.getCurrent() + 1);
        }

        Query query = this.buildQuery(wrapper);
        query.skip(page.getOffset());
        query.limit(Math.toIntExact(page.getSize()));

        List<T> records = this.mongoTemplate.find(query, entityClass);
        page.setRecords(records);

        if (!page.isSearchCount() && Collections.isEmpty(records)) {
            page.setPages(page.getCurrent());
        }

        return page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult insertBatch(List<T> entities, String collectionName) {
        if (Collections.isEmpty(entities)) {
            return BatchResult.builder().acknowledged(true).build();
        }

        BulkOperations bulkOps = this.mongoTemplate.bulkOps(
            BulkOperations.BulkMode.ORDERED,
            collectionName
        );
        for (T entity : entities) {
            bulkOps.insert(entity);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass) {
        if (Collections.isEmpty(entities)) {
            return BatchResult.builder().acknowledged(true).build();
        }

        BulkOperations bulkOps = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, entityClass);
        for (T entity : entities) {
            bulkOps.insert(entity);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult updateBatch(List<T> entities, String collectionName) {
        if (Collections.isEmpty(entities)) {
            return BatchResult.builder().acknowledged(true).build();
        }

        BulkOperations bulkOps = this.mongoTemplate.bulkOps(
            BulkOperations.BulkMode.ORDERED,
            collectionName
        );
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            Query query = this.buildIdQuery(entity);
            if (Objects.isNull(query)) {
                BatchResult result = BatchResult.builder()
                    .acknowledged(false)
                    .build();
                result.addError(i, "Entity has no ID field");
                return result;
            }
            Update update = this.buildUpdateFromEntity(entity);
            bulkOps.updateOne(query, update);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass) {
        if (Collections.isEmpty(entities)) {
            return BatchResult.builder().acknowledged(true).build();
        }

        BulkOperations bulkOps = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, entityClass);
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            Query query = this.buildIdQuery(entity);
            if (Objects.isNull(query)) {
                BatchResult result = BatchResult.builder()
                    .acknowledged(false)
                    .build();
                result.addError(i, "Entity has no ID field");
                return result;
            }
            Update update = this.buildUpdateFromEntity(entity);
            bulkOps.updateOne(query, update);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName) {
        if (Collections.isEmpty(ids)) {
            return BatchResult.builder().acknowledged(true).build();
        }

        BulkOperations bulkOps = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, collectionName);
        for (Serializable id : ids) {
            Query query = new Query(Criteria.where(ID).is(id));
            bulkOps.remove(query);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass) {
        if (Collections.isEmpty(ids)) {
            return BatchResult.builder()
                .acknowledged(true)
                .build();
        }
        BulkOperations bulkOps = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, entityClass);
        for (Serializable id : ids) {
            Query query = new Query(Criteria.where(ID).is(id));
            bulkOps.remove(query);
        }

        return this.executeBulk(bulkOps);
    }

    /**
     * Builds an identifier-based query for the supplied entity.
     *
     * @param entity the entity whose identifier should be extracted
     * @param <T>    the entity type
     * @return the identifier query, or {@code null} when no identifier can be resolved
     */
    protected <T> Query buildIdQuery(T entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        EntityMetadata metadata = this.resolveEntityMetadata(entity);
        if (Strings.isBlank(metadata.getIdField())) {
            return null;
        }

        Object id = this.readFieldValue(entity, metadata.getIdField());
        if (Objects.isNull(id)) {
            return null;
        }

        return new Query(Criteria.where(this.resolveIdColumn(metadata)).is(id));
    }

    /**
     * Builds an {@link Update} object by reading all non-null entity fields except the identifier.
     *
     * @param entity the entity to inspect
     * @param <T>    the entity type
     * @return the translated update object
     */
    protected <T> Update buildUpdateFromEntity(T entity) {
        Update update = new Update();
        if (Objects.isNull(entity)) {
            return update;
        }

        EntityMetadata metadata = this.resolveEntityMetadata(entity);
        for (Field field : this.collectInstanceFields(entity.getClass())) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getName().equals(metadata.getIdField())) {
                continue;
            }

            Object value = this.readFieldValue(entity, field.getName());
            if (Objects.nonNull(value)) {
                update.set(metadata.getColumnName(field.getName()), value);
            }
        }

        return update;
    }

    /**
     * Executes a prepared bulk operation and converts the result to {@link BatchResult}.
     *
     * @param bulkOps the prepared bulk operation
     * @return the batch execution result
     */
    private BatchResult executeBulk(BulkOperations bulkOps) {
        try {
            BulkWriteResult result = bulkOps.execute();
            return BatchResult.builder()
                .insertedCount(result.getInsertedCount())
                .modifiedCount(result.getModifiedCount())
                .deletedCount(result.getDeletedCount())
                .matchedCount(result.getMatchedCount())
                .acknowledged(result.wasAcknowledged())
                .build();
        } catch (Exception e) {
            BatchResult result = BatchResult.builder()
                .acknowledged(false)
                .build();
            result.addError(-1, e.getMessage());
            return result;
        }
    }

    /**
     * Resolves metadata for the supplied entity instance.
     *
     * @param entity the entity instance
     * @return the resolved entity metadata
     */
    private EntityMetadata resolveEntityMetadata(Object entity) {
        return EntityResolver.resolve(entity.getClass());
    }

    /**
     * Resolves the persisted identifier column name from entity metadata.
     *
     * @param metadata the entity metadata
     * @return the identifier column name
     */
    private String resolveIdColumn(EntityMetadata metadata) {
        return Strings.isNotBlank(metadata.getIdColumn())
            ? metadata.getIdColumn()
            : ID;
    }

    /**
     * Reads a field value from an entity using reflection.
     *
     * @param entity    the entity instance
     * @param fieldName the field name to read
     * @return the field value, or {@code null} when the field cannot be found
     */
    private Object readFieldValue(Object entity, String fieldName) {
        Field field = this.findField(entity.getClass(), fieldName);
        if (Objects.isNull(field)) {
            return null;
        }

        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read entity field: " + fieldName, e);
        }
    }

    /**
     * Finds a declared field on the supplied type or one of its superclasses.
     *
     * @param type      the starting type
     * @param fieldName the field name to locate
     * @return the matching field, or {@code null} when none is found
     */
    private Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (Objects.nonNull(current) && !Object.class.equals(current)) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        return null;
    }

    /**
     * Collects all declared instance fields from the supplied type hierarchy.
     *
     * @param type the starting type
     * @return the collected fields
     */
    private List<Field> collectInstanceFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (Objects.nonNull(current) && !Object.class.equals(current)) {
            for (Field field : current.getDeclaredFields()) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }

        return fields;
    }

    /**
     * Compiles a wrapper into a Spring Data {@link Query}.
     *
     * @param wrapper the wrapper to compile
     * @param <T>     the entity type
     * @return the compiled query
     */
    protected <T> Query buildQuery(AbstractWrapper<T> wrapper) {
        return this.queryCompiler.compile(wrapper);
    }
}


