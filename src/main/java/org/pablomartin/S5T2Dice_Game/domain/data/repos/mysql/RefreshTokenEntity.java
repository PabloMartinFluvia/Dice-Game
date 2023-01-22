package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity(name = "RefreshTokens")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tokenId;

    @ManyToOne
    @JoinColumn(name="player_id", nullable=false)
    /*
    Relació NO bidireccional.
    Establerta aquí per evitar que "llegir un player" impliqui llegir també totels refresh tokens
        (no és estrictament necessari).
    Cicle de vida independent, però com que no existeix l'opció DELETE player no és un problema.
     */
    private PlayerEntity owner;

    public RefreshTokenEntity(PlayerEntity owner) {
        this.owner = owner;
    }
}
