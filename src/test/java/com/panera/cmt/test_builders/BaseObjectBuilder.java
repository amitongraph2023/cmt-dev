package com.panera.cmt.test_builders;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

abstract class BaseObjectBuilder<T> {

    abstract T getTestClass();

    public T build() {
        return build(false);
    }
    public T build(boolean convertNumbersToInt) {
        T result = getTestClass();

        for(Field sourceField : this.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(sourceField.getModifiers()) && !Modifier.isFinal(sourceField.getModifiers())) {
                try {
                    Class currentClass = result.getClass();
                    Field destinationField = getDeclaredField(sourceField, currentClass);
                    if(destinationField == null) {
                        while(destinationField == null && currentClass.getSuperclass() != null) {
                            currentClass = currentClass.getSuperclass();
                            destinationField = getDeclaredField(sourceField, currentClass);
                        }
                    }

                    boolean destinationFieldWasPrivate = makeFieldAccessible(destinationField);
                    boolean sourceWasPrivate = makeFieldAccessible(sourceField);

                    if (convertNumbersToInt) {
                        copyFieldIntValue(result, sourceField, destinationField);
                    } else {
                        copyFieldValue(result, sourceField, destinationField);
                    }

                    revertToOriginalAccessibility(destinationFieldWasPrivate, destinationField);
                    revertToOriginalAccessibility(sourceWasPrivate, sourceField);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private Field getDeclaredField(Field sourceField, Class currentClass) throws NoSuchFieldException {
        try {
            return currentClass.getDeclaredField(sourceField.getName());
        } catch (Exception e) {
            return null;
        }
    }

    private void copyFieldValue(T result, Field sourceField, Field destinationField) throws IllegalAccessException {
        if (!Modifier.isStatic(destinationField.getModifiers()) && !Modifier.isFinal(destinationField.getModifiers())) {
            destinationField.set(result, sourceField.get(this));
        }
    }

    private void copyFieldIntValue(T result, Field sourceField, Field destinationField) throws IllegalAccessException {
        if (!Modifier.isStatic(destinationField.getModifiers()) && !Modifier.isFinal(destinationField.getModifiers())) {
            if (sourceField.getType().equals(Long.class)) {
                destinationField.set(result, (long) ((Long) sourceField.get(this)).intValue());
            } else if (sourceField.getType().equals(Double.class)) {
                destinationField.set(result, (double) ((Double) sourceField.get(this)).intValue());
            } else {
                destinationField.set(result, sourceField.get(this));
            }
        }
    }

    private void revertToOriginalAccessibility(boolean destinationFieldWasPrivate, Field destinationField) {
        if (destinationFieldWasPrivate) {
            destinationField.setAccessible(false);
        }
    }

    private boolean makeFieldAccessible(Field field) {
        boolean fieldWasPrivate = false;
        if (!field.isAccessible()) {
            fieldWasPrivate = true;
            field.setAccessible(true);
        }
        return fieldWasPrivate;
    }


}
