package com.github.vitalibo.alarm.api.core.model;

import com.github.vitalibo.alarm.api.core.util.ErrorState;
import com.github.vitalibo.alarm.api.core.util.Jackson;
import com.github.vitalibo.alarm.api.core.util.Resources;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HttpErrorTest {

    @Test
    public void testJsonSerDe() {
        HttpError expected = Jackson.fromJsonString(
            Resources.asInputStream("/HttpError.json"), HttpError.class);

        HttpError actual = new HttpError()
            .withRequestId("83105c54-9821-40d4-9d6c-4c9a4a2ba78d")
            .withMessage("Bad Request")
            .withStatus(400)
            .withErrors(new ErrorState() {{
                this.addError("foo", "bar");
            }});

        Assert.assertEquals(actual, expected);
    }

}