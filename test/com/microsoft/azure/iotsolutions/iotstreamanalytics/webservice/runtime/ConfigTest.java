// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iotstreamanalytics.webservice.runtime;

import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConfigTest {

    @Before
    public void setUp() {
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void providesWebServicePort() {
        Config target = new Config();
        int port = target.getPort();
        assertThat(port, is(9023));
    }
}