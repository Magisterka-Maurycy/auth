package org.maurycy.framework.auth.exception.mapper

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.jboss.resteasy.reactive.ClientWebApplicationException
import org.maurycy.framework.auth.model.ExceptionDto

@Provider
class ClientWebApplicationExceptionMapper : ExceptionMapper<ClientWebApplicationException> {
    override fun toResponse(exception: ClientWebApplicationException): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionDto(exception.localizedMessage)).build()
    }
}