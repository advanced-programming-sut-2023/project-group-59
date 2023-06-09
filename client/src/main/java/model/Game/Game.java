package model.Game;

import controller.GameControllers.GraphicsController;
import model.Map.Map;
import model.MapAsset.MapAsset;
import model.User.Player;
import model.enums.User.Color;
import utils.Pair;
import utils.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private static final Color[] colors = Color.values();
    private final HashMap<Color, Player> players;
    private final boolean isEditableMode;
    private final Map map;
    private Player currentPlayer;
    private int turnCounter;
    private int roundCounter;
    private boolean isNextRound;
    private final ArrayList<Pair> deadPlayers;
    private final String gameID;

    public Game(Map map, HashMap<Color, Player> players, boolean isEditableMode, String gameId ) {
        this.map = map;
        this.isEditableMode = isEditableMode;
        this.players = players;
        this.turnCounter = 0;
        this.roundCounter = 1;
        initializeColors();
        deadPlayers = new ArrayList<>();
        this.gameID = gameId;
    }

    public void nextTurn() {
        isNextRound = false;
        if (turnCounter == map.getPlayerCount()) {
            isNextRound = true;
            turnCounter = 0;
            roundCounter++;
        }
        currentPlayer = players.get(colors[turnCounter]);
        turnCounter++;
    }

    public void setGraphicsController(GraphicsController graphicsController){
        map.setGraphicsController(graphicsController);
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRound() {
        return roundCounter;
    }

    public Map getMap() {
        return map;
    }

    public Enum getGameState() {
        return null;
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public void removePlayer(Player toBeRemoved){
        for (java.util.Map.Entry<Color, Player> colorPlayerEntry : players.entrySet()) {
            if(colorPlayerEntry.getValue().equals(toBeRemoved)){
                players.remove(colorPlayerEntry.getKey());
                return;
            }
        }
    }

    private void initializeColors() {
        for (java.util.Map.Entry<Color, Player> colorPlayerEntry : players.entrySet()) {
            int colorIndex = colorPlayerEntry.getKey().ordinal();
            Vector2D hqCoord = getMap().getHeadQuarterCoordinate(colorIndex);
            MapAsset headQuarter = getMap().getCell(hqCoord).getAllAssets().get(0);
            headQuarter.setOwner(colorPlayerEntry.getValue());
            colorPlayerEntry.getValue().getGovernance().addAsset(headQuarter);
            Vector2D storeCoord = getMap().getStoreHouseCoordinate(colorIndex);
            MapAsset storeHouse = getMap().getCell(storeCoord).getAllAssets().get(0);
            storeHouse.setOwner(colorPlayerEntry.getValue());
            colorPlayerEntry.getValue().getGovernance().addAsset(storeHouse);
        }
    }

    public boolean isEditableMode() {
        return isEditableMode;
    }

    public ArrayList<Pair> getDeadPlayers() {
        return deadPlayers;
    }

    public String getGameID() {
        return gameID;
    }
}
