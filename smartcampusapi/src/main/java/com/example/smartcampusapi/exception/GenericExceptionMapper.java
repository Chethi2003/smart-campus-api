package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {

        ErrorMessage error = new ErrorMessage(
                "Internal server error",
                500,
                "Unexpected failure"
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
        .type(MediaType.APPLICATION_JSON)   // 🔥 ADD THIS
        .build();
    }
}