package svt.projekat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import svt.projekat.model.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getEmailFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
            System.out.println("Extracted username from token: " + username);
        } catch (Exception e) {
            username = null;
            e.printStackTrace();
        }
        return username;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Claims from token: " + claims);
        } catch (Exception e) {
            claims = null;
            e.printStackTrace();
        }
        return claims;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getEmailFromToken(token);
        boolean isValid = (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        System.out.println("Token validation result: " + isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        boolean isExpired = expirationDate.before(new Date());
        System.out.println("Token expiration check: " + isExpired);
        return isExpired;
    }

    private Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
            e.printStackTrace();
        }
        return expiration;
    }

    public String generateToken(UserDetails userDetails, User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("type", user.getClass().getSimpleName());
        claims.put("created", new Date());
        claims.put("userId", user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public int getExpiredIn() {
        return expiration.intValue();
    }
}
