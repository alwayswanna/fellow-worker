package a.gleb.oauth2server.mapper.authorization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class OAuth2Mapper {

    protected final ObjectMapper objectMapper;

    @PostConstruct
    void init () {
        var modules = SecurityJackson2Modules.getModules(this.getClass().getClassLoader());
        modules.addAll(List.of(new JavaTimeModule(), new OAuth2AuthorizationServerJackson2Module()));
        /* register modules for mapping */
        objectMapper.registerModules(modules);

        log.trace(
                "OAuth2Mapper, registered modules: {}",
                modules.stream().map(Module::getModuleName).collect(Collectors.joining())
        );
    }

    protected Map<String, Object> parseMap(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    protected String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

}
