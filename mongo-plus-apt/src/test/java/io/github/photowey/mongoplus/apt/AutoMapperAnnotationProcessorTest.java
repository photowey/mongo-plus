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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AutoMapperAnnotationProcessorTest - Verifies generated mapper sources through the processor.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AutoMapperAnnotationProcessorTest")
class AutoMapperAnnotationProcessorTest {

    @Test
    @DisplayName("Given annotated fixtures When compiling with processor Then generated and skipped sources match expectations")
    @Story("Compile annotated fixtures generates and skips sources as expected")
    void givenAnnotatedFixtures_whenCompilingWithProcessor_thenGeneratedAndSkippedSourcesMatchExpectations()
        throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Path root = Files.createTempDirectory("auto-mapper-processor-test");
        Path sourceRoot = root.resolve("src");
        Path classRoot = root.resolve("classes");
        Path generatedRoot = root.resolve("generated");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(classRoot);
        Files.createDirectories(generatedRoot);

        this.writeSource(
            sourceRoot,
            "test.auto.User",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.annotation.AutoMapper;\n"
                + "@AutoMapper\n"
                + "public class User {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.Invoice",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.annotation.AutoMapper;\n"
                + "@AutoMapper(suffix = \"Mapper\")\n"
                + "public class Invoice {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.Product",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.annotation.AutoMapper;\n"
                + "@AutoMapper(exclude = true)\n"
                + "public class Product {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.Order",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.annotation.AutoMapper;\n"
                + "@AutoMapper\n"
                + "public class Order {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.OrderMapper",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.mapper.MongoMapper;\n"
                + "public interface OrderMapper extends MongoMapper<Order> {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.Account",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.annotation.AutoMapper;\n"
                + "@AutoMapper\n"
                + "public class Account {}\n"
        );
        this.writeSource(
            sourceRoot,
            "test.auto.AccountAutoMapper",
            "package test.auto;\n"
                + "import io.github.photowey.mongoplus.mapper.MongoMapper;\n"
                + "public interface AccountAutoMapper extends MongoMapper<Account> { boolean manual(); }\n"
        );

        List<Path> sourceFiles = new ArrayList<>();
        Files.walk(sourceRoot)
            .filter(path -> path.toString().endsWith(".java"))
            .forEach(sourceFiles::add);

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
            task.setProcessors(List.of(new AutoMapperAnnotationProcessor()));
            boolean success = task.call();
            assertTrue(success);
        }

        assertTrue(Files.exists(generatedRoot.resolve("test/auto/UserAutoMapper.java")));
        assertTrue(Files.exists(generatedRoot.resolve("test/auto/InvoiceMapper.java")));
        assertFalse(Files.exists(generatedRoot.resolve("test/auto/ProductAutoMapper.java")));
        assertFalse(Files.exists(generatedRoot.resolve("test/auto/OrderAutoMapper.java")));
        assertFalse(Files.exists(generatedRoot.resolve("test/auto/AccountAutoMapper.java")));
    }

    private void writeSource(Path sourceRoot, String qualifiedName, String source) throws IOException {
        String relativePath = qualifiedName.replace('.', '/') + ".java";
        Path file = sourceRoot.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, source, StandardCharsets.UTF_8);
    }
}
