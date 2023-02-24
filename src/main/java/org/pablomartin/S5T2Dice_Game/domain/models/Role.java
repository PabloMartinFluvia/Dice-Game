package org.pablomartin.S5T2Dice_Game.domain.models;

public enum Role {
    ADMIN,
    REGISTERED,
    ANONYMOUS;

    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }

    public String withPrefix() {
        return PREFIX + this.toString();
    }
}
