package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {

        ErrorMessage error = new ErrorMessage(
                ex.getMessage(),
                403,
                "Sensor unavailable"
        );

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .build();
    }
}