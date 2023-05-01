package view.enums.commands.GameCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum GameMenuCommand {
    PLACE_BUILDING("^\\s*building\\s+placement\\s*$"),
    CHANGE_ENVIRONMENT("^\\s*change\\s+environment\\s*$"),
    TRADE_MENU("^\\s*trade\\s+menu\\s*$"),
    SHOW_MAP("");
    private String regex;
    GameMenuCommand(String regex) {
        this.regex = regex;
    }

    public static GameMenuCommand getCommand(String input){
        for(GameMenuCommand cmd : GameMenuCommand.values()){
            if(input.matches(cmd.regex)) return cmd;
        }
        return null;
    }

    public static Matcher getMatcher(String input, GameMenuCommand cmd){
        Pattern pattern = Pattern.compile(cmd.regex);
        Matcher matcher = pattern.matcher(input);
        return matcher;
    }
}