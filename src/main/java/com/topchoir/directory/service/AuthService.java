package com.topchoir.directory.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.SignUpRequest;
import com.auth0.net.TokenRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${auth0.client.realm}")
    private String realm;

    public String getUserId(String email, String password) throws Auth0Exception {
        // signup new auth0 user
        AuthAPI auth = AuthAPI.newBuilder(domain, clientId, clientSecret).build();
        SignUpRequest signUp = auth.signUp(email, password.toCharArray(), realm);
        String userId = signUp.execute().getBody().getUserId();
        logger.info("New user created with id: " + userId + " and email " + email);
        return userId;
    }

    public TokenHolder login(String email, String password) throws Auth0Exception {
        // login using auth0 client
        AuthAPI auth = AuthAPI.newBuilder(domain, clientId, clientSecret).build();

        TokenRequest loginRequest = auth.login(email, password.toCharArray(), realm);

        return loginRequest.execute().getBody();
    }
}
