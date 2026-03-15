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
package io.github.photowey.mongoplus.examples.spring.boot.b3x.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.examples.spring.boot.b3x.mapper.UserMapper;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;

/**
 * UserService - Example service using MongoMapper (CRUD, query, page, batch).
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean save(UserDocument user) {
        return userMapper.insert(user);
    }

    public UserDocument getById(Long id) {
        return userMapper.selectById(id);
    }

    public List<UserDocument> findByName(String name) {
        LambdaQueryWrapper<UserDocument> wrapper =
            Wrappers.lambdaQuery(UserDocument.class).eq(UserDocument::getName, name);
        return userMapper.selectList(wrapper);
    }

    public List<UserDocument> findByAgeBetween(int minAge, int maxAge) {
        LambdaQueryWrapper<UserDocument> wrapper = Wrappers.lambdaQuery(UserDocument.class)
            .gte(UserDocument::getAge, minAge)
            .lte(UserDocument::getAge, maxAge)
            .orderByAsc(UserDocument::getAge);
        return userMapper.selectList(wrapper);
    }

    public Page<UserDocument> page(long current, long size) {
        Page<UserDocument> page = new Page<>(current, size);
        LambdaQueryWrapper<UserDocument> wrapper =
            Wrappers.lambdaQuery(UserDocument.class).orderByDesc(UserDocument::getId);
        return userMapper.selectPage(page, wrapper);
    }

    public long count() {
        return userMapper.selectCount(Wrappers.lambdaQuery(UserDocument.class));
    }

    public BatchResult insertBatch(List<UserDocument> users) {
        return userMapper.insertBatch(users);
    }

    public BatchResult deleteBatch(List<Long> ids) {
        return userMapper.deleteBatch(ids);
    }

    public boolean updateById(UserDocument user) {
        return userMapper.updateById(user);
    }
}
