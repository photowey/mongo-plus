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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.autoconfigure.core.mapper.MongoMapperScannerConfigurer;
import io.github.photowey.mongoplus.core.util.ClassUtils;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;

import lombok.NonNull;

/**
 * MongoMapperScannerRegistrar - Registers MongoMapper beans via MapperFactoryBean.
 * Handles @MongoMapperScan annotation with @Import.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class MongoMapperScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(
            metadata.getAnnotationAttributes(MongoMapperScan.class.getName())
        );
        if (Objects.isNull(attrs)) {
            return;
        }
        this.registerBeanDefinitions(
            metadata,
            attrs,
            registry,
            generateBaseBeanName(metadata, 0)
        );
    }

    void registerBeanDefinitions(
        AnnotationMetadata annoMeta,
        AnnotationAttributes annoAttrs,
        BeanDefinitionRegistry registry,
        String beanName
    ) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MongoMapperScannerConfigurer.class);
        builder.addPropertyValue("processPropertyPlaceHolders", true);

        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            builder.addPropertyValue("annotationClass", annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            builder.addPropertyValue("markerInterface", markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            builder.addPropertyValue("mapperFactoryBeanClass", mapperFactoryBeanClass);
        }

        String lazyInitialization = annoAttrs.getString("lazyInitialization");
        if (StringUtils.hasText(lazyInitialization)) {
            builder.addPropertyValue("lazyInitialization", lazyInitialization);
        }

        List<String> basePackages = determineBasePackages(annoMeta, annoAttrs);
        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    @NonNull
    private static List<String> determineBasePackages(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs) {
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(
            Arrays.stream(annoAttrs.getStringArray("value"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList()
                )
        );

        basePackages.addAll(
            Arrays.stream(annoAttrs.getStringArray("basePackages"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList())
        );

        basePackages.addAll(
            Arrays.stream(annoAttrs.getClassArray("basePackageClasses"))
                .map(ClassUtils::getPackageName)
                .collect(Collectors.toList())
        );

        if (Collections.isEmpty(basePackages)) {
            basePackages.add(getDefaultBasePackage(annoMeta));
        }

        return basePackages;
    }

    private static String generateBaseBeanName(AnnotationMetadata metadata, int index) {
        return metadata.getClassName()
            + "#"
            + MongoMapperScannerRegistrar.class.getSimpleName()
            + "#"
            + index;
    }

    private static String getDefaultBasePackage(AnnotationMetadata metadata) {
        return ClassUtils.getPackageName(metadata.getClassName());
    }

    static class RepeatingRegistrar extends MongoMapperScannerRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            AnnotationAttributes mapperScansAttrs = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(MongoMapperScans.class.getName()));
            if (Objects.nonNull(mapperScansAttrs)) {
                AnnotationAttributes[] annotations = mapperScansAttrs.getAnnotationArray("value");
                for (int i = 0; i < annotations.length; i++) {
                    String beanName = generateBaseBeanName(metadata, i);
                    this.registerBeanDefinitions(metadata, annotations[i], registry, beanName);
                }
            }
        }
    }
}
