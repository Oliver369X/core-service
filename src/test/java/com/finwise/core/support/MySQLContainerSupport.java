package com.finwise.core.support;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public abstract class MySQLContainerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLContainerSupport.class);

    private static final boolean DOCKER_AVAILABLE = isDockerAvailable();

    private static final MySQLContainer<?> MYSQL_CONTAINER = DOCKER_AVAILABLE
            ? new MySQLContainer<>(DockerImageName.parse("mysql:8.4.0"))
            .withDatabaseName("finwise_core_test")
            .withUsername("test_user")
            .withPassword("test_password")
            : null;

    private static boolean isDockerAvailable() {
        try {
            org.testcontainers.DockerClientFactory.instance().client();
            return true;
        } catch (Throwable ex) {
            LOGGER.warn("Docker no está disponible; las pruebas usarán H2 en memoria. Causa: {}", ex.getMessage());
            return false;
        }
    }

    private static void ensureMySqlStarted() {
        if (MYSQL_CONTAINER != null && !MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.start();
        }
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        if (MYSQL_CONTAINER != null) {
            ensureMySqlStarted();
            registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
            registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
            registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
        } else {
            final String h2Url = "jdbc:h2:mem:finwise_core_test_" + UUID.randomUUID().toString().replace("-", "") + ";MODE=MySQL";
            registry.add("spring.datasource.url", () -> h2Url);
            registry.add("spring.datasource.username", () -> "sa");
            registry.add("spring.datasource.password", () -> "");
            registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        }
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }
}
