package org.pablomartin.S5T2Dice_Game.security.principalsModels;

import lombok.Builder;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.old.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.old.RefreshTokenPrincipal;
import org.pablomartin.S5T2Dice_Game.security.old.TokenPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Builder
@ToString
public class DefaultPrincipal implements PrincipalProvider, BasicPrincipal, TokenPrincipal, RefreshTokenPrincipal {

    private UUID userId;

    private String username;

    private String password;

    private Set<? extends GrantedAuthority> authorities;
    //private Role role;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPrincipal that = (DefaultPrincipal) o;
        return getUserId().equals(that.getUserId()) && getUsername().equals(that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && getAuthorities().equals(that.getAuthorities()) && Objects.equals(getRefreshTokenId(), that.getRefreshTokenId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getUsername(), getPassword(), getAuthorities(), getRefreshTokenId());
    }
}
