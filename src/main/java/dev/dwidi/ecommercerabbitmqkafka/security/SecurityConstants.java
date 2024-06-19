package dev.dwidi.ecommercerabbitmqkafka.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
public class SecurityConstants {

    @Value("#{'${security.whitelist.urls}'.split(',')}")
    private List<String> whiteListURLs;

}

