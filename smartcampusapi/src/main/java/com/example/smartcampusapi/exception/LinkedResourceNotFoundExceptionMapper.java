package com.example.smartcampusapi.exception;

import com.example.smartcampusapi.model.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {

        ErrorMessage error = new ErrorMessage(
                ex.getMessage(),
                422,
                "Linked resource not found"
        );

        return Response.status(422)
                .entity(error)
                .build();
    }
}