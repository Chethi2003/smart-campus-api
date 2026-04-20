package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {

        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                400,
                "https://smartcampus/api/errors"
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}