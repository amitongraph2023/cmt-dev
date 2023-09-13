package com.panera.cmt.repository;

import com.panera.cmt.entity.AppConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAppConfigRepository extends CrudRepository<AppConfig, Long> {

    Boolean existsByCode(@Param("code") String code);

    Optional<AppConfig> getByCode(@Param("code") String code);

    @Query(value = "select ac from AppConfig ac where upper(ac.code) like concat(concat('%', upper(:query)), '%') or ac.value like concat(concat('%', :query), '%')",
            countQuery = "select count(ac) from AppConfig ac where upper(ac.code) like concat(concat('%', upper(:query)), '%') or ac.value like concat(concat('%', :query), '%')")
    Page<AppConfig> searchAppConfigPaged(@Param("query") String query, Pageable page);

    @Query("select ac from AppConfig ac where lower(ac.code) like concat('%', lower(:code), '%')")
    List<AppConfig> searchByCode(@Param("code") String code);



}
