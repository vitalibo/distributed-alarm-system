package com.github.vitalibo.alarm.api.core.util;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;

public class RulesTest {

    @Mock
    private BiConsumer<HttpRequest, ErrorState> mockHttpRequestRule;
    @Mock
    private BiConsumer<String, ErrorState> mockRule;

    private Rules<String> rules;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rules = new Rules<>(
            Collections.singletonList(mockHttpRequestRule),
            Collections.singletonList(mockRule));
    }

    @Test
    public void testVerifyPreRules() {
        HttpRequest request = new HttpRequest();
        rules.verify(request);

        Mockito.verify(mockHttpRequestRule).accept(Mockito.eq(request), Mockito.any());
        Mockito.verify(mockRule, Mockito.never()).accept(Mockito.any(), Mockito.any());
    }

    @Test
    public void testVerifyPostRules() {
        rules.verify("foo");

        Mockito.verify(mockHttpRequestRule, Mockito.never()).accept(Mockito.any(), Mockito.any());
        Mockito.verify(mockRule).accept(Mockito.eq("foo"), Mockito.any());
    }

    @Test
    public void testVerify() {
        Rules<String> rules = new Rules<>(
            Collections.emptyList(),
            Collections.singletonList((request, errorState) -> errorState.addError("foo", request)));

        ValidationException actual = Assert.expectThrows(ValidationException.class, () -> rules.verify("bar"));

        ErrorState errorState = actual.getErrorState();
        Assert.assertEquals(errorState.get("foo"), Arrays.asList("bar"));
    }

}