package org.pablomartin.S5T2Dice_Game.security.basic;

import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class PlayerDetails implements UserDetails, CredentialsContainer {

    private UUID playerId;

    private String username;

    private String password;

    private Set<? extends GrantedAuthority> authorities;

    public PlayerDetails (Player player){
        this.playerId = player.getPlayerId();
        this.username = player.getUsername();
        this.password = player.getPassword();
        this.authorities = collectAuthorities(player);
    }

     /*
     In this project:
     This class will be used as principal for any Authentication.
        Obs: Only set the authorities required for authorization in secured resources

     This class also will be used for to provide the needed info to create the jwt.
     */

    private Set<? extends GrantedAuthority> collectAuthorities(Player player){
        Set<GrantedAuthority> grantedAuthorities= new HashSet<>();

        grantedAuthorities.add(new SimpleGrantedAuthority(player.getRole().withPrefix()));

        /*
        Player still don't require other authorities.
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
         */
        return grantedAuthorities;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
