package com.virtualmarathon.Authorization.security;

import com.virtualmarathon.Authorization.model.LoginRequestModel;
import com.virtualmarathon.Authorization.service.UserService;
import com.virtualmarathon.Authorization.shared.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment environment;
    public AuthenticationFilter(UserService userService, Environment environment, AuthenticationManager authenticationManager){
        this.userService=userService;
        this.environment=environment;
        super.setAuthenticationManager(authenticationManager);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            LoginRequestModel creds=new ObjectMapper().readValue(request.getInputStream(),LoginRequestModel.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(),creds.getPassword(),new ArrayList<>()));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String userName=((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails=userService.getUserDetailsByEmail(userName);
        String token= Jwts.builder().setSubject(userDetails.getEmail()).setExpiration(new Date(System.currentTimeMillis()+Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS256,environment.getProperty("token.secret")).compact();
        response.addHeader("token",token);
        response.addHeader("emailId",userDetails.getEmail());
        Map<String, Object> jwtObject=new HashMap<>();
        jwtObject.put("jwt",token);
        String json = new ObjectMapper().writeValueAsString(jwtObject);
        response.getWriter().write(json);
        response.flushBuffer();

    }
}
