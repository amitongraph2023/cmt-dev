package com.panera.cmt.controller.chub;

import com.panera.cmt.controller.BaseController;
import com.panera.cmt.service.chub.IStaticDataService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Customer Hub Static Data Controller", description = "Searches Customers", produces = "application/json")
@RequestMapping(value = "/api/v1/static/chub")
@RestController
@Slf4j
public class StaticDataController extends BaseController {

    private IStaticDataService staticDataService;

    @Autowired
    public StaticDataController(IStaticDataService staticDataService) {
        this.staticDataService = staticDataService;
    }

    @ApiOperation(value = "searchCustomers, Required Authorities: Admin, Cbss, Cbss Supervisor, Prod Support, Sales Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Object.class, responseContainer = "List")
    })
    @RequestMapping(value = "/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> searchCustomers(@ApiParam("The search type")
                                                      @PathVariable(value = "type") String type) {
        return staticDataService.getStaticData(type)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
