package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.PlayerSecurity;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "RefreshTokens")
@Getter
@Setter // recommended for jpa
@ToString
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private UUID refreshTokenId;

    @ManyToOne(optional = false) // no cascade
    @JoinColumn(name = "playerId")
    private PlayerEntity player;

    RefreshTokenEntity() { //no args constructor, limited to package visibility
        //due jpa specification
    }

    //factory method
    public static RefreshTokenEntity of(@NotNull PlayerEntity playerEntity) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.player = playerEntity;
        return token;
    }

    public SecurityClaims toCredentialsForJWT() {
        PlayerSecurity securityModel = PlayerSecurity.builder()
                .role(player.getSecurityDetails().getRole())
                .refreshTokenId(refreshTokenId)
                .build();

        return Player.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .security(securityModel)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenEntity that = (RefreshTokenEntity) o;
        return getRefreshTokenId().equals(that.getRefreshTokenId()) && getPlayer().equals(that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRefreshTokenId(), getPlayer());
    }
}
