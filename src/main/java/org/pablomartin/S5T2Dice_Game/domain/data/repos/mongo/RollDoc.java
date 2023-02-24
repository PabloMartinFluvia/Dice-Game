package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.Roll;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "Rolls")
@Getter
@Setter // for if defined property acccess
@ToString
public class RollDoc {

    @MongoId
    private UUID rollId;

    @NotEmpty
    private int[] dicesValues;

    @DBRef
    @Nonnull
    private PlayerDoc player;

    @Nonnull
    private LocalDateTime instantRoll;

    //one unique all args constructor, visible only at package level
    RollDoc(UUID rollId, int[] dicesValues, PlayerDoc player, LocalDateTime instantRoll) {
        this.rollId = rollId;
        this.dicesValues = dicesValues;
        this.player = player;
        this.instantRoll = instantRoll;
    }

    //factory method
    public static RollDoc of(@NotNull UUID rollId, RollDetails rollDetails, @NotNull PlayerDoc playerDoc, LocalDateTime now) {
        return new RollDoc(rollId, rollDetails.getDicesValues(), playerDoc, now);
    }

    public RollDetails toRollDetails(){
        return Roll.builder()
                .rollId(rollId) // sotored only as auxiliar value when sorting
                .dicesValues(dicesValues)
                //no info if won
                .instantRoll(instantRoll)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RollDoc rollDoc = (RollDoc) o;
        return getRollId().equals(rollDoc.getRollId()) && Arrays.equals(getDicesValues(), rollDoc.getDicesValues()) && getPlayer().equals(rollDoc.getPlayer()) && getInstantRoll().equals(rollDoc.getInstantRoll());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getRollId(), getPlayer(), getInstantRoll());
        result = 31 * result + Arrays.hashCode(getDicesValues());
        return result;
    }
}
