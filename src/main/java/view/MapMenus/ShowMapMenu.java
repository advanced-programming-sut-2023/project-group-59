package view.MapMenus;

import controller.MapControllers.ShowMapController;
import utils.Vector2D;
import view.Menu;
import view.enums.commands.ShowMapCommand;
import view.enums.messages.ProfileMessage;
import view.enums.messages.ShowMapMessage;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowMapMenu {

    private final ShowMapController showMapController;
    private final Scanner scanner;

    public ShowMapMenu(ShowMapController showMapController) {
        this.showMapController = showMapController;
        this.scanner = Menu.getScanner();
    }

    public String run() {
        System.out.println(showMapController.printMap());
        String input;
        while (true) {
            input = scanner.nextLine();
            ShowMapCommand cmd = ShowMapCommand.getCommand(input);
            if (cmd == null) {
                ShowMapMessage.printMessage(ShowMapMessage.INVALID_COMMAND);
                continue;
            }
            Matcher matcher = ShowMapCommand.getMatcher(input, cmd);
            switch (cmd) {
                case MOVE_MAP:
                    runMoveMap(matcher);
                    break;
                case EXIT:
                    return "exit";
                default:
                    ProfileMessage.printMessage(ProfileMessage.INVALID_COMMAND);
                    break;
            }
        }
    }

    private void runMoveMap(Matcher matcher) {
        String details = matcher.group("details");
        Vector2D moveOffset = getMoveOffset(details);
        ShowMapMessage message = showMapController.moveMap(moveOffset);
        if(message == ShowMapMessage.MOVE_SUCCESSFUL)
            System.out.println(showMapController.printMap());
        else
            System.out.println(message.getMessage());
    }

    private static Vector2D getMoveOffset(String details) {
        String directionRegex = "(\\s+(?<num>\\d+))?";
        Vector2D moveVector = new Vector2D(0, 0);
        Matcher directionMatcher = Pattern.compile("up" + directionRegex).matcher(details);
        if (directionMatcher.find()) {
            String moveAmountStr = directionMatcher.group("num");
            int moveAmount = 1;
            if (moveAmountStr != null)
                moveAmount = Integer.parseInt(moveAmountStr);
            moveVector.y -= moveAmount;
        }
        directionMatcher = Pattern.compile("down" + directionRegex).matcher(details);
        if (directionMatcher.find()) {
            String moveAmountStr = directionMatcher.group("num");
            int moveAmount = 1;
            if (moveAmountStr != null)
                moveAmount = Integer.parseInt(moveAmountStr);
            moveVector.y += moveAmount;
        }
        directionMatcher = Pattern.compile("left" + directionRegex).matcher(details);
        if (directionMatcher.find()) {
            String moveAmountStr = directionMatcher.group("num");
            int moveAmount = 1;
            if (moveAmountStr != null)
                moveAmount = Integer.parseInt(moveAmountStr);
            moveVector.x -= moveAmount;
        }
        directionMatcher = Pattern.compile("right" + directionRegex).matcher(details);
        if (directionMatcher.find()) {
            String moveAmountStr = directionMatcher.group("num");
            int moveAmount = 1;
            if (moveAmountStr != null)
                moveAmount = Integer.parseInt(moveAmountStr);
            moveVector.x += moveAmount;
        }
        return moveVector;
    }

}