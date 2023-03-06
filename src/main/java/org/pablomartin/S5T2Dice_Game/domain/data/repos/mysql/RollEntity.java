package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.Roll;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "Rolls")
@Getter
@Setter // recommended for jpa
@ToString
public class RollEntity {

    @Id
    @GeneratedValue
    private UUID rollId;

    @NotEmpty
    //Basic type -> no @ElementCollection
    private int[] dicesValues; //array of basic types ok (array of embeddable: better Collection or subtype)

    @ManyToOne(optional = false) // no cascade
    @JoinColumn(name = "playerId")
    private PlayerEntity player;

    @Nonnull
    private LocalDateTime instantRoll;

    RollEntity() {
        /*
        no args constructor, limited to package visibility
        due jpa specification
         */
    }

    public static RollEntity of(RollDetails rollDetails, @NotNull PlayerEntity playerEntity,  LocalDateTime now) {
        RollEntity roll = new RollEntity();
        roll.dicesValues = rollDetails.getDicesValues();
        roll.player = playerEntity;
        roll.instantRoll = now;
        return roll;
    }

    public RollDetails toRollDetails(){
        return Roll.builder()
                .rollId(rollId) // stored only as auxiliar value when sorting
                .dicesValues(dicesValues)
                .instantRoll(instantRoll)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RollEntity that = (RollEntity) o;
        return getRollId().equals(that.getRollId()) && Arrays.equals(getDicesValues(), that.getDicesValues()) && getPlayer().equals(that.getPlayer()) && getInstantRoll().equals(that.getInstantRoll());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getRollId(), getPlayer(), getInstantRoll());
        result = 31 * result + Arrays.hashCode(getDicesValues());
        return result;
    }
}
