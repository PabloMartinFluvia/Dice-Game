

    //I WANT AN STATIC FINAL FIELD, WITH THE VALUE INJECTED

    /*
    STEP 1:
    Inject based by setter (private). Injection it's NOT SUPPORTED ON STATIC METHODS.

    A way to inject in a static field (it works)
    The problem is that it's not final (can be modified later)


    STEP 2:
    Final fields can only be injected by constructor, so an instance
    is needed, -> that's not what I want (I don't want to create instances)

    A) make the static field private
    B) make a public static "getter" for that field
    C) Meanwhile the value is returned "by value" (and not "by reference")
    and there's no "setter"
    -> the value of the static field can't be modified -> final field in practice
    -> normally the injected value is a String or number -> values are returned "by value"

    -> When calling the public static getter -> the value is loaded.

    Problem:
    If I want to save this value in a field of other class:

    Ex (in another clas):

    public final static String DEFAULT_USERNAME = DiceGameContext.getDefaultUsername();
        *This gets null, Spring or Java first "reads" static fields, and when is trying
        to read the static method this value is still null, due injection is
         done after (firsts loads values for static/class, then injects/loads values for objects)

    public final String DEFAULT_USERNAME = DiceGameContext.getDefaultUsername();
        *this works: due is loading a value for an object (and injection is now already done).

    So:
    If I want a static access:
        don't save this value anywhere else, and request the value to this class
         when needed (calling the static method)
        There's no way to save the value in static fields of others classes, due this
         one still will have a null value
    If I want an instance access:
        I can save this value in an instance field (can be final) of other classes.

    Or I can declare the other(s) classes as @Component and inject the value to a static field
        (like in this class),
        *** But remember that a bean is a singleton / prototype, new instances only share
        static fields (won't share injections done in the instance bean)
     */




package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:values.properties")
@Log4j2
public class DiceGamePathsContext {


    private static String DEFAULT_USERNAME;

    @Value("${player.username.default}")
    private void setDefaultUsername(String defaultUsername) {
        DEFAULT_USERNAME = defaultUsername;
    }

    private static int WON_VALUE;

    @Value("${game.won_value}")
    public void setWonValue(int wonValue) {
        WON_VALUE = wonValue;
    }


    public static String getDefaultUsername(){
        return DEFAULT_USERNAME;
    }

    public static int getWinValue() {
        return WON_VALUE;
    }

    //////////////////////////PATHS

    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    public static final String LOGOUT_ANY = LOGOUT+"/**";
    public static final String LOGOUT_ALL = LOGOUT+"/all";
    public static final String JWTS = "/jwts";

    public static final String JWTS_ANY = JWTS+"/**";

    public static final String JWTS_ACCESS = JWTS+"/access";

    public static final String JWTS_RESET = JWTS+"/reset";


    public static final String PLAYERS = "/players";

    public static final String PLAYERS_ANY = PLAYERS+"/**";

    public static final String PLAYERS_REGISTER = PLAYERS+"/register-anonymous";
    public static final String PLAYERS_CONCRETE = PLAYERS+"/{id}";

    public static final String PLAYERS_CONCRETE_ROLLS = PLAYERS_CONCRETE +"/games";
    public static final String ADMINS = "/admins";
    public static final String ADMINS_PLAYERS_CONCRETE = ADMINS+ PLAYERS_CONCRETE;

    public static final String ADMINS_ANY = ADMINS+"/**";

    public static final String RANKING = "/ranking";

    public static final String PLAYERS_RANKING = PLAYERS+RANKING;

    public static final String PLAYERS_CONCRETE_RANKING = PLAYERS_CONCRETE+RANKING;

}
