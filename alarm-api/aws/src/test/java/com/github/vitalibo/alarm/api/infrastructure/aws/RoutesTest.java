package com.github.vitalibo.alarm.api.infrastructure.aws;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RoutesTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {"/foo", "GET", Routes.NotFound},
            {"/rules", "HEAD", Routes.NotFound},
            {"/rules", "PUT", Routes.CreateRule},
            {"/rules", "DELETE", Routes.DeleteRule},
            {"/rules", "GET", Routes.GetRule}
        };
    }

    @Test(dataProvider = "samples")
    public void testRoute(String path, String method, Routes expected) {
        HttpRequest request = new HttpRequest();
        request.setPath(path);
        request.setHttpMethod(method);

        Routes actual = Routes.route(request);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, expected);
    }

}