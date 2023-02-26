package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Builder
@ToString
@EqualsAndHashCode
public class Player implements NewPlayerInfo, GameDetails, SecurityClaims, InfoForAppAccess {

    private UUID playerId;

    private String username;

    private PlayerSecurity security;

    private Game game;

    public static Player asRegistered(String username, String passwordEncoded){
        return Player.builder()
                .username(username)
                .security(PlayerSecurity.builder()
                        .passwordEncoded(passwordEncoded)
                        .role(Role.REGISTERED)
                        .build())
                .build();
    }

    public static Player asAnonymous(){
        return Player.builder()
                .username(DiceGamePathsContext.getDefaultUsername())
                .security(PlayerSecurity.builder()
                        .role(Role.ANONYMOUS)
                        .build())
                .build();
    }

    @Override
    public boolean isUsernameProvided() {
        return username !=null && !username.equalsIgnoreCase(DiceGamePathsContext.getDefaultUsername());
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public Optional<UUID> getPlayerAuthenticatedId() {
        if(playerId != null){
            return Optional.of(playerId);
        }else {
            return Optional.empty();
        }
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public String getPasswordEncoded() {
        return security.getPasswordEncoded();
    }

    @Override
    public Role getRole() {
        return security.getRole();
    }

    @Override
    public UUID getRefreshTokenId(){
        return security.getRefreshTokenId();
    }

    @Override
    public InfoForAppAccess toAppAccess(String accessJwt, String refreshJwt) {
        security.setAccessJwt(accessJwt);
        security.setRefreshJwt(refreshJwt);
        if(username.equalsIgnoreCase(DiceGamePathsContext.getDefaultUsername())){
            username = null;
        }
        return this;
    }

    @Override
    public String getAccessJwt(){
        return security.getAccessJwt();
    }

    @Override
    public String getRefreshJwt(){
        return security.getRefreshJwt();
    }

    @Override
    public float getWinRate() {
        return game.getWinRate();
    }

    @Override
    public void calculateWinRate() {
        game.calculateWinRate();
    }

    @Override
    public int getNumRolls() {
        return game.getNumRolls();
    }

    @Override
    public Optional<List<RollDetails>> getRolls() {
        if(game.getRolls() != null){
            return Optional.of(game.getRolls());
        }else {
            return Optional.empty();
        }
    }
}
