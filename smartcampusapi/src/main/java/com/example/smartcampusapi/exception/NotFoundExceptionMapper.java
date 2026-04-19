package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {

        ErrorMessage error = new ErrorMessage(
                exception.getMessage(),
                404,
                "https://smartcampus/api/errors"
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .type("application/json")
                .build();
    }
}