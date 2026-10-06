package com.acme.backoffice.security;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/** The logged-in user as seen by this app, built from the tokens Keycloak issued. */
@RequestScoped
public class CurrentUser {

    public static final String OPERADOR = "operador";
    public static final String SUPERVISOR = "supervisor";
    private static final List<String> APP_ROLES = List.of(OPERADOR, SUPERVISOR);

    @Inject
    SecurityIdentity identity;

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    AccessTokenCredential accessToken;

    public String username() {
        return idToken.getClaim("preferred_username");
    }

    public String displayName() {
        return Objects.requireNonNullElse(idToken.getClaim("name"), username());
    }

    public String email() {
        return idToken.getClaim("email");
    }

    /** Backoffice roles only (Keycloak also adds default roles such as offline_access). */
    public List<String> appRoles() {
        return APP_ROLES.stream().filter(identity::hasRole).toList();
    }

    public boolean hasRole(String role) {
        return identity.hasRole(role);
    }

    /** Keycloak groups, set by the IdP mappers from the corporate (Entra) groups. */
    public List<String> groups() {
        var groups = accessTokenClaims().getJsonArray("groups");
        return groups == null ? List.of() : groups.stream().map(String::valueOf).toList();
    }

    /** Access token payload, for display. Its signature was already validated by quarkus-oidc. */
    public JsonObject accessTokenClaims() {
        String payload = accessToken.getToken().split("\\.")[1];
        return new JsonObject(new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8));
    }
}
