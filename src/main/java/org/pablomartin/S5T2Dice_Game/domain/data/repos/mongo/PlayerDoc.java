package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "Players")
@Getter
@Setter // for if defined property access
@ToString
public class PlayerDoc {

    /*
    If this doc changes and stores (? extens Collection<T>):
    -> then maybe will be interesting change how is loaded with projections
    loadCredentialsByUserId
    loadCredentialsByUsername
    loadCredentialsByRefreshTokenId
    findUserRole

    *** update credentials needs to load all this entity

    delete all tokens is not affected, unless I want to change the method
        (load all player + set the collection of dbrefs to empty + save the plaer)
        anyway is needed remove de refresh token doc in their collection (no cascade in mongo)
    Idem for delete all rolls.

    benefit for change:
    find all rolls, find player, find all players
    deletePlayer (in sql)
     */

    @MongoId
    private UUID playerId; //before persisting the ID is provided (not autogenerated)

    @Nonnull
    private String username;

    @Nonnull
    private SecurityDetailsMongo securityDetails;

    @Nonnull
    private LocalDateTime instantSingup;

    //one unique all args constructor, visible only at package level
    PlayerDoc(UUID playerId, String username, SecurityDetailsMongo securityDetails, LocalDateTime instantSingup) {
        this.playerId = playerId;
        this.username = username;
        this.securityDetails = securityDetails;
        this.instantSingup = instantSingup;
    }

    public static PlayerDoc of(@NotNull UUID playerId, @NotNull NewPlayerInfo credentials, @NotNull LocalDateTime now){
        return new PlayerDoc(playerId, credentials.getUsername(), SecurityDetailsMongo.of(credentials),now);
    }

    public SecurityClaims toCredentialsForAccessJWT() {
        PlayerSecurity securityModel = PlayerSecurity.builder()
                .role(getSecurityDetails().getRole())
                // no refresh token id
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
        PlayerDoc playerDoc = (PlayerDoc) o;
        return getPlayerId().equals(playerDoc.getPlayerId()) && getUsername().equals(playerDoc.getUsername()) && getSecurityDetails().equals(playerDoc.getSecurityDetails()) && getInstantSingup().equals(playerDoc.getInstantSingup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId(), getUsername(), getSecurityDetails(), getInstantSingup());
    }
}
