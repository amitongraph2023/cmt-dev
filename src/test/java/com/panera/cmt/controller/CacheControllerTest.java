package com.panera.cmt.controller;

import com.panera.cmt.service.IAuthenticationService;
import com.panera.cmt.service.ICacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CacheController.class, secure = false)
public class CacheControllerTest {

    @MockBean private ICacheService cacheService;
    @MockBean private IAuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void resetCacheTest() throws Exception {
        this.mockMvc.perform(post("/api/v1/cache/clear")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(cacheService, times(1)).clearAllCache();
    }

}