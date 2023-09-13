package com.panera.cmt.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static com.panera.cmt.util.SharedUtils.isNull;

public class DTOConverter {

    public static <T> T convert(T d, Object e) {
        if (isNull(d, e)) {
            return null;
        }

        for (Field destinationField : d.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(destinationField.getModifiers()) && !Modifier.isFinal(destinationField.getModifiers())) {
                try {
                    Class sourceClass = e.getClass();
                    Field sourceField = getDeclaredField(destinationField, sourceClass);
                    if (sourceField == null) {
                        while (sourceField == null && sourceClass.getSuperclass() != null) {
                            sourceClass = sourceClass.getSuperclass();
                            sourceField = getDeclaredField(destinationField, sourceClass);
                        }
                    }

                    if (sourceField != null) {
                        boolean destinationFieldWasPrivate = makeFieldAccessible(destinationField);
                        boolean sourceWasPrivate = makeFieldAccessible(sourceField);

                        destinationField.set(d, sourceField.get(e));

                        revertToOriginalAccessibility(destinationFieldWasPrivate, destinationField);
                        revertToOriginalAccessibility(sourceWasPrivate, sourceField);
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
        return d;
    }

    private static Field getDeclaredField(Field sourceField, Class sourceClass) throws NoSuchFieldException {
        try {
            return sourceClass.getDeclaredField(sourceField.getName());
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean makeFieldAccessible(Field field) {
        boolean fieldWasPrivate = false;
        if (!field.isAccessible()) {
            fieldWasPrivate = true;
            field.setAccessible(true);
        }
        return fieldWasPrivate;
    }

    private static void revertToOriginalAccessibility(boolean destinationFieldWasPrivate, Field destinationField) {
        if (destinationFieldWasPrivate) {
            destinationField.setAccessible(false);
        }
    }
}
