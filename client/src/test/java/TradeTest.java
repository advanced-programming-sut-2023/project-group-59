//import controller.GameControllers.TradeController;
//import model.Game.Game;
//import model.Map.Map;
//import model.Map.MapManager;
//import model.Stronghold;
//import model.User.Player;
//import model.User.User;
//import model.enums.User.Color;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import utils.SignupAndLoginUtils;
//import view.enums.commands.TradeMenuCommand;
//import view.enums.messages.TradeMenuMessage;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.regex.Matcher;
//
//public class TradeTest {
//    private static Player player1, player2, player3;
//    private static Game game;
//    private static TradeController tradeController;
//
//
//    @BeforeAll
//    public static void beforeAll() {
//        File users = new File("Resources/users.user");
//        users.delete();
//        User user1, user2, user3;
//        user1 = new User("kian1", "K123456!A@n",
//                "kianghassemi1@gmail.com", "kkk", "someSlogan");
//        user2 = new User("kian2", "K123456!A@n",
//                "kianghassemi2@gmail.com", "kkk2", "someSlogan");
//        user3 = new User("kian3", "K123456!A@n",
//                "kianghassemi3@gmail.com", "kkk3", "someSlogan");
//        Stronghold.load();
//        Stronghold.getInstance().addUser(user1);
//        Stronghold.getInstance().addUser(user2);
//        Stronghold.getInstance().addUser(user3);
//        Stronghold.getInstance().updateData();
//        player1 = new Player(user1);
//        player2 = new Player(user2);
//        player3 = new Player(user3);
//        HashMap<Color, Player> players = new HashMap<>();
//        players.put(Color.getColorWithSizeCheck("RED"), player1);
//        players.put(Color.getColorWithSizeCheck("BLUE"), player2);
//        players.put(Color.getColorWithSizeCheck("GREEN"), player3);
//        Map selectedMap = MapManager.load("1002");
//        game = new Game(selectedMap, players, false);
//        game.setCurrentPlayer(player1);
//        tradeController = new TradeController(game);
//    }
//
//
//    @Test
//    public void requestTest() {
//        HashMap<String, String> inputs;
//        Matcher matcher;
//        String requestCommand_emptyField = "trade -t stone -a 10 -p 100 ";
//        String requestCommand_invalidMaterial = "trade -t water -a 10 -p 100 -m \"give me\" ";
//        String requestCommand_invalidAmount = "trade -t stone -a 10 -p -100 -m \"give me\" ";
//        String requestCommand_correct = "trade -t stone -a 10 -p 2000 -m \"give me\" ";
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_emptyField, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.EMPTY_FIELD);
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_invalidMaterial, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.INVALID_MATERIAL);
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_invalidAmount, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.INVALID_AMOUNT);
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_correct, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.GOLD_NEEDED);
//
//        game.setCurrentPlayer(player1);
//        player1.getGovernance().changeGold(1005);
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.REQUEST_SUCCESS);
//        game.setCurrentPlayer(player2);
//        Assertions.assertNotEquals(tradeController.showNewTradesForPlayer(), null);
//    }
//
//    @Test
//    public void showTradesTest() {
//        HashMap<String, String> inputs;
//        Matcher matcher;
//
//        String requestCommand_correct1 = "trade -t stone -a 10 -p 50 -m \"give me\" ";
//        String requestCommand_correct2 = "trade -t wood -a 30 -p 720 -m \"give me\" ";
//        String requestCommand_correct3 = "trade -t Iron -a 2 -p 200 -m \"give me\" ";
//
//        Assertions.assertEquals("There are not trades!", tradeController.showAllTrades());
//        Assertions.assertEquals("you don't have any trades yet!", tradeController.tradeHistory());
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_correct1, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        tradeController.request(inputs);
//
//        matcher = TradeMenuCommand.getMatcher(requestCommand_correct2, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        tradeController.request(inputs);
//
//        game.setCurrentPlayer(player3);
//        // tradeMenu.run();
//        matcher = TradeMenuCommand.getMatcher(requestCommand_correct3, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//
//        Assertions.assertEquals(tradeController.request(inputs), TradeMenuMessage.REQUEST_SUCCESS);
//        Assertions.assertNotEquals("you don't have any trades yet!", tradeController.tradeHistory());
//
//        Assertions.assertEquals(3, TradeController.trades.size());
//        Assertions.assertEquals(2, player3.getNewTrades().size());
//        Assertions.assertEquals(3, player2.getNewTrades().size());
//        Assertions.assertEquals(1, player1.getNewTrades().size());
//    }
//
//
//    @Test
//    public void acceptTradeTest() {
//        HashMap<String, String> inputs;
//        Matcher matcher;
//        game.setCurrentPlayer(player1);
//        String requestCommand_correct1 = "trade -t stone -a 102 -p 50 -m \"give me\" ";
//
//        //Add a trade with id=4
//        matcher = TradeMenuCommand.getMatcher(requestCommand_correct1, TradeMenuCommand.REQUEST);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.REQUEST.getRegex());
//        tradeController.request(inputs);
//
//        game.setCurrentPlayer(player2);
//
//        String acceptTradeCommand_invalidId = "trade accept -i 5      -m thankYou";
//        matcher = TradeMenuCommand.getMatcher(acceptTradeCommand_invalidId, TradeMenuCommand.ACCEPT_TRADE);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.ACCEPT_TRADE.getRegex());
//        Assertions.assertEquals(TradeMenuMessage.INVALID_ID, tradeController.accept_trade(inputs));
//
//        String acceptTradeCommand_correctCommand = "trade accept -i 4 -m thankYou";
//
//        matcher = TradeMenuCommand.getMatcher(acceptTradeCommand_correctCommand, TradeMenuCommand.ACCEPT_TRADE);
//        inputs = SignupAndLoginUtils.getInputs(matcher, TradeMenuCommand.ACCEPT_TRADE.getRegex());
//        Assertions.assertEquals(TradeMenuMessage.MATERIAL_NEEDED, tradeController.accept_trade(inputs));
//        //   StorageBuilding building = new StorageBuilding((StorageBuilding) ConstantManager.getInstance().getAsset(MapAssetType.STORE_HOUSE),new Vector2D(25,25), player2);
//        //   player2.getGovernance().addAsset(building);
//        //    player2.getGovernance().changeStorageStock(Material.STONE, 3);
//
//        // TODO handle storage completely
//
//        //     Assertions.assertEquals(TradeMenuMessage.ACCEPTED, tradeController.accept_trade(inputs));
//        //   Assertions.assertEquals(TradeMenuMessage.ALREADY_ACCEPTED, tradeController.accept_trade(inputs));
//    }
//}
