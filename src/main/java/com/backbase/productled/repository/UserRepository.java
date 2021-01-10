package com.backbase.productled.repository;

import com.backbase.buildingblocks.backend.security.auth.config.SecurityContextUtil;
import com.backbase.dbs.user.manager.api.service.v2.UserManagementApi;
import com.backbase.dbs.user.manager.api.service.v2.model.BatchUser;
import com.backbase.dbs.user.manager.api.service.v2.model.User;
import java.util.Collections;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserRepository {

    private final SecurityContextUtil securityContextUtil;

    private final UserManagementApi userManagementApi;

    public String getMarqetaUserToken() {

        userManagementApi.updateUserInBatch(Collections.singletonList(new BatchUser().externalId("paolo")
            .userUpdate(new User()
                .externalId("paolo")
                .fullName("Paolo Doe")
                .legalEntityId("8a81978c765221ba01765268c0d60009")
            .putAdditionsItem("marqetaUserToken", "1be8bb0b-dcdd-4219-81ab-565621d3707c"))));
        log.info("Calling user manager for to retrieve marqeta user token");
        return Objects.requireNonNull(
            userManagementApi.getUserById(securityContextUtil.getInternalId().orElse(null), false).getAdditions())
            .getOrDefault("marqetaUserToken", null);
    }

}
