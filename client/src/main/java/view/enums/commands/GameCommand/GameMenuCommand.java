package view.enums.commands.GameCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum GameMenuCommand {
    SELECT_UNIT("\\s*select\\s+unit\\s+-x\\s+(?<x>\\d+)\\s+-y\\s+(?<y>\\d+)\\s*"),
    SELECT_BUILDING("\\s*select\\s+building\\s+-x\\s+(?<x>\\d+)\\s+-y\\s+(?<y>\\d+)\\s*"),
    SHOW_POPULARITY_FACTORS("\\s*show\\s+popularity\\s+factors\\s*"),
    SHOW_POPULARITY("\\s*show\\s+popularity\\s*"),
    SHOW_FOOD_RATE("\\s*food\\s+rate\\s+show\\s*"),
    SHOW_FOOD_LIST("\\s*show\\s+food\\s+list\\s*"),
    SHOW_TAX_RATE("\\s*tax\\s+rate\\s+show\\s*"),
    SET_FEAR_RATE("\\s*fear\\s+rate\\s+-r\\s+(?<rate>-?\\d+)\\s*"),
    PLACE_BUILDING("\\s*building\\s+placement\\s*"),
    CHANGE_ENVIRONMENT("\\s*change\\s+environment\\s*"),
    TRADE_MENU("\\s*trade\\s+menu\\s*"),
    SHOW_MAP("\\s*show\\s+map\\s+-x\\s+(?<x>\\d+)\\s+-y\\s+(?<y>\\d+)\\s*"),
    MARKET_MENU("\\s*market\\s+menu\\s*"),
    NEXT_TURN("\\s*next\\s+turn\\s*"),
    SHOW_GAME_INFO("\\s*show\\s+game\\s+info\\s*");
    private final String regex;

    GameMenuCommand(String regex) {
        this.regex = regex;
    }

    public static GameMenuCommand getCommand(String input) {
        for (GameMenuCommand cmd : GameMenuCommand.values()) {
            if (input.matches(cmd.regex)) return cmd;
        }
        return null;
    }

    public static Matcher getMatcher(String input, GameMenuCommand cmd) {
        Pattern pattern = Pattern.compile(cmd.regex);
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        return matcher;
    }
}
