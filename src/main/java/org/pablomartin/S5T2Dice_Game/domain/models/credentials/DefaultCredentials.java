package org.pablomartin.S5T2Dice_Game.domain.models.credentials;

import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext;

import java.util.UUID;


public class DefaultCredentials implements AuthenticationCredentials, ProvidedCredentials, AccessDetails{

    private UUID playerId;

    private String username;

    private String password;

    private Role role;

    private String accessJwt, refreshJwt;

    //Strings: access jwt + refresh jwt


    public static DefaultCredentials.CredentialsBuilder builder(){
        return new DefaultCredentials.CredentialsBuilder();
    }

    @Override
    public boolean isUsernameProvided(){
        return username !=null && !username.equalsIgnoreCase(DiceGameContext.getDefaultUsername());
    }

    @Override
    public AccessDetails toAccessDetails(String accessJwt, String refreshJwt) {
        this.accessJwt = accessJwt;
        this.refreshJwt = refreshJwt;
        if(username.equalsIgnoreCase(DiceGameContext.getDefaultUsername())){
            username = null;
        }
        return this;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }



    //Meanwhile not used outside will be private
    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setRole(Role role) {
        this.role = role;
    }

    public static class CredentialsBuilder {

        private DefaultCredentials credentials;

        private CredentialsBuilder(){
            credentials = new DefaultCredentials();
        }

        public CredentialsBuilder asRegistered(String username, String password){
            credentials.setUsername(username);
            credentials.setPassword(password);
            credentials.setRole(Role.REGISTERED);
            return this;
        }

        public CredentialsBuilder asAnnonimous(){
            credentials.setUsername(DiceGameContext.getDefaultUsername());
            //password remains null
            credentials.setRole(Role.ANONYMOUS);
            return this;
        }

        public CredentialsBuilder playerId (UUID playerId){
            credentials.setPlayerId(playerId);
            return this;
        }

        public CredentialsBuilder username(String username){
            credentials.setUsername(username);
            return this;
        }

        public CredentialsBuilder password(String encodedPassword){
            credentials.setPassword(encodedPassword);
            return this;
        }

        public CredentialsBuilder role(Role role){
            credentials.setRole(role);
            return this;
        }


        public DefaultCredentials build(){
            return credentials;
        }
    }
}
