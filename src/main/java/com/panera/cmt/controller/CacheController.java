package com.panera.cmt.controller;

import com.panera.cmt.service.ICacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Managers in-memory cache", produces = "application/json")
@RequestMapping(value = "/api/v1/cache/clear")
@RestController
@Slf4j
public class CacheController {
    private ICacheService cacheService;

    @Autowired
    public CacheController(ICacheService cacheService) {this.cacheService = cacheService; }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_USER','ROLE_ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "OK", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public ResponseEntity<?> resetCache() {
        log.info("Clearing all cached data");
        cacheService.clearAllCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
