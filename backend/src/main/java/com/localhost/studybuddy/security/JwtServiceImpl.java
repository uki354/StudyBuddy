package com.localhost.studybuddy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {


    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final long accessTokenDuration = 600000; // 10min
    private final long refreshTokenDuration = 15778440000L; // 6months

    @PostConstruct
    public void initializeKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    @Override
    public String generateJwtToken(User user, String issuer) {
//        long duration;
//        if (type.equals(ACCESS)){
//            duration = accessTokenDuration;
//        }else{
//            duration = refreshTokenDuration;
//        }

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(issuer)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenDuration))
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(getAlgorithm());
    }

    @Override
    public DecodedJWT verifyJwtToken(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

//    public RefreshToken generateRefreshTokenAndSave(User user, String issuer){
//        String token = generateJwtToken(user, issuer, TokenType.REFRESH);
//        RefreshToken refreshToken = RefreshToken.builder().token(token).isValid(true).checksumToken(hashToken(token)).build();
//        refreshTokenRepository.save(refreshToken);
//        return refreshToken;
//    }

//    public String refreshAccessToken(String token, HttpServletRequest request){
//        String tokenChecksum = hashToken(token);
//        RefreshToken refreshToken = refreshTokenRepository.findRefreshToken(tokenChecksum);
//        try{
//            DecodedJWT decodedJWT = verifyJwtToken(refreshToken.getToken());
//            Claim roles = decodedJWT.getClaim("roles");
//            SimpleGrantedAuthority[] simpleGrantedAuthorities = roles.asArray(SimpleGrantedAuthority.class);
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(Arrays.asList(simpleGrantedAuthorities));
//            User user = new User(decodedJWT.getSubject(),"", authorities);
//            return generateJwtToken(user,request.getRequestURL().toString(),ACCESS);
//        }catch (TokenExpiredException e){
//            refreshTokenRepository.invalidateRefreshToken(tokenChecksum);
//            throw new RuntimeException("Token has been expired");
//        }
//    }


    private Algorithm getAlgorithm() {
        return Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
    }
}
