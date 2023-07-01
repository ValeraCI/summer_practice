package com.ledivax.util;

import com.ledivax.models.AccountDetails;
import com.ledivax.models.RoleTitle;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private Key secretKey;
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.token.lifetimeInMinutes}")
    private Integer tokenTime;

    @Value("${jwt.token.secret}")
    private String secret;

    @PostConstruct
    public void configure() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    public String create(Authentication authentication) {
        LocalDateTime now = LocalDateTime.now();
        Date exp = Date.from(now.plusMinutes(tokenTime)
                .atZone(ZoneId.systemDefault()).toInstant());

        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        Claims claims = Jwts.claims().setSubject(username);
        RoleTitle role = RoleTitle.valueOf(authentication.getAuthorities()
                .stream()
                .collect(Collectors.toList()).get(0).getAuthority());
        claims.put("role", role.name());

        Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(nowDate)
                .setNotBefore(nowDate)
                .setExpiration(exp)
                .signWith(secretKey)
                .compact();

    }


    public String getEmail(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Boolean validate(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            logger.error("Method validate was throwing exception {}",
                    e.getMessage());
            return false;
        }
    }
}