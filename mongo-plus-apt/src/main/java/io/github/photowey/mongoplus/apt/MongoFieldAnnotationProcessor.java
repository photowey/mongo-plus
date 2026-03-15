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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import io.github.photowey.mongoplus.annotation.MongoIndex;
import io.github.photowey.mongoplus.annotation.MongoIndexes;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.core.util.Strings;

/**
 * MongoFieldAnnotationProcessor - Generates typed field metadata classes for annotated entities.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@SupportedAnnotationTypes(
    {
        "io.github.photowey.mongoplus.annotation.MongoField",
        "io.github.photowey.mongoplus.annotation.MongoIndex",
        "io.github.photowey.mongoplus.annotation.MongoIndexes",
    }
)
public class MongoFieldAnnotationProcessor extends AbstractProcessor {

    private static final String FIELD_METADATA_QUALIFIED_NAME =
        "io.github.photowey.mongoplus.core.metadata.FieldMetadata";
    private static final String VERBOSE_OPTION = "mongoplus.apt.verbose";

    private final Set<String> processedElements = new HashSet<>();

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
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (!(element instanceof TypeElement)) {
                    continue;
                }

                TypeElement typeElement = (TypeElement) element;
                String elementName = typeElement.getQualifiedName().toString();
                if (this.processedElements.contains(elementName)) {
                    continue;
                }
                this.processedElements.add(elementName);
                this.generateColumnsClass(typeElement);
            }
        }

        return true;
    }

    private void generateColumnsClass(TypeElement element) {
        PackageElement packageElement = this.processingEnv.getElementUtils().getPackageOf(element);
        String packageName = packageElement.isUnnamed()
            ? ""
            : packageElement.getQualifiedName().toString();
        String columnsClassName = element.getSimpleName().toString() + "Columns";
        String qualifiedName = this.qualify(packageName, columnsClassName);

        try {
            JavaFileObject file = this.processingEnv.getFiler().createSourceFile(qualifiedName, element);
            try (PrintWriter writer = new PrintWriter(file.openWriter())) {
                Set<String> imports = this.collectImports(element, packageName);
                this.writeLicenseHeader(writer);
                this.writePackage(writer, packageName);
                this.writeImports(writer, imports);
                this.writeClassHeader(writer, element.getSimpleName().toString(), columnsClassName);
                this.writeFieldMetadataConstants(
                    writer,
                    element,
                    element,
                    packageName,
                    "",
                    "",
                    new HashSet<>()
                );
                this.writeIndexConstants(writer, element);
                this.writeClassFooter(writer, columnsClassName);
            }
        } catch (IOException e) {
            this.processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Failed to generate columns class: " + e.getMessage(),
                element
            );
        }
    }

    private Set<String> collectImports(TypeElement rootType, String packageName) {
        Set<String> imports = new HashSet<>();
        imports.add(FIELD_METADATA_QUALIFIED_NAME);
        this.collectImports(rootType, packageName, imports, new HashSet<>());

        return imports;
    }

    private void collectImports(
        TypeElement currentType,
        String packageName,
        Set<String> imports,
        Set<String> visitingTypes
    ) {
        String qualifiedName = currentType.getQualifiedName().toString();
        if (!visitingTypes.add(qualifiedName)) {
            return;
        }

        for (Element enclosed : currentType.getEnclosedElements()) {
            if (!(enclosed instanceof VariableElement)) {
                continue;
            }

            VariableElement field = (VariableElement) enclosed;
            if (this.shouldSkipField(field)) {
                continue;
            }

            this.collectTypeImports(field.asType(), packageName, imports);
            if (this.isSimpleType(field.asType())) {
                continue;
            }

            TypeElement nestedType = this.asTypeElement(field.asType());
            if (Objects.nonNull(nestedType)) {
                this.collectImports(nestedType, packageName, imports, visitingTypes);
            }
        }

        visitingTypes.remove(qualifiedName);
    }

    private void collectTypeImports(TypeMirror type, String packageName, Set<String> imports) {
        TypeKind kind = type.getKind();
        if (kind.isPrimitive()) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            TypeElement boxedType = this.processingEnv.getTypeUtils().boxedClass(primitiveType);
            this.addImport(boxedType.getQualifiedName().toString(), packageName, imports);
            return;
        }
        if (kind == TypeKind.ARRAY) {
            this.collectTypeImports(((ArrayType) type).getComponentType(), packageName, imports);
            return;
        }
        if (kind != TypeKind.DECLARED) {
            return;
        }

        DeclaredType declaredType = (DeclaredType) type;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        this.addImport(typeElement.getQualifiedName().toString(), packageName, imports);
        for (TypeMirror typeArgument : declaredType.getTypeArguments()) {
            this.collectTypeImports(typeArgument, packageName, imports);
        }
    }

    private void addImport(String qualifiedName, String packageName, Set<String> imports) {
        if (Strings.isBlank(qualifiedName) || !qualifiedName.contains(".")) {
            return;
        }

        String importPackage = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        if ("java.lang".equals(importPackage) || packageName.equals(importPackage)) {
            return;
        }

        imports.add(qualifiedName);
    }

    private void writePackage(PrintWriter writer, String packageName) {
        if (Strings.isBlank(packageName)) {
            return;
        }

        writer.println("package " + packageName + ";");
        writer.println();
    }

    private void writeImports(PrintWriter writer, Set<String> imports) {
        List<String> orderedImports = new ArrayList<>(imports);
        orderedImports.sort(this::compareImports);
        if (Collections.isEmpty(orderedImports)) {
            return;
        }

        int currentGroup = -1;
        for (String importName : orderedImports) {
            int importGroup = this.importGroup(importName);
            if (currentGroup != -1 && currentGroup != importGroup) {
                writer.println();
            }
            writer.println("import " + importName + ";");
            currentGroup = importGroup;
        }
        writer.println();
    }

    private int compareImports(String left, String right) {
        int leftGroup = this.importGroup(left);
        int rightGroup = this.importGroup(right);
        if (leftGroup != rightGroup) {
            return Integer.compare(leftGroup, rightGroup);
        }

        return left.compareTo(right);
    }

    private int importGroup(String importName) {
        if (importName.startsWith("java.")) {
            return 0;
        }
        if (importName.startsWith("javax.")) {
            return 1;
        }
        if (importName.startsWith("jakarta.")) {
            return 2;
        }
        if (importName.startsWith("org.")) {
            return 3;
        }
        if (importName.startsWith("com.")) {
            return 4;
        }
        if (importName.startsWith("io.")) {
            return 5;
        }

        return 6;
    }

    private void writeClassHeader(PrintWriter writer, String entityName, String columnsClassName) {
        writer.println("/**");
        writer.println(" * " + columnsClassName + " - Generated field metadata for " + entityName + ".");
        writer.println(" *");
        writer.println(" * @author photowey");
        writer.println(" * @version 2026.1.0.0");
        writer.println(" * @since 2026/03/10");
        writer.println(" */");
        writer.println("public final class " + columnsClassName + " {");
        writer.println();
    }

    private void writeFieldMetadataConstants(
        PrintWriter writer,
        TypeElement rootType,
        TypeElement currentType,
        String packageName,
        String parentPath,
        String parentConstant,
        Set<String> visitingTypes
    ) {
        String currentQualifiedName = currentType.getQualifiedName().toString();
        if (!visitingTypes.add(currentQualifiedName)) {
            return;
        }

        for (Element enclosed : currentType.getEnclosedElements()) {
            if (!(enclosed instanceof VariableElement)) {
                continue;
            }

            VariableElement field = (VariableElement) enclosed;
            if (this.shouldSkipField(field)) {
                continue;
            }

            String propertyName = field.getSimpleName().toString();
            boolean rootField = Strings.isBlank(parentPath);
            boolean idField = rootField && this.isIdField(field);
            String columnName = this.resolveColumnName(field, idField);
            String path = Strings.isBlank(parentPath)
                ? columnName
                : parentPath + "." + columnName;
            String constantName = Strings.isBlank(parentConstant)
                ? this.toConstantName(propertyName)
                : parentConstant + "_" + this.toConstantName(propertyName);
            TypeRender typeRender = this.renderType(field.asType());

            writer.println(
                "    public static final FieldMetadata<" + typeRender.declarationType + "> "
                    + constantName + " = FieldMetadata.<" + typeRender.declarationType + ">builder()"
            );
            writer.println("        .name(\"" + propertyName + "\")");
            writer.println("        .column(\"" + columnName + "\")");
            writer.println("        .path(\"" + path + "\")");
            writer.println("        .type(" + typeRender.classLiteral + ")");
            writer.println("        .entityType(" + rootType.getSimpleName().toString() + ".class)");
            if (Strings.isNotBlank(parentConstant)) {
                writer.println("        .parent(" + parentConstant + ")");
            }
            if (idField) {
                writer.println("        .id(true)");
            }

            MongoIndex fieldIndex = field.getAnnotation(MongoIndex.class);
            if (Objects.nonNull(fieldIndex)) {
                writer.println("        .indexed(true)");
                if (fieldIndex.unique()) {
                    writer.println("        .unique(true)");
                }
            }
            writer.println("        .build();");
            writer.println();

            if (this.isSimpleType(field.asType())) {
                continue;
            }

            TypeElement nestedType = this.asTypeElement(field.asType());
            if (Objects.nonNull(nestedType)) {
                this.writeFieldMetadataConstants(
                    writer,
                    rootType,
                    nestedType,
                    packageName,
                    path,
                    constantName,
                    visitingTypes
                );
            }
        }

        visitingTypes.remove(currentQualifiedName);
    }

    private void writeIndexConstants(PrintWriter writer, TypeElement element) {
        MongoIndex singleIndex = element.getAnnotation(MongoIndex.class);
        MongoIndexes multipleIndexes = element.getAnnotation(MongoIndexes.class);
        boolean wroteIndex = false;

        if (Objects.nonNull(singleIndex)) {
            this.writeCompositeIndexConstant(writer, element, singleIndex);
            wroteIndex = true;
        }
        if (Objects.nonNull(multipleIndexes)) {
            for (MongoIndex index : multipleIndexes.value()) {
                this.writeCompositeIndexConstant(writer, element, index);
                wroteIndex = true;
            }
        }
        for (Element enclosed : element.getEnclosedElements()) {
            if (!(enclosed instanceof VariableElement)) {
                continue;
            }

            VariableElement field = (VariableElement) enclosed;
            MongoIndex fieldIndex = field.getAnnotation(MongoIndex.class);
            if (Objects.isNull(fieldIndex)) {
                continue;
            }

            this.writeFieldIndexConstant(writer, field, fieldIndex);
            wroteIndex = true;
        }
        if (wroteIndex) {
            writer.println();
        }
    }

    private void writeFieldIndexConstant(PrintWriter writer, VariableElement field, MongoIndex index) {
        String propertyName = field.getSimpleName().toString();
        String constantName = "IDX_" + this.toConstantName(propertyName);
        String columnName = this.resolveColumnName(field, this.isIdField(field));
        String indexDefinition = "{ \\\"" + columnName + "\\\": 1 }";

        writer.println(
            "    public static final String " + constantName + " = \"" + indexDefinition + "\";"
        );
        if (index.unique()) {
            writer.println("    public static final boolean " + constantName + "_UNIQUE = true;");
        }
    }

    private void writeCompositeIndexConstant(PrintWriter writer, TypeElement element, MongoIndex index) {
        String[] fields = index.fields();
        if (fields.length == 0) {
            return;
        }

        StringBuilder constantName = new StringBuilder();
        if (Strings.isNotBlank(index.name())) {
            constantName.append(this.toConstantName(index.name()));
        } else {
            for (int i = 0; i < fields.length; i++) {
                if (i > 0) {
                    constantName.append("_");
                }
                constantName.append(this.toConstantName(this.stripDirection(fields[i])));
            }
        }
        if (!constantName.toString().startsWith("IDX_")) {
            constantName.insert(0, "IDX_");
        }

        StringBuilder indexDefinition = new StringBuilder();
        indexDefinition.append("{ ");
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                indexDefinition.append(", ");
            }

            String fieldExpression = fields[i];
            String fieldName = this.stripDirection(fieldExpression);
            String resolvedField = this.resolveIndexedPath(element, fieldName);
            int direction = this.resolveDirection(fieldExpression);
            indexDefinition.append("\\\"").append(resolvedField).append("\\\": ").append(direction);
        }
        indexDefinition.append(" }");

        writer.println(
            "    public static final String " + constantName + " = \"" + indexDefinition + "\";"
        );
        if (index.unique()) {
            writer.println("    public static final boolean " + constantName + "_UNIQUE = true;");
        }
    }

    private String resolveIndexedPath(TypeElement rootType, String declaredPath) {
        String[] segments = declaredPath.split("\\.");
        TypeElement currentType = rootType;
        StringBuilder resolvedPath = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            String resolvedSegment = segment;
            VariableElement field = Objects.nonNull(currentType)
                ? this.findField(currentType, segment)
                : null;
            if (Objects.nonNull(field)) {
                resolvedSegment = this.resolveColumnName(field, i == 0 && this.isIdField(field));
                currentType = this.isSimpleType(field.asType())
                    ? null
                    : this.asTypeElement(field.asType());
            } else {
                currentType = null;
            }
            if (i > 0) {
                resolvedPath.append('.');
            }
            resolvedPath.append(resolvedSegment);
        }

        return resolvedPath.toString();
    }

    private VariableElement findField(TypeElement typeElement, String propertyName) {
        for (Element enclosed : typeElement.getEnclosedElements()) {
            if (!(enclosed instanceof VariableElement)) {
                continue;
            }
            VariableElement field = (VariableElement) enclosed;
            if (propertyName.equals(field.getSimpleName().toString())) {
                return field;
            }
        }

        return null;
    }

    private int resolveDirection(String fieldExpression) {
        if (!fieldExpression.contains(":")) {
            return 1;
        }

        String direction = fieldExpression.substring(fieldExpression.indexOf(':') + 1);
        return "DESC".equalsIgnoreCase(direction) ? -1 : 1;
    }

    private String stripDirection(String fieldExpression) {
        return fieldExpression.contains(":")
            ? fieldExpression.substring(0, fieldExpression.indexOf(':'))
            : fieldExpression;
    }

    private String resolveColumnName(VariableElement field, boolean idField) {
        if (idField) {
            return MongoPlusConstants.ID;
        }

        Field fieldAnnotation = field.getAnnotation(Field.class);
        if (Objects.nonNull(fieldAnnotation) && Strings.isNotBlank(fieldAnnotation.value())) {
            return fieldAnnotation.value();
        }

        return field.getSimpleName().toString();
    }

    private boolean isIdField(VariableElement field) {
        return Objects.nonNull(field.getAnnotation(Id.class))
            || "id".equals(field.getSimpleName().toString());
    }

    private boolean shouldSkipField(VariableElement field) {
        Set<Modifier> modifiers = field.getModifiers();
        return modifiers.contains(Modifier.STATIC)
            || modifiers.contains(Modifier.TRANSIENT)
            || "serialVersionUID".equals(field.getSimpleName().toString())
            || Objects.nonNull(field.getAnnotation(Transient.class));
    }

    private boolean isSimpleType(TypeMirror type) {
        if (type.getKind().isPrimitive() || type.getKind() == TypeKind.ARRAY) {
            return true;
        }
        if (type.getKind() != TypeKind.DECLARED) {
            return true;
        }

        TypeElement typeElement = this.asTypeElement(type);
        if (Objects.isNull(typeElement)) {
            return true;
        }
        if (typeElement.getKind() == ElementKind.ENUM) {
            return true;
        }

        String qualifiedName = typeElement.getQualifiedName().toString();
        return qualifiedName.startsWith("java.")
            || qualifiedName.startsWith("javax.")
            || qualifiedName.startsWith("jakarta.")
            || qualifiedName.startsWith("org.bson.");
    }

    private TypeElement asTypeElement(TypeMirror type) {
        Element element = this.processingEnv.getTypeUtils().asElement(type);
        if (Objects.isNull(element) || !(element instanceof TypeElement)) {
            return null;
        }

        return (TypeElement) element;
    }

    private TypeRender renderType(TypeMirror type) {
        TypeKind kind = type.getKind();
        if (kind.isPrimitive()) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            TypeElement boxedType = this.processingEnv.getTypeUtils().boxedClass(primitiveType);
            String simpleName = boxedType.getSimpleName().toString();
            return new TypeRender(simpleName, simpleName + ".class");
        }
        if (kind == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) type;
            TypeRender componentType = this.renderType(arrayType.getComponentType());
            String rawComponentType = this.renderRawType(arrayType.getComponentType());
            return new TypeRender(componentType.declarationType + "[]", rawComponentType + "[].class");
        }
        if (kind != TypeKind.DECLARED) {
            return new TypeRender("Object", "Object.class");
        }

        DeclaredType declaredType = (DeclaredType) type;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String simpleName = typeElement.getSimpleName().toString();
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (Collections.isEmpty(typeArguments)) {
            return new TypeRender(simpleName, simpleName + ".class");
        }

        StringBuilder declarationType = new StringBuilder();
        declarationType.append(simpleName).append('<');
        for (int i = 0; i < typeArguments.size(); i++) {
            if (i > 0) {
                declarationType.append(", ");
            }
            declarationType.append(this.renderTypeArgument(typeArguments.get(i)));
        }
        declarationType.append('>');

        return new TypeRender(declarationType.toString(), simpleName + ".class");
    }

    private String renderTypeArgument(TypeMirror type) {
        if (type.getKind() == TypeKind.WILDCARD) {
            return "?";
        }
        if (type.getKind() == TypeKind.TYPEVAR) {
            return "Object";
        }

        return this.renderType(type).declarationType;
    }

    private String renderRawType(TypeMirror type) {
        TypeKind kind = type.getKind();
        if (kind.isPrimitive()) {
            PrimitiveType primitiveType = (PrimitiveType) type;
            return this.processingEnv.getTypeUtils().boxedClass(primitiveType).getSimpleName().toString();
        }
        if (kind == TypeKind.ARRAY) {
            return this.renderRawType(((ArrayType) type).getComponentType());
        }
        if (kind != TypeKind.DECLARED) {
            return "Object";
        }

        DeclaredType declaredType = (DeclaredType) type;
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        return typeElement.getSimpleName().toString();
    }

    private void writeClassFooter(PrintWriter writer, String columnsClassName) {
        writer.println("    private " + columnsClassName + "() {");
        writer.println("    }");
        writer.println("}");
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

    private String qualify(String packageName, String simpleName) {
        return Strings.isBlank(packageName)
            ? simpleName
            : packageName + "." + simpleName;
    }

    private String toConstantName(String fieldName) {
        StringBuilder buffer = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                buffer.append('_');
            }
            buffer.append(Character.toUpperCase(c));
        }

        return buffer.toString();
    }

    private static final class TypeRender {

        private final String declarationType;
        private final String classLiteral;

        private TypeRender(String declarationType, String classLiteral) {
            this.declarationType = declarationType;
            this.classLiteral = classLiteral;
        }
    }
}
