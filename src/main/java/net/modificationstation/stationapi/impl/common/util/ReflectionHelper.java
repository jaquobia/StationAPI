package net.modificationstation.stationapi.impl.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

public class ReflectionHelper {

    private static final Field modifiers;

    static {
        try {
            modifiers = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldsWithAnnotation(Object target, Class<? extends Annotation> annotation, Object value) throws IllegalAccessException {
        setFieldsWithAnnotation(target.getClass(), target, annotation, value);
    }

    public static void setFieldsWithAnnotation(Class<?> targetClass, Object target, Class<? extends Annotation> annotation, Object value) throws IllegalAccessException {
        setFieldsWithAnnotation(targetClass, target, annotation, annotation1 -> value);
    }

    public static <T extends Annotation> void setFieldsWithAnnotation(Object target, Class<T> annotation, Function<T, Object> processor) throws IllegalAccessException {
        setFieldsWithAnnotation(target.getClass(), target, annotation, processor);
    }

    public static <T extends Annotation> void setFieldsWithAnnotation(Class<?> targetClass, Object target, Class<T> annotation, Function<T, Object> processor) throws IllegalAccessException {
        for (Field field : ReflectionHelper.getFieldsWithAnnotation(targetClass, annotation))
            ReflectionHelper.setPrivateFinalField(field, target, processor.apply(field.getAnnotation(annotation)));
    }

    public static Field[] getFieldsWithAnnotation(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        Field[] fields = new Field[0];
        for (Field field : targetClass.getFields())
            if (field.getAnnotation(annotationClass) != null) {
                fields = Arrays.copyOf(fields, fields.length + 1);
                fields[fields.length - 1] = field;
            }
        return fields;
    }

    public static void setPrivateFinalField(Field field, Object instance, Object value) throws IllegalAccessException {
        int mod = field.getModifiers();
        setPrivateField(modifiers, field, mod & ~Modifier.FINAL);
        setPrivateField(field, instance, value);
        setPrivateField(modifiers, field, mod);
    }

    public static void setPrivateField(Field field, Object instance, Object value) throws IllegalAccessException {
        boolean inaccessible = !field.isAccessible();
        if (inaccessible)
            field.setAccessible(true);
        field.set(instance, value);
        if (inaccessible)
            field.setAccessible(false);
    }
}