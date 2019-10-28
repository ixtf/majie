package org.jzb.majie.interfaces.rest;

import com.google.inject.Inject;
import org.jzb.majie.application.AuthService;
import org.jzb.majie.application.command.TokenCommand;
import reactor.core.publisher.Mono;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.security.Principal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author jzb 2019-10-24
 */
@Path("")
@Produces(APPLICATION_JSON)
public class AuthResource {
    private final AuthService authService;

    @Inject
    private AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @Path("token")
    @POST
    @Produces(TEXT_PLAIN)
    public Mono<String> token(TokenCommand command) {
        return authService.token(command);
    }

    @Path("/api/auth")
    @GET
    public Mono auth(Principal principal) {
        return authService.auth(principal);
    }
}
