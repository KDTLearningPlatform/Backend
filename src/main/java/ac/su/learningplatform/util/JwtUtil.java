package ac.su.learningplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token, String secret) {
        return extractClaim(token, Claims::getSubject, secret);
    }

    public Date extractExpiration(String token, String secret) {
        return extractClaim(token, Claims::getExpiration, secret);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token, String secret) {
        return extractExpiration(token, secret).before(new Date());
    }

    public String generateToken(String email, String secret) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, secret);
    }

    private String createToken(Map<String, Object> claims, String subject, String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails, String secret) {
        final String username = extractUsername(token, secret);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, secret));
    }
}
