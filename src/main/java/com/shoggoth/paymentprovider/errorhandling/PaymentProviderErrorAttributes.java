package com.shoggoth.paymentprovider.errorhandling;

import com.shoggoth.paymentprovider.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

@Component
public class PaymentProviderErrorAttributes extends DefaultErrorAttributes {

    public PaymentProviderErrorAttributes() {
        super();
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, options);
        var error = getError(request);

        var errorList = new ArrayList<Map<String, Object>>();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        switch (error) {
            case WebExchangeBindException  ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                handleError(errorList, "VALIDATION_ERROR", Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
            }
            case MerchantAuthenticationException ex -> {
                httpStatus = HttpStatus.UNAUTHORIZED;
                handleError(errorList, ex.getErrorCode(), error.getMessage());
            }
            case NotFoundException ex -> {
                httpStatus = HttpStatus.NOT_FOUND;
                handleError(errorList, ex.getErrorCode(), error.getMessage());
            }
            case NotEnoughFundsException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                handleError(errorList, ex.getErrorCode(), error.getMessage());
            }
            case TimePeriodException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                handleError(errorList, ex.getErrorCode(), error.getMessage());
            }

            case RestException ex -> {
                httpStatus = HttpStatus.BAD_REQUEST;
                handleError(errorList, ex.getErrorCode(), error.getMessage());
            }
            default -> {
                String errorMessage = error.getMessage();
                String message = Objects.isNull(errorMessage) ?
                        error.getClass().getName() :
                        errorMessage;
                handleError(errorList, "INTERNAL_SERVER_ERROR", message);
            }
        }

        var errors = new HashMap<String, Object>();
        errors.put("errors", errorList);
        errorAttributes.put("status", httpStatus.value());
        errorAttributes.put("errors", errors);
        return errorAttributes;
    }

    private void handleError(List<Map<String, Object>> errorList, String code, String message) {
        var errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("code", code);
        errorMap.put("message", message);
        errorList.add(errorMap);
    }
}
