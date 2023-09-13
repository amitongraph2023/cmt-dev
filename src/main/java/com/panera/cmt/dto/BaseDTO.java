package com.panera.cmt.dto;

import com.panera.cmt.entity.AuditableEntity;

@SuppressWarnings("unchecked")
public abstract class BaseDTO<D, T extends AuditableEntity> {

    public static <D, T extends AuditableEntity> D toDTO(T entity, Class<D> clazz) {
        D dto = createDTO(clazz);

        java.lang.reflect.Method method;
        try {
            method = dto.getClass().getMethod("fromEntity", entity.getClass());
            dto = (D) method.invoke(dto, entity);
        } catch (Exception e) {
            dto = null;
        }

        return dto;
    }

    private static <D> D createDTO(Class<D> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    public abstract D fromEntity(T entity);

    public abstract T toEntity();
}
