package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.Objects;

@Embeddable
@Getter
@Setter // recommended for jpa
@ToString
public class SecurityDetailsSql {

    @Nullable
    private String password;

    @Enumerated(EnumType.STRING)
    @Nonnull
    private Role role;

    SecurityDetailsSql() {
        /*
        no args constructor, limited to package visibility
        due jpa specification
         */
    }

    public static SecurityDetailsSql of(@NotNull NewPlayerInfo credentials){
        SecurityDetailsSql details = new SecurityDetailsSql();
        details.password = credentials.getPasswordEncoded();
        details.role = credentials.getRole();
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDetailsSql that = (SecurityDetailsSql) o;
        return Objects.equals(getPassword(), that.getPassword()) && getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassword(), getRole());
    }
}
