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
package io.github.photowey.mongoplus.autoconfigure.core.scanner;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import io.github.photowey.mongoplus.autoconfigure.core.bean.MapperFactoryBean;
import io.github.photowey.mongoplus.core.util.Objects;

import lombok.Setter;

/**
 * ClassPathMongoMapperScanner - Scans classpath packages for MongoMapper interfaces.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@Setter
public class ClassPathMongoMapperScanner extends ClassPathBeanDefinitionScanner {

    private Class<?> markerInterface;
    private Class<? extends Annotation> annotationClass;
    private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;

    private boolean lazyInitialization;

    public ClassPathMongoMapperScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public ClassPathMongoMapperScanner(
        BeanDefinitionRegistry registry,
        ResourceLoader resourceLoader,
        Environment environment
    ) {
        super(registry, false);

        super.setResourceLoader(resourceLoader);
        super.setEnvironment(environment);
    }

    public void setMapperFactoryBean(MapperFactoryBean mapperFactoryBean) {
        this.mapperFactoryBeanClass = Objects.isNull(mapperFactoryBean)
            ? MapperFactoryBean.class
            : mapperFactoryBean.getClass();
    }

    public void setMapperFactoryBeanClass(Class<? extends MapperFactoryBean> mapperFactoryBeanClass) {
        this.mapperFactoryBeanClass = Objects.isNull(mapperFactoryBeanClass)
            ? MapperFactoryBean.class
            : mapperFactoryBeanClass;
    }

    // ----------------------------------------------------------------

    public void registerFilters() {
        boolean acceptAllInterfaces = true;
        if (Objects.nonNull(this.annotationClass)) {
            this.addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        if (Objects.nonNull(this.markerInterface)) {
            this.addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            this.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        this.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    // ----------------------------------------------------------------

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        this.processBeanDefinitions(beanDefinitions);

        return beanDefinitions;
    }

    // ----------------------------------------------------------------

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();

            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setBeanClass(this.mapperFactoryBeanClass);

            definition.setLazyInit(this.lazyInitialization);
        }
    }

    // ----------------------------------------------------------------

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        }

        // ...

        return false;
    }
}
