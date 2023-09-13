package com.panera.cmt.controller.app_config;

import com.panera.cmt.dto.AllErrorsDTO;
import com.panera.cmt.dto.app_config.AppConfigDTO;
import com.panera.cmt.dto.PageDTO;
import com.panera.cmt.entity.AppConfig;
import com.panera.cmt.entity.ResponseHolder;
import com.panera.cmt.enums.AppConfigDomain;
import com.panera.cmt.enums.sort.AppConfigSortColumn;
import com.panera.cmt.service.app_config.IAppConfigLocalService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.emptyList;

@Api(value = "App Config Controller", description = "App Config CRUD Operations", produces = "application/json")
@RequestMapping(value = "/api/v1/app-config")
@RestController
@Slf4j
public class AppConfigController extends BaseAppConfigController {

    private IAppConfigLocalService appConfigService;

    @Autowired
    public AppConfigController(IAppConfigLocalService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @ApiOperation(value = "addAppConfig, Required Authority: Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = AppConfigDTO.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> addAppConfig(
            @Validated @RequestBody AppConfigDTO dto
            ,  @ApiParam(value = "Server to set AppConfig")
            @RequestParam(value = "service", required = false, defaultValue = "CMT") AppConfigDomain domain) {
        if (domain == AppConfigDomain.CMT) {
            return addLocalAppConfig(dto);
        } else {
            return addRemoteAppConfig(dto, domain);
        }
    }

    private ResponseEntity<?> addLocalAppConfig(AppConfigDTO dto) {
        if (appConfigService.doesAppConfigExist(dto.getCode())){
            return buildValidationErrorResponse("error.code.duplicate", "code");
        }
        return appConfigService.createLocalAppConfig(dto.toEntity())
                .map(ResponseHolder::getEntity)
                .map(AppConfigDTO::fromEntity)
                .map(savedDto -> new ResponseEntity<>(savedDto, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    private ResponseEntity<?> addRemoteAppConfig(AppConfigDTO dto, AppConfigDomain domain) {
        return appConfigService.createRemoteAppConfig(dto.toEntity(), domain)
                .map(responseHolder -> {
                    if (responseHolder.getStatus().is2xxSuccessful()) {
                        return new ResponseEntity<>(responseHolder.getEntity(), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(responseHolder.getErrors(), responseHolder.getStatus());
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "deleteAppConfigById, Required Authority: Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deleteAppConfigByCode(@ApiParam("The id of the property to delete")
                                                   @PathVariable("id") Long id,
                                                   @RequestParam(value = "service", required = false, defaultValue = "CMT") AppConfigDomain domain) {
        if (domain == AppConfigDomain.CMT) {
            return deleteLocalAppConfigByCode(id);
        }
        return deleteRemoteAppConfigByCode(id, domain);
    }

    private ResponseEntity<?> deleteLocalAppConfigByCode(Long id) {
        if (!appConfigService.doesAppConfigExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        appConfigService.deleteLocalAppConfigById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).contentLength(0L).build();
    }

    private ResponseEntity<?> deleteRemoteAppConfigByCode(Long id, AppConfigDomain domain) {
        return appConfigService.deleteRemoteAppConfigById(id, domain)
                .map(responseHolder -> {
                    if (responseHolder.getStatus().is2xxSuccessful()) {
                        return new ResponseEntity<>(responseHolder.getEntity(), HttpStatus.NO_CONTENT);
                    } else {
                        return new ResponseEntity<>(responseHolder.getErrors(), responseHolder.getStatus());
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiOperation(value = "getAllAppConfigsPaged, Required Authority: Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = PageDTO.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAllAppConfigsPaged(@ApiParam("The search query string")
                                                       @RequestParam(value = "query", required = false) String query,
                                                   @ApiParam(value = "The page number")
                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                   @ApiParam(value = "The page size")
                                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                                   @ApiParam(value = "The sort direction")
                                                       @RequestParam(value = "dir", required = false) Sort.Direction dir,
                                                   @ApiParam(value = "The sort column")
                                                       @RequestParam(value = "col", required = false) AppConfigSortColumn col,
                                                   @ApiParam(value = "The service/microservice to view")
                                                   @RequestParam(value = "service", required = false, defaultValue = "CMT") AppConfigDomain domain) {
        if (page < 1) {
            return buildValidationErrorResponse("error.page.greaterThanOne", "page");
        }
        if (size < 1) {
            return buildValidationErrorResponse("error.size.greaterThanOne", "size");
        }

        if (domain == AppConfigDomain.CMT) {
            return getLocalAllAppConfigsPaged(query, page, size, dir, col);
        }

        return getRemoteAllAppConfigsPaged(query, page, size, dir, col, domain);
    }

    private ResponseEntity<?> getLocalAllAppConfigsPaged(String query, Integer page, Integer size, Sort.Direction dir, AppConfigSortColumn col) {
        return appConfigService.searchLocalAppConfigPaged(query, page, size, dir, col)
                .map(ResponseHolder::getEntity)
                .map(entities -> PageDTO.convert(entities, AppConfigDTO.class))
                .map(dtos -> new ResponseEntity<>(dtos, HttpStatus.OK))
                .orElse(new ResponseEntity<>(PageDTO.convert(new PageImpl<>(emptyList()), AppConfig.class), HttpStatus.OK));
    }

    private ResponseEntity<?> getRemoteAllAppConfigsPaged(String query, Integer page, Integer size, Sort.Direction dir, AppConfigSortColumn col, AppConfigDomain domain) {
        return appConfigService.searchRemoteAppConfigPaged(query, page, size, dir, col, domain)
                .map(ResponseHolder::getEntity)
                .map(entities -> PageDTO.convert(entities, AppConfigDTO.class))
                .map(dtos -> new ResponseEntity<>(dtos, HttpStatus.OK))
                .orElse(new ResponseEntity<>(PageDTO.convert(new PageImpl<>(emptyList()), AppConfig.class), HttpStatus.OK));
    }

    @ApiOperation(value = "updateAppConfig, Required Authority: Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content", response = String.class),
            @ApiResponse(code = 404, message = "Not Found", response = String.class),
            @ApiResponse(code = 406, message = "Not Acceptable", response = AllErrorsDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateAppConfig(@ApiParam("The id of the property to update")
                                                 @PathVariable("id") Long id,
                                             @Validated @RequestBody AppConfigDTO dto,
                                             @RequestParam(value = "service", required = false, defaultValue = "CMT") AppConfigDomain domain) {
        if (domain == AppConfigDomain.CMT ) {
            return updateLocalAppConfig(id, dto);
        }

        return updateRemoteAppConfig(id, dto, domain);
    }

    private ResponseEntity<?> updateLocalAppConfig(Long id, AppConfigDTO dto) {
        if (!appConfigService.doesAppConfigExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (appConfigService.doesAppConfigExistExcludeId(dto.getCode(), id)) {
            return buildValidationErrorResponse("error.code.duplicate", "code");
        }

        return appConfigService.updateLocalAppConfig(id, dto.toEntity())
                .map(savedDto -> ResponseEntity.status(HttpStatus.NO_CONTENT).contentLength(0L).build())
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<?> updateRemoteAppConfig(Long id, AppConfigDTO dto, AppConfigDomain domain) {
        return appConfigService.updateRemoteAppConfig(id, dto.toEntity(), domain)
                .map(responseHolder -> {
                    if (responseHolder.getStatus().is2xxSuccessful()) {
                        return new ResponseEntity<>(responseHolder.getEntity(), HttpStatus.NO_CONTENT);
                    } else {
                        return new ResponseEntity<>(responseHolder.getErrors(), responseHolder.getStatus());
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
