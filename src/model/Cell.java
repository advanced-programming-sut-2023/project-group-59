package model;

import java.util.Collection;
import java.util.HashMap;

public class Cell {
    private Vector2D coordinate;
    private CellType type;
    private HashMap<Integer, mapAsset> objects;

    public Cell(Vector2D coordinate, CellType type) {
        this.coordinate = coordinate;
        this.type = type;
    }

    public Collection<mapAsset> getAllObjects() {
        return null;
    }

    public Collection<mapAsset> getPlayersObjects(Player player){
        return null;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public Vector2D getCoordinate() {
        return coordinate;
    }

    public boolean isTraversable(Person person){
        return false;
    }
}