package view.enums.messages.GameMessage;

public enum GameMenuMessage {
    INVALID_COMMAND("Invalid command"),
    INVALID_COORDINATE("Invalid coordinate"),
    NO_UNITS_IN_PLACE("No units owned by you is in this place"),
    UNIT_SELECTED("Entered unit menu successfully"),
    NO_BUILDING_IN_PLACE("No building is in this place"),
    BUILDING_SELECTED("Entered selected building menu successfully"),
    WRONG_OWNER("You don't own the selected building"),
    ENTER_SHOW_MAP("Entered show map menu successfully"),
    INVALID_FEAR_RATE("Invalid fear rate"),
    FEAR_RATE_CHANGE_SUCCESS("Successfully changed fear rate"),
    INVALID_TAX_RATE("Invalid tax rate"),
    TAX_RATE_CHANGE_SUCCESS("Successfully changed tax rate"),
    CHANGE_ENVIRONMENT("Entered change environment menu successfully"),
    BUILDING_PLACEMENT("Entered building placement menu successfully"),
    TRADE("Entered trade menu successfully"),
    ENTER_MARKET("Entered market menu successfully"),
    NOT_MODIFIABLE("Map is not modifiable");
    private final String message;

    GameMenuMessage(String message) {
        this.message = message;
    }

    public static void printMessage(GameMenuMessage msg) {
        System.out.println(msg.message);
    }

    public String getMessage() {
        return message;
    }
}
