package view;

import controller.GameControllers.GameController;
import controller.GameControllers.GraphicsController;
import controller.MapControllers.BuildingPlacementController;
import controller.UserControllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import model.ConstantManager;
import model.Game.Game;
import model.Map.Map;
import model.Map.MapManager;
import model.Stronghold;
import model.User.Player;
import model.enums.User.Color;
import view.GameMenus.GraphicGameMenu;
import view.MapMenus.dropBuildingMenu.GraphicBuildingPlacementMenu;
import view.UserMenus.LoginMenu;

import java.util.HashMap;

public class Main extends Application {
    public static Stage mainStage;
    public static void main(String[] args) throws Exception {
        Stronghold.load();
        ConstantManager.load();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        LoginMenu.setLoginController(new LoginController());
        mainStage = stage;
        new LoginMenu().start(stage);
    }

//    @Override
//    public void start(Stage stage) throws Exception {
//        HashMap<Color, Player> players = new HashMap<>();
//        players.put(Color.RED, new Player(Stronghold.getInstance().getUser("ayeen")));
//        players.put(Color.BLUE, new Player(Stronghold.getInstance().getUser("kian")));
//        Map map = MapManager.load("1001");
//        Game game = new Game(map, players, true);
//        GameController gameController = new GameController(Stronghold.getInstance().getUser("ayeen"), game);
//        GraphicGameMenu.setGameController(gameController);
//        GraphicGameMenu graphicGameMenu = new GraphicGameMenu();
//        GraphicGameMenu.setGraphicGameMenu(graphicGameMenu);
//        GraphicGameMenu.setGraphicsController(new GraphicsController(gameController, game));
//        GraphicBuildingPlacementMenu.setController(new BuildingPlacementController(game.getCurrentPlayer(), map, true));
//        mainStage = stage;
//        graphicGameMenu.start(stage);
//    }
}
