/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.feign.configuration;

import a.gleb.clientmanager.configuration.properties.ClientManagerConfigurationProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String JWT_PREFIX = "Bearer %s";

    private final OAuth2AuthorizeRequest oAuth2AuthorizeRequest;
    private final ClientManagerConfigurationProperties properties;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public OAuth2FeignRequestInterceptor(
            ClientManagerConfigurationProperties properties,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        this.properties = properties;
        var oAuth2AuthorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                oAuth2AuthorizedClientService
        );
        oAuth2AuthorizedClientManager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().refreshToken().build());

        var registrationId = properties.getFeignConfig().getRegistrationId();
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
        this.oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(registrationId)
                .principal(new OAuth2Authentication(registrationId))
                .build();
    }


    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(AUTHORIZATION_HEADER_KEY, getAccessToken());
    }

    private String getAccessToken() {
        var client = authorize();
        return String.format(JWT_PREFIX, client.getAccessToken().getTokenValue());
    }


    private OAuth2AuthorizedClient authorize() {
        try {
            return oAuth2AuthorizedClientManager.authorize(oAuth2AuthorizeRequest);
        } catch (ClientAuthorizationException e) {
            return oAuth2AuthorizedClientManager.authorize(oAuth2AuthorizeRequest);
        }
    }
}
