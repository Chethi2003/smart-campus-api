package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Override
    public Response toResponse(NotAllowedException ex) {

        ErrorMessage error = new ErrorMessage(
                "HTTP method not allowed for this endpoint",
                405,
                "Use correct HTTP method"
        );

        return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}