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
package io.github.photowey.mongoplus.core.exception;

import lombok.Getter;

/**
 * MongoPlusException - Base exception for MongoPlus framework.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Getter
public class MongoPlusException extends RuntimeException {

    private static final long serialVersionUID = -7868700705022854732L;

    private final String errorCode;
    private Class<?> entityClass;

    public MongoPlusException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public MongoPlusException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MongoPlusException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public MongoPlusException setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
        return this;
    }
}
