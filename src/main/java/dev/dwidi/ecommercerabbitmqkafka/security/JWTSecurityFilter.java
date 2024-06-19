package dev.dwidi.jobportal.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dwidi.jobportal.config.util.RequestIdGenerator;
import dev.dwidi.jobportal.dto.PublicResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JWTSecurityFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityConstants securityConstants;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RequestIdGenerator requestIdGenerator;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = request.getRequestURI();
        // Add debug log to check the request URL
        log.debug("Request URL: {}", path);

        boolean isWhitelisted = securityConstants.getWhiteListURLs().stream()
                .anyMatch(whitelistUrl -> path.matches(whitelistUrl.replace("**", ".*")));

        // Add debug log to check if the URL is whitelisted
        log.debug("Is Whitelisted: {}", isWhitelisted);

        if (isWhitelisted) {
            chain.doFilter(request, response);
            return;
        }

        String requestId = RequestIdGenerator.generateRequestId();

        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Add debug log for successful authentication
            log.debug("User authenticated successfully: {}", authentication.getName());
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(
                    mapper.writeValueAsString(
                            new PublicResponseDTO<String>(HttpStatus.UNAUTHORIZED.value(), "Token authentication failed", requestId, null)
                    )
            );
            return;
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.error("Token can't be null or empty");
            throw new Exception("Token can't be null or empty");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        log.debug("Processed token: {}", token);

        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        if (decodedJWT == null) {
            log.error("Failed to decode token");
            throw new Exception("Failed to decode token");
        }

        String userName = decodedJWT.getSubject();
        String role = decodedJWT.getClaim("role").asString();
        String tokenType = decodedJWT.getClaim("type").asString();
        if (!tokenType.equalsIgnoreCase("ACCESS")) {
            log.error("Token is not an Access Token");
            throw new Exception("Token is not an Access Token");
        }

        log.info("Authority: {}", role);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        return new UsernamePasswordAuthenticationToken(userName, null, Collections.singletonList(authority));
    }
}
