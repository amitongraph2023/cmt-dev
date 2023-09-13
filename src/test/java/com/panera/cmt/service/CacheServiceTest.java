package com.panera.cmt.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class CacheServiceTest {

    @InjectMocks private CacheService classUnderTest;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test public void clearAllCache() {
        classUnderTest.clearAllCache();
    }

}