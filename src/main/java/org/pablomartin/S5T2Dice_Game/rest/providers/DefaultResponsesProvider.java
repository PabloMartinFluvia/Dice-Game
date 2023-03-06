package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.AccessInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/*
this class separated from controllers and dto provider due it would be used
if HATEOAS specifications wants to be implemented (add links in responses)
 */

@Component
@RequiredArgsConstructor
public class DefaultResponsesProvider implements ResponsesProvider{

    private final DtoProvider dto;


    @Override
    public ResponseEntity<?> forSingUp(@NotNull AccessInfo details) {
        //TODO: add uri location in response: return ResponseEntity.created(Uri location).body(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto.ofFullCredentials(details));
    }

    @Override
    public ResponseEntity<?> forRegisterAnonymous(@NotNull AccessInfo details) {
        return ResponseEntity.ok(dto.ofCredentialsWithoutRefresh(details));
    }

    @Override
    public ResponseEntity<?> forUpdateRegistered(@NotNull AccessInfo details) {
        return ResponseEntity.ok(dto.ofCredentialsWithoutRefresh(details));
    }

    @Override
    public ResponseEntity<?> fromDeleteUser() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> forLogin(@NotNull AccessInfo details) {
        return ResponseEntity.ok(dto.ofCredentialsWithoutUsername(details));
    }

    @Override
    public ResponseEntity<?> forReset(@NotNull AccessInfo details) {
        return ResponseEntity.ok(dto.ofCredentialsWithoutUsername(details));
    }

    @Override
    public ResponseEntity<?> forAccessJwt(@NotNull AccessInfo details) {
        return ResponseEntity.ok(dto.ofCredentialsOnlyAccess(details));
    }

    @Override
    public ResponseEntity<?> forLogout() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> forLogoutAll() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> forAverageWinRate(@Max(1) @Min(0) float avg) {
        return ResponseEntity.ok(dto.ofAverage(avg));
    }

    @Override
    public ResponseEntity<?> forWinRate(@NotNull RankedDetails player) {
        return ResponseEntity.ok(dto.ofGame(player));
    }

    @Override
    public ResponseEntity<?> forPlayersRanked(@NotNull List<? extends RankedDetails> playersRanked) {
        return ResponseEntity.ok(dto.ofRanking(playersRanked));
    }

    @Override
    public ResponseEntity<?> forNewRoll(@NotNull RollDetails roll) {
        //TODO: add uri location in response: return ResponseEntity.created(Uri location).body(roll);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto.ofRoll(roll));
    }

    @Override
    public ResponseEntity<?> forListRolls(@NotNull List<RollDetails> rollsSorted) {
        return ResponseEntity.ok(dto.ofRolls(rollsSorted));
    }


    @Override
    public ResponseEntity<?> forDeleteRolls() {
        return ResponseEntity.noContent().build();
    }
}
