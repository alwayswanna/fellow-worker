package a.gleb.user_app.mapper.oauth;

import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.jackson.OAuth2AuthorizationServerJacksonModule;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

abstract class AbstractOAuthMapper {

    protected static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(AbstractOAuthMapper.class.getClassLoader()))
            .addModules(new OAuth2AuthorizationServerJacksonModule())
            .build();

    protected String writeMap(Map<String, Object> map) {
        return JSON_MAPPER.writeValueAsString(map);
    }

    protected Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return JSON_MAPPER.readValue(json, new TypeReference<>() {});
    }
}