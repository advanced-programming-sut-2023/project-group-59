package controller.GameControllers;

import utils.Pair;
import view.GameMenus.EndGameMenu;

import java.util.ArrayList;

public class EndGameController {
    ArrayList<Pair> players;

    public EndGameController(ArrayList<Pair> players) {
        this.players = players;
    }

    public void run() {
        sortPlayers();
        System.out.println(showPlayers());
        EndGameMenu endGameMenu = new EndGameMenu(this);
        endGameMenu.run();
    }

    public String showInfo(String username) {
        Pair pair = getPlayerByName(username);
        if (pair == null) return "player does not exist";
        return pair.z;
    }

    private void sortPlayers() {
        players.sort(((o1, o2) -> Integer.parseInt(o2.y) - Integer.parseInt(o1.y)));
    }

    private Pair getPlayerByName(String name) {
        for (Pair pair : players) {
            if (pair.x.equals(name)) return pair;
        }
        return null;
    }

    private String showPlayers() {
        StringBuilder result = new StringBuilder();
        result.append("winner: ").append(players.get(0).x).append("\n");
        int index = 1;
        for (Pair player : players) {
            result.append(index).append(".").append(player.x).append("\t\t").append(player.y).append("\n");
            index++;
        }
        return result.toString();
    }
}