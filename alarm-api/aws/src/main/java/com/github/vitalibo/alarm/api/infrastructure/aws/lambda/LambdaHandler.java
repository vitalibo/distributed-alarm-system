package com.github.vitalibo.alarm.api.infrastructure.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.vitalibo.alarm.api.core.Facade;
import com.github.vitalibo.alarm.api.core.model.HttpError;
import com.github.vitalibo.alarm.api.core.model.HttpRequest;
import com.github.vitalibo.alarm.api.core.model.HttpResponse;
import com.github.vitalibo.alarm.api.core.util.ValidationException;
import com.github.vitalibo.alarm.api.infrastructure.aws.Factory;
import com.github.vitalibo.alarm.api.infrastructure.aws.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Factory factory;

    public LambdaHandler() {
        this(Factory.getInstance());
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        HttpRequest request = ApiGatewayRequestTranslator.from(event);
        HttpResponse<?> response = handleRequest(request, context);
        return ApiGatewayResponseTranslator.from(response);
    }

    public HttpResponse<?> handleRequest(HttpRequest request, Context context) {
        try {
            final Facade facade;
            switch (Routes.route(request)) {
                case CreateRule:
                    facade = factory.createCreateRuleFacade();
                    break;
                case DeleteRule:
                    facade = factory.createDeleteRuleFacade();
                    break;
                case GetRule:
                    facade = factory.createGetRuleFacade();
                    break;
                case UpdateRule:
                    facade = factory.createUpdateRuleFacade();
                    break;
                case NotFound:
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
            logger.error("Failed processing: {}", e.getMessage(), e);
            return new HttpError()
                .withStatus(500)
                .withMessage("Internal Server Error")
                .build();
        }
    }

}