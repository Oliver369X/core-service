package com.finwise.core;

import com.finwise.core.support.MySQLContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CoreServiceApplicationTests extends MySQLContainerSupport {

    @Test
    void contextLoads() {
    }
}
