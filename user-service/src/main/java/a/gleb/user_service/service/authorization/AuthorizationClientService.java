package a.gleb.user_service.service.authorization;

import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientRequest;
import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientResponse;
import a.gleb.user_service.db.repository.authorization.AuthorizationClientRepository;
import a.gleb.user_service.exception.BadRequestException;
import a.gleb.user_service.mapper.authorization.AuthorizationClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static a.gleb.user_service.constant.OAuth2ServerConstants.MAX_ENTITIES_PER_PAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationClientService {

    private final AuthorizationClientMapper authorizationClientMapper;
    private final AuthorizationClientRepository authorizationClientRepository;

    /**
     * Method for load client`s by page.
     *
     * @param page number of page
     * @return {@link List}<{@link AuthorizationClientResponse}>
     */
    public List<AuthorizationClientResponse> all(int page) {
        log.debug("Request to get all registered clients. [page={}]", page);

        return authorizationClientRepository.findAll(PageRequest.of(page, MAX_ENTITIES_PER_PAGE))
                .stream()
                .map(authorizationClientMapper::toResponse)
                .toList();
    }

    /**
     * Method for create new authorization client.
     *
     * @param request data for new client.
     * @return {@link AuthorizationClientResponse} data of existing account.
     */
    public AuthorizationClientResponse create(AuthorizationClientRequest request) {
        log.debug("Request to create a new authorization client [clientId={}]", request);

        if (authorizationClientRepository.existsByClientId(request.clientId())) {
            throw new BadRequestException(String.format("Client with clientId=%s already exists", request.clientId()));
        }

        var entity = authorizationClientMapper.toEntity(request);
        var saved = authorizationClientRepository.save(entity);

        log.debug("Created new authorization client [clientId={}, id={}]", saved.getClientId(), saved.getId());

        return authorizationClientMapper.toResponse(saved);
    }
}
