package com.topchoir.directory.resource;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
import com.topchoir.directory.dto.LoginInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should configure CORS for their use case.
@CrossOrigin(origins = "*")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${auth0.clinet.realm}")
    private String realm;

    @GetMapping("/public")
    public String publicEndpoint() {
        return "public";
    }

    @PostMapping("/login")
    public TokenHolder login(@RequestBody LoginInput input) throws Auth0Exception {
        // login using auth0 client
        AuthAPI auth = AuthAPI.newBuilder(domain, clientId, clientSecret).build();

        TokenRequest loginRequest =  auth.login(input.getEmail(), input.getPassword().toCharArray(), realm);

        return loginRequest.execute().getBody();
    }
}