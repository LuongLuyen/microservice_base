package com.eazybytes.gatewayserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:7080/realms/master/protocol/openid-connect/certs"
        }
)
class GatewayserverApplicationTests {

    @Test
    void contextLoads() {
    }
}
