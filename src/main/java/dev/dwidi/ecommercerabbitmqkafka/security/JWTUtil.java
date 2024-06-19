package dev.dwidi.ecommercerabbitmqkafka.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.issuer}")
    private String issuer;

    @Value("${jwt.token.accessValid}")
    private long accessTokenExpiration;

    @Value("${jwt.token.refreshValid}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String username, Long userId, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withClaim("type", "ACCESS")
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("type", "REFRESH")
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    public boolean validateToken(String token, String username) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getSubject().equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(new Date());
    }
}
