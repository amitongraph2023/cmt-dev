package com.panera.cmt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuppressWarnings("unchecked")
@Table(name = "APP_CONFIG")
public class AppConfig extends AuditableEntity {

    @Column(name = "APP_CONFIG_ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="APP_CONFIG_SEQ")
    @Id
    @SequenceGenerator(name="APP_CONFIG_SEQ", sequenceName="APP_CONFIG_SEQ", allocationSize=1)
    private Long id;

    @Column(name = "APP_CONFIG_CD")
    private String code;

    @Column(name = "APP_CONFIG_VALUE")
    private String value;

    public String[] getStringArray(String regex) {
        if (value == null) {
            return new String[0];
        }

        return value.split(regex);
    }

    public List<String> getStringList(String regex) {
        return new ArrayList<>(asList(getStringArray(regex)));
    }
}
