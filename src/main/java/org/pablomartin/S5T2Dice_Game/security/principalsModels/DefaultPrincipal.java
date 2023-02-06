package org.pablomartin.S5T2Dice_Game.security.principalsModels;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class DefaultPrincipal implements PlayerCredentials, BasicPrincipal, TokenPrincipal, RefreshTokenPrincipal{

    private UUID userId;

    private String username;

    private String password;

    private Set<? extends GrantedAuthority> authorities;

    private UUID refreshTokenId;


    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Role getUserRole() {
        return this.authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> StringUtils.startsWithIgnoreCase(authority, Role.PREFIX))
                .map(Role::of)
                .findFirst()//in this project user only has one role
                .orElse(null);
    }


    @Override
    public UUID getRefreshTokenId() {
        return refreshTokenId;
    }

    @Override
    public BasicPrincipal toBasicPrincipal() {
        return this;
    }

    @Override
    public TokenPrincipal toAccessTokenPrincipal() {
        return this;
    }

    @Override
    public RefreshTokenPrincipal toRefreshTokenPrincipal(UUID refreshTokenId) {
        this.refreshTokenId = refreshTokenId;
        return this;
    }

    @Override
    public void eraseCredentials() {
        password = "[*PROTECTED*]";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
