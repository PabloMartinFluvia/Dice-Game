package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Players")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID playerId;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    //@CreationTimestamp // -> can't be used. @Transactional implies value won't be created until persisted,
    //                  implies Mongo can't copy the value (we want idem backup DB) until the transaction is completed
    //                  -> incoherent
    private LocalDateTime registerDate;

    public PlayerEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
