package com.topchoir.directory.resource;

import com.auth0.client.auth.AuthAPI;
import com.topchoir.directory.dto.LoginInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should configure CORS for their use case.
@CrossOrigin(origins = "*")
public class AuthController {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;


    @PostMapping("/login")
    public String login(@RequestBody LoginInput input) {
        // login using auth0 client
        AuthAPI auth = AuthAPI.newBuilder("{YOUR_DOMAIN}",
                "{YOUR_CLIENT_ID}", "{YOUR_CLIENT_SECRET}").build();

        return LoginInput.class.getSimpleName() + " " + input.getUsername() + " " + input.getPassword();
    }
}