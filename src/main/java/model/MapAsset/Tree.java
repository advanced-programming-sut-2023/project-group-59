package model.MapAsset;

import model.Player;
import model.enums.TreeType;
import utils.Vector2D;

public class Tree extends MapAsset {
    public Tree(Vector2D coordinate, Player owner, TreeType type) {
        super(coordinate, owner, null);
    }

    @Override
    public void getDamageFrom(MapAsset attacker) {

    }
}