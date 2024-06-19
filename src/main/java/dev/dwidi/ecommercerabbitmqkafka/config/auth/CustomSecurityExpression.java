package dev.dwidi.ecommercerabbitmqkafka.config.auth;

import dev.dwidi.ecommercerabbitmqkafka.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CustomSecurityExpression {

    public boolean isAdminOrSelf(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();
        String role = userDetails.getRole();

        return "ROLE_ADMIN".equals(role) || userId.equals(authenticatedUserId);
    }
}