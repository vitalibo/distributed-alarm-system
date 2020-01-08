package com.github.vitalibo.alarm.api.infrastructure.azure.function;

import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.model.HttpError;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import com.github.vitalibo.alarm.api.infrastructure.azure.Factory;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import lombok.RequiredArgsConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.microsoft.azure.functions.HttpMethod.*;

@RequiredArgsConstructor
public class HttpFunction {

    private final Factory factory;

    public HttpFunction() {
        this(Factory.getInstance());
    }

    @FunctionName("rules")
    public HttpResponseMessage handleRequest(
        @HttpTrigger(name = "req", methods = {GET, POST, PUT, DELETE}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<String> request,
        ExecutionContext context) {

        HttpRequest httpRequest = HttpRequestTranslator.from(request);
        HttpResponse<?> httpResponse = process(httpRequest, context);
        return HttpResponseTranslator.from(request, httpResponse);
    }

    public HttpResponse<?> process(HttpRequest request, ExecutionContext context) {
        try {
            final Facade facade;
            switch (request.getHttpMethod()) {
                case "PUT":
                    facade = factory.createCreateRuleFacade();
                    break;
                case "DELETE":
                    facade = factory.createDeleteRuleFacade();
                    break;
                case "GET":
                    facade = factory.createGetRuleFacade();
                    break;
                case "POST":
                    facade = factory.createUpdateRuleFacade();
                    break;
                default:
                    return new HttpError()
                        .withStatus(404)
                        .withMessage("Not Found")
                        .build();
            }

            return facade.process(request);
        } catch (ValidationException e) {
            return new HttpError()
                .withStatus(400)
                .withMessage("Bad Request")
                .withErrors(e.getErrorState())
                .build();

        } catch (Exception e) {
            Logger logger = context.getLogger();
            logger.log(Level.SEVERE, "Failed processing: " + e.getMessage(), e);
            return new HttpError()
                .withStatus(500)
                .withMessage("Internal Server Error")
                .build();
        }
    }

}