package view.UserMenus;

import controller.UserControllers.MainController;
import view.Menu;
import view.enums.commands.UserCommand.MainMenuCommand;
import view.enums.messages.UserCommand.MainMenuMessage;

import java.util.Scanner;

public class MainMenu {
    private MainController mainController;
    private Scanner scanner;
    public MainMenu(MainController mainController){
        this.mainController = mainController;
        this.scanner = Menu.getScanner();
    }

    public String run(){
        String input;
        while(true){
            input = scanner.nextLine();
            MainMenuCommand cmd = MainMenuCommand.getCommand(input);
            if(cmd == null){
                MainMenuMessage.printMessage(MainMenuMessage.INVALID_COMMAND);
                continue;
            }
            switch (cmd){
                case PROFILE_MENU:
                    return "profileMenu";
                case GAME_MAKER_MENU:
                    return "newGame";
                case BACK:
                    return "logout";
            }
        }
    }
}
