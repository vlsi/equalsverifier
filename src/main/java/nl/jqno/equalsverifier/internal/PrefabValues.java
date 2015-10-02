/*
 * Copyright 2010, 2012-2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal;

import nl.jqno.equalsverifier.internal.exceptions.RecursionException;
import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.prefabvaluefactory.PrefabValueFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Container and creator of prefabricated instances of objects and classes.
 *
 * @author Jan Ouwens
 */
public class PrefabValues {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_OBJECT_MAPPER = createPrimitiveObjectMapper();
    private final StaticFieldValueStash stash;
    private final Map<TypeTag, Tuple<?>> values = new HashMap<>();
    private final Map<Class<?>, PrefabValueFactory<?>> factories = new HashMap<>();

    public PrefabValues() {
        this(new StaticFieldValueStash());
    }

    public PrefabValues(StaticFieldValueStash stash) {
        this.stash = stash;
    }

    /**
     * Backs up the values of all static member fields of the given type.
     *
     * @param typeTag TypeTag for the type for which to store the values of
     *          static member fields.
     */
    public void backupToStash(TypeTag typeTag) {
        stash.backup(typeTag);
    }

    /**
     * Restores the values of all static member fields, for all types for which
     * they were stored at once.
     */
    public void restoreFromStash() {
        stash.restoreAll();
    }

    public <T> void addFactory(Class<T> type, PrefabValueFactory<T> factory) {
        factories.put(type, factory);
    }

    /**
     * Associates the specified values with the specified class in this
     * collection of prefabricated values.
     *
     * @param <T> The type of value to put into this {@link PrefabValues}.
     * @param typeTag TypeTag for the class of the values.
     * @param red A value of type T.
     * @param black Another value of type T.
     */
    public <T> void put(TypeTag typeTag, T red, T black) {
        values.put(typeTag, new Tuple<>(red, black));
    }

    /**
     * Copies all prefabricated values of the specified {@link PrefabValues} to
     * this one.
     *
     * @param from Prefabricated values to be copied to this
     *          {@link PrefabValues}.
     */
    public void putAll(PrefabValues from) {
        values.putAll(from.values);
    }

    /**
     * Tests whether prefabricated values exist for the specified class.
     *
     * @param typeTag TypeTag for the Class whose presence in this
     *          {@link PrefabValues} is to be tested.
     * @return True if prefabricated values exist for the specified class.
     */
    public boolean contains(TypeTag typeTag) {
        return values.containsKey(typeTag);
    }

    /**
     * Getter for the "red" prefabricated value of the specified type.
     *
     * @param typeTag TypeTag for the class for which to return the
     *          prefabricated value.
     * @return The "red" prefabricated value for the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getRed(TypeTag typeTag) {
        return (T)getTuple(typeTag).red;
    }

    /**
     * Getter for the "black" prefabricated value of the specified type.
     *
     * @param typeTag TypeTag for the class for which to return the
     *          prefabricated value.
     * @return The "black" prefabricated value for the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T> T getBlack(TypeTag typeTag) {
        return (T)getTuple(typeTag).black;
    }

    @SuppressWarnings("unchecked")
    private <T> Tuple<T> getTuple(TypeTag typeTag) {
        putFor(typeTag);
        return (Tuple<T>)values.get(typeTag);
    }

    /**
     * Returns a prefabricated value for type which is not equal to value.
     *
     * @param typeTag TypeTag for the class for which to return a prefabricated
     *          value.
     * @param value An instance of type.
     * @return A prefabricated value for type which is not equal to value.
     */
    public Object getOther(TypeTag typeTag, Object value) {
        if (typeTag == null) {
            throw new ReflectionException("Type is null.");
        }

        Class<?> type = typeTag.getType();
        if (value != null && !type.isAssignableFrom(value.getClass()) && !wraps(type, value.getClass())) {
            throw new ReflectionException("Type does not match value.");
        }

        Tuple<?> tuple = getTuple(typeTag);

        if (type.isArray() && arraysAreDeeplyEqual(tuple.red, value)) {
            return tuple.black;
        }
        if (!type.isArray() && tuple.red.equals(value)) {
            return tuple.black;
        }

        return tuple.red;
    }

    private boolean wraps(Class<?> expectedClass, Class<?> actualClass) {
        return PRIMITIVE_OBJECT_MAPPER.get(expectedClass) == actualClass;
    }

    private boolean arraysAreDeeplyEqual(Object x, Object y) {
        // Arrays.deepEquals doesn't accept Object values so we need to wrap them in another array.
        return Arrays.deepEquals(new Object[] { x }, new Object[] { y });
    }

    /**
     * Creates instances for the specified type, and for the types of the
     * fields contained within the specified type, recursively, and adds them.
     *
     * Both created instances are guaranteed not to be equal to each other,
     * but are not guaranteed to be non-null. However, nulls will be very rare.
     *
     * @param typeTag TypeTag for the type to create prefabValues for.
     * @throws RecursionException If recursion is detected.
     */
    public void putFor(TypeTag typeTag) {
        putFor(typeTag, new LinkedHashSet<TypeTag>());
    }

    private void putFor(TypeTag typeTag, LinkedHashSet<TypeTag> typeStack) {
        if (noNeedToCreatePrefabValues(typeTag)) {
            return;
        }
        if (typeStack.contains(typeTag)) {
            throw new RecursionException(typeStack);
        }

        stash.backup(typeTag);
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>)typeStack.clone();
        clone.add(typeTag);

        Class<?> type = typeTag.getType();

        if (factories.containsKey(type)) {
            putGenericInstances(factories.get(type), typeTag);
        }
        else if (type.isEnum()) {
            putEnumInstances(typeTag);
        }
        else if (type.isArray()) {
            putArrayInstances(typeTag, clone);
        }
        else {
            traverseFields(typeTag, clone);
            createAndPutInstances(typeTag);
        }
    }

    private boolean noNeedToCreatePrefabValues(TypeTag typeTag) {
        return contains(typeTag) || typeTag.getType().isPrimitive();
    }

    private void putGenericInstances(PrefabValueFactory<?> factory, TypeTag typeTag) {
        for (TypeTag inner : typeTag.getGenericTypes()) {
            putFor(inner);
        }
        Object red = factory.createRed(typeTag, this);
        Object black = factory.createBlack(typeTag, this);
        put(typeTag, red, black);
    }

    private void putEnumInstances(TypeTag typeTag) {
        Object[] enumConstants = typeTag.getType().getEnumConstants();

        switch (enumConstants.length) {
            case 0:
                throw new ReflectionException("Enum " + typeTag.getType().getSimpleName() + " has no elements");
            case 1:
                put(typeTag, enumConstants[0], enumConstants[0]);
                break;
            default:
                put(typeTag, enumConstants[0], enumConstants[1]);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void putArrayInstances(TypeTag typeTag, LinkedHashSet<TypeTag> typeStack) {
        Class<?> componentType = typeTag.getType().getComponentType();
        TypeTag componentTypeTag = new TypeTag(componentType);
        putFor(componentTypeTag, typeStack);
        Object red = Array.newInstance(componentType, 1);
        Array.set(red, 0, getRed(componentTypeTag));
        Object black = Array.newInstance(componentType, 1);
        Array.set(black, 0, getBlack(componentTypeTag));
        put(typeTag, red, black);
    }

    private void traverseFields(TypeTag typeTag, LinkedHashSet<TypeTag> typeStack) {
        for (Field field : FieldIterable.of(typeTag.getType())) {
            int modifiers = field.getModifiers();
            boolean isStaticAndFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
            if (!isStaticAndFinal) {
                putFor(TypeTag.rawTypeTagFor(field), typeStack);
            }
        }
    }

    private void createAndPutInstances(TypeTag typeTag) {
        ClassAccessor<?> accessor = ClassAccessor.of(typeTag.getType(), this, false);
        Object red = accessor.getRedObject();
        Object black = accessor.getBlackObject();
        put(typeTag, red, black);
    }

    private static Map<Class<?>, Class<?>> createPrimitiveObjectMapper() {
        Map<Class<?>, Class<?>> result = new HashMap<>();
        result.put(boolean.class, Boolean.class);
        result.put(byte.class, Byte.class);
        result.put(char.class, Character.class);
        result.put(double.class, Double.class);
        result.put(float.class, Float.class);
        result.put(int.class, Integer.class);
        result.put(long.class, Long.class);
        result.put(short.class, Short.class);
        return result;
    }

    private static final class Tuple<T> {
        private T red;
        private T black;

        private Tuple(T red, T black) {
            this.red = red;
            this.black = black;
        }
    }
}
