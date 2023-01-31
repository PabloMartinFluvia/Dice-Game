package org.pablomartin.S5T2Dice_Game.domain.models.credentials;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext;

import java.util.UUID;

@Setter
public class DefaultCredentialsProvider implements JwtCredentialsProvider, BasicCredentials{

    private UUID userId;

    private String username;

    private String password;

    private Role role;


    public static DefaultCredentialsProvider.CredentialsBuilder builder(){
        return new DefaultCredentialsProvider.CredentialsBuilder();
    }

    public static class CredentialsBuilder {

        private DefaultCredentialsProvider credentials;

        private CredentialsBuilder(){
            credentials = new DefaultCredentialsProvider();
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

        public CredentialsBuilder userId (UUID userId){
            credentials.setUserId(userId);
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


        public DefaultCredentialsProvider build(){
            return credentials;
        }
    }
}
