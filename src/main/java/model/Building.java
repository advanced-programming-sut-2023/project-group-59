package model;

import utils.Vector2D;

public class Building extends MapAsset {
    public Building(Vector2D coordinate, Player owner) {
        super(coordinate, owner);
    }

    @Override
    public void getDamageFrom(MapAsset attacker) {

    }
}