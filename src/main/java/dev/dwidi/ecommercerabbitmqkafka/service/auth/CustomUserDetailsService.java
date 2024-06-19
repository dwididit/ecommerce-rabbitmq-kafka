package dev.dwidi.ecommercerabbitmqkafka.service.auth;

import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import dev.dwidi.ecommercerabbitmqkafka.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserRole().name(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name()))
        );
    }
}
