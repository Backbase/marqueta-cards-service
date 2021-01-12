package com.backbase.productled.repository;

import com.backbase.buildingblocks.backend.security.auth.config.SecurityContextUtil;
import com.backbase.dbs.user.manager.api.service.v2.UserManagementApi;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserRepository {

    private static final String MARQETA_USER_TOKEN = "marqetaUserToken";

    private final SecurityContextUtil securityContextUtil;

    private final UserManagementApi userManagementApi;

    public String getMarqetaUserToken() {
        log.info("Calling user manager for to retrieve marqeta user token");
        return Objects.requireNonNull(
            userManagementApi.getUserById(securityContextUtil.getInternalId().orElse(null), false).getAdditions())
            .getOrDefault(MARQETA_USER_TOKEN, null);
    }

}
