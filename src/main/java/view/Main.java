package view;

import controller.GameControllers.GameController;
import controller.GameControllers.GraphicsController;
import controller.MapControllers.BuildingPlacementController;
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

import java.util.HashMap;

public class Main extends Application {
    public static Stage mainStage;
    public static void main(String[] args) throws Exception {
        Stronghold.load();
        ConstantManager.load();
        launch(args);
    }

//    @Override
//    public void start(Stage stage) throws Exception {
//        LoginMenu.setLoginController(new LoginController());
//        new LoginMenu().start(stage);
//    }

    @Override
    public void start(Stage stage) throws Exception {
        HashMap<Color, Player> players = new HashMap<>();
        players.put(Color.RED, new Player(Stronghold.getInstance().getUser("ayeen")));
        players.put(Color.BLUE, new Player(Stronghold.getInstance().getUser("kian")));
        Map map = MapManager.load("1001");
        Game game = new Game(map, players, true);
        GraphicGameMenu.setGameController(new GameController(Stronghold.getInstance().getUser("ayeen"), game));
        GraphicGameMenu.setGraphicsController(new GraphicsController(game));
        GraphicBuildingPlacementMenu.setController(new BuildingPlacementController(game.getCurrentPlayer(), map));
        new GraphicGameMenu().start(stage);
    }
}