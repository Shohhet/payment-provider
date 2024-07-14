package com.shoggoth.paymentprovider.exception;

import com.shoggoth.paymentprovider.dto.TransactionErrorResponse;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final HttpStatus defaultHttpStatus;
    private final Map<Class<? extends RestException>, HttpStatus> restExceptionToHttpStatus;

    public ReactiveExceptionHandler(ErrorAttributes errorAttributes,
                                    WebProperties.Resources resources,
                                    ApplicationContext applicationContext,
                                    HttpStatus defaultHttpStatus, Map<Class<? extends RestException>,
                                    HttpStatus> restExceptionToHttpStatus) {
        super(errorAttributes, resources, applicationContext);
        this.defaultHttpStatus = defaultHttpStatus;
        this.restExceptionToHttpStatus = restExceptionToHttpStatus;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Throwable error = getError(serverRequest);
        HttpStatus httpStatus = defaultHttpStatus;
        if (error instanceof RestException e) {
            httpStatus = restExceptionToHttpStatus.getOrDefault(e.getClass(), defaultHttpStatus);
        }
        var errorResponse = new TransactionErrorResponse(httpStatus.toString(), error.getMessage());
        return ServerResponse
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}
