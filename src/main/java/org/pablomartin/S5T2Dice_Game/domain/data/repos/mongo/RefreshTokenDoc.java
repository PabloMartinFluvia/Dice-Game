package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(collection = "RefreshTokens")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RefreshTokenDoc {

    @MongoId
    private UUID tokenId;

    @DocumentReference(lazy = true) //DBREF
    /*
    Relació NO bidireccional.
    Establerta aquí per evitar que "llegir un player" impliqui llegir també totels refresh tokens
        (no és estrictament necessari).
    Cicle de vida independent, però com que no existeix l'opció DELETE player no és un problema.
     */
    private PlayerDoc owner;

    public RefreshTokenDoc(PlayerDoc owner) {
        this.owner = owner;
    }
}
