package org.pablomartin.S5T2Dice_Game.domain.models;

public enum Role {
    ADMIN,
    REGISTERED,
    VISITOR; //IMPORTANT: ROLE_ANONYMOUS reservat per al AnonymousAuthentication Filter/Token


    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }

    public String withPrefix() {
        return PREFIX + this;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
