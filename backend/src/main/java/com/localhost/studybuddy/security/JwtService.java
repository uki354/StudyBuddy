package com.localhost.studybuddy.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.User;

public interface JwtService {

    String generateJwtToken(User user, String issuer);
    DecodedJWT verifyJwtToken(String token);


}
