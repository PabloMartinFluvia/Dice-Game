package org.pablomartin.S5T2Dice_Game.domain.models;

public enum Role {
    //if modified abreviatures + toString implemented -> role must be stored as authority as ROLE_xxxx
    //check how is stored + how it's loaded + and how is checked in security filters
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

    @Override
    public String toString() {
        return this.name();
    }
}
