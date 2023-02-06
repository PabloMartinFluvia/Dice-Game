package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.StatusDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AverageWinRateDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.PlayerDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DefaultResponsesProvider implements ResponsesProvider{

    @Override
    public ResponseEntity<?> forSingUp(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .username(details.getUsername()) // may be null when sing up as anonymous
                .accessJwt(details.getAccessJwt())
                .refreshJwt(details.getRefreshJwt())
                .build();
        //TODO: add uri location in response
        //return ResponseEntity.created(Uri location).body(accessInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Override
    public ResponseEntity<?> forRegisterAnonymous(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .username(details.getUsername())
                .accessJwt(details.getAccessJwt())
                .build();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> forUpdateRegistered(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .username(details.getUsername()) // may be null if only password update
                .accessJwt(details.getAccessJwt()) // may be null if only password update (old access jwt still valid)
                .build();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> fromDeleteUser() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> fromPromoteUser() {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> forLogin(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .accessJwt(details.getAccessJwt())
                .refreshJwt(details.getRefreshJwt())
                .build();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> forReset(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .accessJwt(details.getAccessJwt())
                .refreshJwt(details.getRefreshJwt())
                .build();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> forAccessJwt(@NotNull AccessDetails details) {
        CredentialsDto dto = CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .accessJwt(details.getAccessJwt())
                .build();
        return ResponseEntity.ok(dto);
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
        return ResponseEntity.ok(new AverageWinRateDto(avg));
    }

    @Override
    public ResponseEntity<?> forWinRate(@NotNull StatusDetails player) {
        PlayerDto dto = toPlayerDto(player);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> forPlayersRanked(@NotNull Collection<StatusDetails> playersRanked) {
        Set<PlayerDto> dtos = new LinkedHashSet<>();
        PlayerDto dto;
        for(StatusDetails player: playersRanked){
            dto = toPlayerDto(player);
            dtos.add(dto);
        }
        return ResponseEntity.ok(dtos);
    }

    private PlayerDto toPlayerDto(@NotNull StatusDetails player){
        return PlayerDto.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .numRolls(player.getNumRolls())
                .winRate(player.getWinRate())
                .build();
    }

    @Override
    public ResponseEntity<?> forNewRoll(@NotNull RollDetails roll) {
        RollDto dto = toRollDto(roll);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> forListRolls(@NotNull Collection<RollDetails> rolls) {
        Set<RollDto> dtos = new LinkedHashSet<>();
        RollDto dto;
        for(RollDetails roll: rolls){
            dto = toRollDto(roll);
            dtos.add(dto);
        }
        return ResponseEntity.ok(rolls);
    }

    private RollDto toRollDto(@NotNull RollDetails roll){
        return RollDto.builder()
                .dicesValues(roll.getDicesValues())
                .result(roll.isWon()?"WIN":"LOOSE")
                .instant(roll.getInstant())
                .build();
    }

    @Override
    public ResponseEntity<?> forDeleteRolls() {
        return ResponseEntity.noContent().build();
    }
}
