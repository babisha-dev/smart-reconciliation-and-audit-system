package com.example.smartReconciliationAndAudit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;



@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private Long expiration;
    public Key key(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generate(UserDetails u, String role)
    {
        return Jwts.builder()
                .setClaims(Map.of("role",role))
                .setSubject(u.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key(), SignatureAlgorithm.ES256)
                .compact();
    }

  public String username(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
  }
  public Boolean validate(String  token, UserDetails u){
        try{
            Claims claims=Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username=claims.getSubject();
            return username.equals(u.getUsername()) && claims.getExpiration().before(new Date());
        }
        catch(Exception e){
            return false;
        }
  }



}
