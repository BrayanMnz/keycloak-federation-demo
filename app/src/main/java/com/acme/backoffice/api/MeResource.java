package com.acme.backoffice.api;

import com.acme.backoffice.security.CurrentUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Map;

/** Small JSON API, handy for curl/scripted checks of what the UI shows. */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {

    public record Me(String username, String email, List<String> groups, List<String> roles) {}

    @Inject
    CurrentUser user;

    @GET
    @Path("/me")
    public Me me() {
        return new Me(user.username(), user.email(), user.groups(), user.appRoles());
    }

    @GET
    @Path("/operador")
    @RolesAllowed(CurrentUser.OPERADOR)
    public Map<String, String> operador() {
        return Map.of("page", "operador", "access", "granted");
    }

    @GET
    @Path("/supervisor")
    @RolesAllowed(CurrentUser.SUPERVISOR)
    public Map<String, String> supervisor() {
        return Map.of("page", "supervisor", "access", "granted");
    }
}
