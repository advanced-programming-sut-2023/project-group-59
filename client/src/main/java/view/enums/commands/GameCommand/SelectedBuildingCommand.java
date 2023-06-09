package view.enums.commands.GameCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SelectedBuildingCommand {
    REPAIR("\\s*repair\\s*"),
    INFO("\\s*info\\s*"),
    Change_PRODUCTION("\\s*change\\s+production\\s*"),
    CREATE_UNIT("\\s*createunit((\\s+-t\\s+(?<type>\\S+))|(\\s+-c\\s+(?<count>\\S+)))*"),
    CHANGE_ENTRANCE("\\s*change\\s+entrance\\s+gate"),
    SET_FOOD_RATE("\\s*food\\s+rate\\s+-r\\s+(?<rate>-?\\d+)\\s*"),
    SET_TAX_RATE("\\s*tax\\s+rate\\s+-r\\s+(?<rate>-?\\d+)\\s*"),

    BACK("\\s*back\\s*"),

    DELETE("\\s*delete\\s*");

    private final String regex;

    SelectedBuildingCommand(String regex) {
        this.regex = regex;
    }

    public static SelectedBuildingCommand getCommand(String input) {
        for (SelectedBuildingCommand cmd : SelectedBuildingCommand.values()) {
            if (input.matches(cmd.regex)) return cmd;
        }
        return null;
    }

    public static Matcher getMatcher(String command, SelectedBuildingCommand sampleCommand) {
        Matcher matcher = Pattern.compile(sampleCommand.regex).matcher(command);
        return matcher.matches() ? matcher : null;
    }

    public String getRegex() {
        return regex;
    }
}
