package org.pablomartin.S5T2Dice_Game.security.basic;

import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DefaultBasicPrincipal implements BasicPrincipal {

    private UUID playerId;

    private String username;

    private String password;

    private Set<? extends GrantedAuthority> authorities;

    public DefaultBasicPrincipal(PlayerOld playerOld){
        this.playerId = playerOld.getPlayerId();
        this.username = playerOld.getUsername();
        this.password = playerOld.getPassword();
        this.authorities = collectAuthorities(playerOld);
    }

     /*
     In this project:
     This class will be used as principal for any Authentication.
        Obs: Only set the authorities required for authorization in secured resources

     This class also will be used for to provide the needed info to create the jwt.
     */

    private Set<? extends GrantedAuthority> collectAuthorities(PlayerOld playerOld){
        Set<GrantedAuthority> grantedAuthorities= new HashSet<>();

        //Player has only one role
        grantedAuthorities.add(new SimpleGrantedAuthority(playerOld.getRole().withPrefix()));

        /*
        Player still don't require other authorities. TODO IF NEEDED:
        for (String authority : player.getAuthorities()) {
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
        this.password = "[*PROTECTED*]";
    }


    //TODO: assert granted authorities are stored in format "ROLE_XXX"

    @Override
    public Role getRole() {
        return this.authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> StringUtils.startsWithIgnoreCase(authority, Role.PREFIX))
                .map(Role::of)
                .findFirst()
                .orElse(null);
    }
}
