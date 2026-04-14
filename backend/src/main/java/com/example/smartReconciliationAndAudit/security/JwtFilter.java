package com.example.smartReconciliationAndAudit.security;

import com.example.smartReconciliationAndAudit.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
private final JwtUtil jwtUtil;
private final UserDetailsService userDetailsService;
    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
String header=req.getHeader("Authorization");
if(header !=null && header.startsWith("Bearer ")){
String token=header.substring(7);
try{
String username=jwtUtil.username(token);
if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
    UserDetails u=userDetailsService.loadUserByUsername(username);
 if(jwtUtil.validate(token,u)){
var auth=new UsernamePasswordAuthenticationToken(u,null,u.getAuthorities());
auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

 }
}
}
catch (Exception Ignored){}

}
chain.doFilter(req,res);
    }
}
