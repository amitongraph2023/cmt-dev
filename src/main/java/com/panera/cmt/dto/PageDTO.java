package com.panera.cmt.dto;

import com.panera.cmt.entity.AuditableEntity;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@SuppressWarnings("unchecked")
public class PageDTO<D> {

    private List<D> content;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private String sortCol;
    private String sortDir;
    private int numberOfElements;

    public static <D, E extends AuditableEntity> PageDTO convert(org.springframework.data.domain.Page page, Class<D> dClazz) {
        PageDTO dto = new PageDTO();

        dto.setContent((List<D>)page.getContent()
                .stream()
                .map(entity -> BaseDTO.toDTO((E)entity, dClazz))
                .collect(Collectors.toList()));

        dto.setLast(page.isLast());
        dto.setTotalPages(page.getTotalPages());
        dto.setTotalElements(page.getTotalElements());
        dto.setSize(page.getSize());
        dto.setNumber(page.getNumber());
        dto.setFirst(page.isFirst());
        dto.setNumberOfElements(page.getNumberOfElements());

        if (page.getSort() != null && page.getSort().toString().indexOf(":") > -1) {
            String[] split = page.getSort().toString().split(": ");
            dto.setSortCol(split[0]);
            dto.setSortDir(split[1]);
        }

        return dto;
    }
}
