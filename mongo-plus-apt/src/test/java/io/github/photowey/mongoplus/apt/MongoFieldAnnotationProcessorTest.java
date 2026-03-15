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
package io.github.photowey.mongoplus.apt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MongoFieldAnnotationProcessorTest - Verifies generated field metadata sources through the processor.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoFieldAnnotationProcessorTest")
class MongoFieldAnnotationProcessorTest {

    @Test
    @DisplayName("Given MongoField fixture When compiling with processor Then generated columns use field metadata")
    @Story("MongoField fixture generates columns with field metadata")
    void givenMongoFieldFixture_whenCompilingWithProcessor_thenGeneratedColumnsUseFieldMetadata()
        throws IOException {
        // Given
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
        Path root = Files.createTempDirectory("mongo-field-processor-test");
        Path sourceRoot = root.resolve("src");
        Path classRoot = root.resolve("classes");
        Path generatedRoot = root.resolve("generated");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(classRoot);
        Files.createDirectories(generatedRoot);
        this.writeSource(
            sourceRoot,
            "test.columns.Address",
            "package test.columns;\n"
                + "import org.springframework.data.mongodb.core.mapping.Field;\n"
                + "public class Address {\n"
                + "    @Field(\"city_name\")\n"
                + "    private String city;\n"
                + "    private String zipCode;\n"
                + "}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.columns.User",
            "package test.columns;\n"
                + "import io.github.photowey.mongoplus.annotation.MongoField;\n"
                + "import io.github.photowey.mongoplus.annotation.MongoIndex;\n"
                + "import org.springframework.data.annotation.Id;\n"
                + "import org.springframework.data.annotation.Transient;\n"
                + "import org.springframework.data.mongodb.core.mapping.Field;\n"
                + "@MongoField\n"
                + "@MongoIndex(fields = {\"userName\"}, unique = true)\n"
                + "public class User {\n"
                + "    private static final long serialVersionUID = 1L;\n"
                + "    @Id\n"
                + "    private Long id;\n"
                + "    @Field(\"user_name\")\n"
                + "    private String userName;\n"
                + "    private Address address;\n"
                + "    @MongoIndex(unique = true)\n"
                + "    @Field(\"phone\")\n"
                + "    private String phone;\n"
                + "    private transient String cacheKey;\n"
                + "    @Transient\n"
                + "    private String runtimeOnly;\n"
                + "}\n"
        );
        List<Path> sourceFiles = new ArrayList<>();
        Files.walk(sourceRoot)
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(sourceFiles::add);

        // When
        try (
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
            fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(classRoot));
            fileManager.setLocationFromPaths(StandardLocation.SOURCE_OUTPUT, List.of(generatedRoot));
            CompilationTask task = compiler.getTask(
                null,
                fileManager,
                null,
                List.of("-classpath", System.getProperty("java.class.path")),
                null,
                fileManager.getJavaFileObjectsFromPaths(sourceFiles)
            );
            task.setProcessors(List.of(new MongoFieldAnnotationProcessor()));
            assertTrue(task.call());
        }

        // Then
        Path generatedSource = generatedRoot.resolve("test/columns/UserColumns.java");
        assertTrue(Files.exists(generatedSource));
        String generated = Files.readString(generatedSource, StandardCharsets.UTF_8);
        assertTrue(generated.contains("public static final FieldMetadata<Long> ID"));
        assertTrue(generated.contains(".column(\"_id\")"));
        assertTrue(generated.contains(".path(\"user_name\")"));
        assertTrue(generated.contains("public static final FieldMetadata<Address> ADDRESS"));
        assertTrue(generated.contains("public static final FieldMetadata<String> ADDRESS_CITY"));
        assertTrue(generated.contains(".path(\"address.city_name\")"));
        assertTrue(generated.contains(".parent(ADDRESS)"));
        assertTrue(generated.contains(".indexed(true)"));
        assertTrue(generated.contains(".unique(true)"));
        assertFalse(generated.contains("CACHE_KEY"));
        assertFalse(generated.contains("RUNTIME_ONLY"));
        assertFalse(generated.contains("SERIAL_VERSION_UID"));
        assertTrue(generated.contains("IDX_USER_NAME"));
        assertTrue(generated.contains("\\\"user_name\\\": 1"));
    }

    private void writeSource(Path sourceRoot, String qualifiedName, String source) throws IOException {
        String relativePath = qualifiedName.replace('.', '/') + ".java";
        Path file = sourceRoot.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, source, StandardCharsets.UTF_8);
    }
}
