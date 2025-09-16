package a.gleb.apicommon.event.account;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

public record AccountAdditionalInformation(
        UUID accountId,
        Set<UUID> roleIds,
        String username,
        boolean enabled
) {
    @Builder
    public AccountAdditionalInformation {}
}
