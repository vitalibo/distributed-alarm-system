package com.github.vitalibo.alarm.api.core.util;

import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class Rules<T> {

    private final List<BiConsumer<HttpRequest, ErrorState>> preRules;
    private final List<BiConsumer<T, ErrorState>> postRules;

    public void verify(HttpRequest request) {
        verify(preRules, request);
    }

    public void verify(T request) {
        verify(postRules, request);
    }

    private static <S> void verify(List<BiConsumer<S, ErrorState>> rules, S request) {
        final ErrorState errorState = new ErrorState();
        rules.forEach(rule -> rule.accept(request, errorState));
        if (errorState.hasErrors()) {
            throw new ValidationException(errorState);
        }
    }

}