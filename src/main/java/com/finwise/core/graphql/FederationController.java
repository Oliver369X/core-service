package com.finwise.core.graphql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class FederationController {

    @Value("classpath:graphql/core-schema.graphqls")
    private Resource schemaResource;

    @QueryMapping(name = "_service")
    public Map<String, String> getService() throws IOException {
        String sdl = schemaResource.getContentAsString(StandardCharsets.UTF_8);
        return Map.of("sdl", sdl);
    }
}

