package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.UUID;

//@Entity
@Document(collection = "Players")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PlayerDoc {



    //@Id
    @MongoId
    //@GeneratedValue
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID playerId;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime registerDate;

    public PlayerDoc(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
