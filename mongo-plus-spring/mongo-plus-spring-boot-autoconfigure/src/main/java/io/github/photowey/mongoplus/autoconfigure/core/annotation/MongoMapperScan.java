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
package io.github.photowey.mongoplus.autoconfigure.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.mapper.MongoMapper;

/**
 * MongoMapperScan - Enables MongoMapper scanning and registers Mapper beans.
 * Use this with basePackages or basePackageClasses.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MongoMapperScannerRegistrar.class)
public @interface MongoMapperScan {

    /**
     * Base packages to scan for MongoMapper interfaces.
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for MongoMapper interfaces.
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Base package classes to scan (package of each class will be scanned).
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * The {@link BeanNameGenerator} class to be used for naming detected components within the Spring container.
     *
     * @return the class of {@link BeanNameGenerator}
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * This property specifies the annotation that the scanner will search for.
     *
     * <p>
     * The scanner will register all interfaces in the base package that also have the specified annotation.
     *
     * <p>
     * Note this can be combined with markerInterface.
     *
     * @return the annotation that the scanner will search for
     */
    Class<? extends Annotation> annotationClass() default io.github.photowey.mongoplus.annotation.MongoMapper.class;

    /**
     * This property specifies the parent that the scanner will search for.
     *
     * <p>
     * The scanner will register all interfaces in the base package that also have the specified interface class as a
     * parent.
     *
     * <p>
     * Note this can be combined with annotationClass.
     * Default is {@link MongoMapper}.
     *
     * @return the parent that the scanner will search for
     */
    Class<?> markerInterface() default MongoMapper.class;

    /**
     * Specifies a custom MapperFactoryBean to return a mybatis proxy as spring bean.
     *
     * @return the class of {@code MapperFactoryBean}
     */
    Class<? extends MapperFactoryBean> factoryBean() default MapperFactoryBean.class;

    /**
     * Whether enable lazy initialization of mapper bean.
     *
     * <p>
     * Default is {@code false}.
     * </p>
     *
     * @return set {@code true} to enable lazy initialization
     */
    String lazyInitialization() default "";
}
