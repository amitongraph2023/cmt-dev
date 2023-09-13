package com.panera.cmt.dto.app_config;

import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class PageOfAppConfigIncoming {
    List<AppConfig> content;
    boolean last;
    int totalPages;
    int totalElements;
    int size;
    int number;
    boolean first;
    AppConfigSortColumn sortCol;
    Sort.Direction sortDir;
    int numberOfElements;
}
