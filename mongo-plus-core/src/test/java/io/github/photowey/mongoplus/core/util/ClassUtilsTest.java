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
package io.github.photowey.mongoplus.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClassUtilsTest - Tests for {@link ClassUtils}.
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ClassUtilsTest")
class ClassUtilsTest {

    @Test
    @DisplayName("Given valid class name When forName Then returns Class")
    @Story("Resolve class by name")
    void givenValidClassName_whenForName_thenReturnsClass() {
        Class<?> clazz = ClassUtils.forName("java.lang.String");
        assertEquals(String.class, clazz);
    }

    @Test
    @DisplayName("Given nonexistent class name When forName Then throws IllegalArgumentException")
    @Story("Throw on nonexistent class name")
    void givenNonexistentClassName_whenForName_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClassUtils.forName("nonexistent.ClassName");
        });
    }

    @Test
    @DisplayName("Given blank class name When forName Then throws IllegalArgumentException")
    @Story("Throw on blank class name")
    void givenBlankClassName_whenForName_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClassUtils.forName("");
        });
    }

    @Test
    @DisplayName("Given class name When isPresent Then returns true if class on classpath")
    @Story("Check if class present on classpath")
    void givenClassName_whenIsPresent_thenReturnsTrueIfOnClasspath() {
        assertTrue(ClassUtils.isPresent("java.lang.String"));
        assertTrue(ClassUtils.isPresent("java.util.List"));
        assertFalse(ClassUtils.isPresent("nonexistent.ClassName"));
        assertFalse(ClassUtils.isPresent(""));
        assertFalse(ClassUtils.isPresent(null));
    }

    @Test
    @DisplayName("Given full class name string When getShortName Then returns short name")
    @Story("Get short name from full class name string")
    void givenFullClassNameString_whenGetShortName_thenReturnsShortName() {
        assertEquals("String", ClassUtils.getShortName("java.lang.String"));
        assertEquals("List", ClassUtils.getShortName("java.util.List"));
        assertEquals(
            "Map.Entry",
            ClassUtils.getShortName("java.util.Map$Entry")
        );
        assertNull(ClassUtils.getShortName((String) null));
        assertEquals("", ClassUtils.getShortName(""));
    }

    @Test
    @DisplayName("Given Class When getShortName Then returns short name")
    @Story("Get short name from class")
    void givenClass_whenGetShortName_thenReturnsShortName() {
        assertEquals("String", ClassUtils.getShortName(String.class));
        assertEquals("List", ClassUtils.getShortName(List.class));
        assertNull(ClassUtils.getShortName((Class<?>) null));
    }

    @Test
    @DisplayName("Given full class name string When getPackageName Then returns package name")
    @Story("Get package name from full class name string")
    void givenFullClassNameString_whenGetPackageName_thenReturnsPackageName() {
        assertEquals("java.lang", ClassUtils.getPackageName("java.lang.String"));
        assertEquals("java.util", ClassUtils.getPackageName("java.util.List"));
        assertEquals("", ClassUtils.getPackageName("ClassWithoutPackage"));
        assertEquals("", ClassUtils.getPackageName((String) null));
        assertEquals("", ClassUtils.getPackageName(""));
    }

    @Test
    @DisplayName("Given Class When getPackageName Then returns package name or null")
    @Story("Get package name from class or null")
    void givenClass_whenGetPackageName_thenReturnsPackageNameOrNull() {
        assertEquals("java.lang", ClassUtils.getPackageName(String.class));
        assertEquals("java.util", ClassUtils.getPackageName(List.class));
        assertNull(ClassUtils.getPackageName((Class<?>) null));
    }

    @Test
    @DisplayName("Given super and sub type When isAssignable Then returns true if assignable")
    @Story("Check assignability between super and sub types")
    void givenSuperAndSubType_whenIsAssignable_thenReturnsTrueIfAssignable() {
        assertTrue(ClassUtils.isAssignable(String.class, String.class));
        assertTrue(ClassUtils.isAssignable(CharSequence.class, String.class));
        assertTrue(ClassUtils.isAssignable(Object.class, String.class));
        assertFalse(ClassUtils.isAssignable(String.class, Integer.class));
        assertFalse(ClassUtils.isAssignable(null, String.class));
        assertFalse(ClassUtils.isAssignable(String.class, null));
    }

    @Test
    @DisplayName("Given primitive and wrapper When isAssignable Then returns true for compatible pairs")
    @Story("Check assignability between primitive and wrapper")
    void givenPrimitiveAndWrapper_whenIsAssignable_thenReturnsTrueForCompatiblePairs() {
        // Primitive wrapper conversions
        assertTrue(ClassUtils.isAssignable(int.class, Integer.class));
        assertTrue(ClassUtils.isAssignable(Integer.class, int.class));
        assertTrue(ClassUtils.isAssignable(long.class, Long.class));
        assertTrue(ClassUtils.isAssignable(boolean.class, Boolean.class));
    }

    @Test
    @DisplayName("Given class When isPrimitiveWrapper Then returns true for wrapper types only")
    @Story("Check if class is primitive wrapper")
    void givenClass_whenIsPrimitiveWrapper_thenReturnsTrueForWrapperTypesOnly() {
        assertTrue(ClassUtils.isPrimitiveWrapper(Integer.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Long.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Boolean.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Double.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Character.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Byte.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Short.class));
        assertTrue(ClassUtils.isPrimitiveWrapper(Float.class));

        assertFalse(ClassUtils.isPrimitiveWrapper(String.class));
        assertFalse(ClassUtils.isPrimitiveWrapper(int.class));
        assertFalse(ClassUtils.isPrimitiveWrapper(null));
    }

    @Test
    @DisplayName("Given class When isPrimitiveOrWrapper Then returns true for primitive or wrapper")
    @Story("Check if class is primitive or wrapper")
    void givenClass_whenIsPrimitiveOrWrapper_thenReturnsTrueForPrimitiveOrWrapper() {
        assertTrue(ClassUtils.isPrimitiveOrWrapper(int.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Integer.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(boolean.class));
        assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.class));

        assertFalse(ClassUtils.isPrimitiveOrWrapper(String.class));
        assertFalse(ClassUtils.isPrimitiveOrWrapper(null));
    }

    @Test
    @DisplayName("Given ClassUtils When instantiate via reflection Then throws AssertionError")
    @Story("Utility class rejects reflection instantiation")
    void givenClassUtils_whenInstantiateViaReflection_thenThrowsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> {
            try {
                Constructor<ClassUtils> ctor = ClassUtils.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(error.getMessage().contains("No io.github.photowey.mongoplus.core.util.ClassUtils"));
    }
}
