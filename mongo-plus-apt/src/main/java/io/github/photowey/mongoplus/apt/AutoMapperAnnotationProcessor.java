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
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import io.github.photowey.mongoplus.annotation.AutoMapper;
import io.github.photowey.mongoplus.core.util.Strings;

/**
 * AutoMapperAnnotationProcessor - Generates mapper interfaces for types annotated with {@link AutoMapper}.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@SupportedAnnotationTypes("io.github.photowey.mongoplus.annotation.AutoMapper")
public class AutoMapperAnnotationProcessor extends AbstractProcessor {

    private static final String DEFAULT_SUFFIX = "AutoMapper";
    private static final String DEFAULT_SIMPLE_SUFFIX = "Mapper";
    private static final String VERBOSE_OPTION = "mongoplus.apt.verbose";

    private final Set<String> processedTypes = new HashSet<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Set.of(VERBOSE_OPTION);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoMapper.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            String qualifiedName = typeElement.getQualifiedName().toString();
            if (this.processedTypes.contains(qualifiedName)) {
                continue;
            }
            this.processedTypes.add(qualifiedName);
            this.generateAutoMapper(typeElement);
        }

        return true;
    }

    private void generateAutoMapper(TypeElement typeElement) {
        AutoMapper autoMapper = typeElement.getAnnotation(AutoMapper.class);
        if (autoMapper.exclude()) {
            this.note(
                typeElement,
                "Skip auto mapper generation for %s because exclude=true",
                typeElement.getSimpleName().toString()
            );

            return;
        }

        PackageElement packageElement = this.processingEnv.getElementUtils().getPackageOf(typeElement);
        String packageName = packageElement.isUnnamed()
            ? ""
            : packageElement.getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        String suffix = this.resolveSuffix(autoMapper.suffix());
        String generatedSimpleName = simpleName + suffix;
        String generatedQualifiedName = this.qualify(packageName, generatedSimpleName);
        String conventionalMapperName = this.qualify(packageName, simpleName + DEFAULT_SIMPLE_SUFFIX);

        if (this.typeExists(conventionalMapperName)) {
            this.note(
                typeElement,
                "Skip auto mapper generation for %s because %s already exists",
                simpleName,
                conventionalMapperName
            );

            return;
        }
        if (this.typeExists(generatedQualifiedName)) {
            this.note(
                typeElement,
                "Skip auto mapper generation for %s because %s already exists",
                simpleName,
                generatedQualifiedName
            );

            return;
        }

        try {
            this.writeGeneratedInterface(
                typeElement,
                packageName,
                simpleName,
                generatedSimpleName,
                generatedQualifiedName
            );
        } catch (FilerException ignored) {
            this.note(
                typeElement,
                "Skip auto mapper generation for %s because %s already exists in filer output",
                simpleName,
                generatedQualifiedName
            );
        } catch (IOException e) {
            this.processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to generate auto mapper for " + simpleName + ": " + e.getMessage(),
                typeElement
            );
        }
    }

    private void writeGeneratedInterface(
        TypeElement typeElement,
        String packageName,
        String simpleName,
        String generatedSimpleName,
        String generatedQualifiedName
    ) throws IOException {
        JavaFileObject file = this.processingEnv.getFiler().createSourceFile(generatedQualifiedName, typeElement);
        try (PrintWriter writer = new PrintWriter(file.openWriter())) {
            this.writeLicenseHeader(writer);
            if (!packageName.isEmpty()) {
                writer.println("package " + packageName + ";");
                writer.println();
            }
            writer.println("import io.github.photowey.mongoplus.mapper.MongoMapper;");
            writer.println();
            writer.println("/**");
            writer.println(" * " + generatedSimpleName + " - Generated mapper for " + simpleName + ".");
            writer.println(" *");
            writer.println(" * @author photowey");
            writer.println(" * @version 2026.1.0.0");
            writer.println(" * @since 2026/03/10");
            writer.println(" */");
            writer.println("public interface " + generatedSimpleName + " extends MongoMapper<" + simpleName + "> {");
            writer.println("}");
        }
    }

    private void writeLicenseHeader(PrintWriter writer) {
        writer.println("/*");
        writer.println(" * Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.");
        writer.println(" *");
        writer.println(" * Licensed under the Apache License, Version 2.0 (the \"License\");");
        writer.println(" * you may not use this file except in compliance with the License.");
        writer.println(" * You may obtain a copy of the License at");
        writer.println(" *");
        writer.println(" *     http://www.apache.org/licenses/LICENSE-2.0");
        writer.println(" *");
        writer.println(" * Unless required by applicable law or agreed to in writing, software");
        writer.println(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
        writer.println(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        writer.println(" * See the License for the specific language governing permissions and");
        writer.println(" * limitations under the License.");
        writer.println(" */");
    }

    private boolean typeExists(String qualifiedName) {
        return this.processingEnv.getElementUtils().getTypeElement(qualifiedName) != null;
    }

    private String resolveSuffix(String suffix) {
        return Strings.isEmpty(suffix)
            ? DEFAULT_SUFFIX
            : suffix.trim();
    }

    private String qualify(String packageName, String simpleName) {
        return packageName.isEmpty()
            ? simpleName
            : packageName + "." + simpleName;
    }

    private void note(Element element, String pattern, Object... args) {
        if (!this.isVerbose()) {
            return;
        }

        this.processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            String.format(pattern, args),
            element
        );
    }

    private boolean isVerbose() {
        Map<String, String> options = this.processingEnv.getOptions();
        String verbose = options.getOrDefault(VERBOSE_OPTION, Boolean.FALSE.toString());
        return Boolean.parseBoolean(verbose);
    }
}
