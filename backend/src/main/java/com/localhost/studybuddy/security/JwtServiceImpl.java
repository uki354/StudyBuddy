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
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
        File publicKeyFile = new File("public.key");
        File privateKeyFile = new File("private.key");

        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

        KeyFactory rsa = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        privateKey = rsa.generatePrivate(privateKeySpec);
        publicKey = rsa.generatePublic(publicKeySpec);
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
