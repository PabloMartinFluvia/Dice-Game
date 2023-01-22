package org.pablomartin.S5T2Dice_Game.domain.models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RefreshToken {

    private UUID tokenId;

    private Player owner;

    public RefreshToken(Player owner) {
        this.owner = owner;
    }
}
