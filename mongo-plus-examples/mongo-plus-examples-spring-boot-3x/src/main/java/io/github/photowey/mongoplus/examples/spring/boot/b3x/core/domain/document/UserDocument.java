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
package io.github.photowey.mongoplus.examples.spring.boot.b3x.core.domain.document;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.github.photowey.mongoplus.annotation.MongoId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserDocument - User entity for mongo-plus examples.
 * Uses Snowflake-assigned Long ID.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument implements Serializable {

    private static final long serialVersionUID = -7763939743209460859L;

    @Id
    @MongoId(type = MongoId.IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String email;
    private Integer age;
    /**
     * Optional list for testing all() / size() in wrappers.
     */
    private List<String> tags;

    public UserDocument(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
}
