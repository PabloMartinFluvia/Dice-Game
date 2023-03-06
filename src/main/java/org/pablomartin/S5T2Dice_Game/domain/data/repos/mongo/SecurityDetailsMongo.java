package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.Objects;

@Getter
@Setter
@ToString
public class SecurityDetailsMongo {

    private String password;

    private Role role;

    //one unique all args constructor, visible only at package level
    SecurityDetailsMongo(String password, Role role) {
        this.password = password;
        this.role = role;
    }

    public static SecurityDetailsMongo of (@NotNull NewPlayerInfo credentials){
        return new SecurityDetailsMongo(credentials.getPasswordEncoded(),credentials.getRole());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityDetailsMongo that = (SecurityDetailsMongo) o;
        return Objects.equals(getPassword(), that.getPassword()) && getRole() == that.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPassword(), getRole());
    }
}