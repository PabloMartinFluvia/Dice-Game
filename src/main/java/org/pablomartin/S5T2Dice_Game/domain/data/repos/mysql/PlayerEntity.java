/*
    If this entity changes and stores @OneToMany:
    -> then maybe will be interesting change how is loaded with projections
    loadCredentialsByUserId
    loadCredentialsByUsername
    loadCredentialsByRefreshTokenId
    findUserRole

    *** update credentials needs to load all this entity

    delete all tokens is not affected, unless I want to change the method
        (load all player + set the collection to empty + save the plaer)

    benefit for change:
    find all rolls, find player, find all players
    deletePlayer (in sql)
     */

package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.pablomartin.S5T2Dice_Game.domain.models.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "Players")
@Getter
@Setter // recommended for jpa
@ToString
public class PlayerEntity {

    @Id
    @GeneratedValue
    private UUID playerId;

    @Nonnull
    private String username;

    @Nonnull
    private SecurityDetailsSql securityDetails; // de moment embebed

    @Nonnull
    private LocalDateTime instantSingup;

    PlayerEntity() {
        /*
        no args constructor, limited to package visibility
        due jpa specification
         */
    }

    public static PlayerEntity of(@NotNull NewPlayerInfo credentials, @NotNull LocalDateTime now){
        PlayerEntity entity = new PlayerEntity();
        entity.username = credentials.getUsername();
        entity.securityDetails =SecurityDetailsSql.of(credentials);
        entity.instantSingup = now;
        return entity;
    }

    public SecurityClaims toCredentialsForAccessJWT() {
        PlayerSecurity securityModel = PlayerSecurity.builder()
                .role(getSecurityDetails().getRole())
                .build();

        return Player.builder()
                .playerId(getPlayerId())
                .username(getUsername())
                .security(securityModel)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerEntity entity = (PlayerEntity) o;
        return Objects.equals(getPlayerId(), entity.getPlayerId()) && Objects.equals(getUsername(), entity.getUsername()) && Objects.equals(getSecurityDetails(), entity.getSecurityDetails()) && Objects.equals(getInstantSingup(), entity.getInstantSingup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId(), getUsername(), getSecurityDetails(), getInstantSingup());
    }
}
