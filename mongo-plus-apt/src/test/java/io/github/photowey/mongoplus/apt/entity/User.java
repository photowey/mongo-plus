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
package io.github.photowey.mongoplus.apt.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import io.github.photowey.mongoplus.annotation.AutoMapper;
import io.github.photowey.mongoplus.annotation.MongoField;
import io.github.photowey.mongoplus.annotation.MongoIndex;
import io.github.photowey.mongoplus.annotation.MongoIndexes;

/**
 * User - Test entity for annotation processors.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@AutoMapper
@MongoField
@MongoIndex(fields = {"userName", "status"}, unique = true)
@MongoIndexes(
    {
        @MongoIndex(fields = {"email"}, unique = true),
        @MongoIndex(fields = {"createdAt:DESC"}, name = "idx_created_at"),
    }
)
public class User implements Serializable {

    private static final long serialVersionUID = -1260719568216373375L;

    @Id
    private String id;

    @Field("user_name")
    private String userName;

    @Field("email")
    private String email;

    @Field("status")
    private Integer status;

    @Field("created_at")
    private Long createdAt;

    @MongoIndex(unique = true)
    @Field("phone")
    private String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
