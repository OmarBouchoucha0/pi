package tn.esprit.pi.config;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final String name;
    private final Long userId;

    public UserPrincipal(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }
}
