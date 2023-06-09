package model.Map;

import model.MapAsset.Building.Building;
import model.MapAsset.Building.DefenseAndAttackBuilding;
import model.MapAsset.Building.EntranceBuilding;
import model.MapAsset.Building.Wall;
import model.MapAsset.MapAsset;
import model.MapAsset.MobileUnit.MobileUnit;
import model.User.Player;
import model.enums.AssetType.MapAssetType;
import model.enums.CellType;
import model.enums.Direction;
import utils.Vector2D;

import java.io.Serializable;
import java.util.ArrayList;

public class Cell implements Serializable {
    private final Vector2D coordinate;
    private CellType type;
    private ArrayList<MapAsset> assets;
    private int tunnelDestroyCounter; // if -1, no tunnel is there

    public Cell(Vector2D coordinate, CellType type) {
        super();
        this.coordinate = coordinate;
        this.type = type;
        assets = new ArrayList<>();
        tunnelDestroyCounter = -1;
    }

    public void deployTunnel() {
        tunnelDestroyCounter = 4;
    }

    public void tunnelNextRound() {
        if (tunnelDestroyCounter >= 0) tunnelDestroyCounter--;
    }

    public boolean tunnelToBeDestroyed() {
        return tunnelDestroyCounter == 0;
    }

    public boolean hasTunnel() {
        return tunnelDestroyCounter != -1;
    }

    public ArrayList<MapAsset> getAllAssets() {
        return assets;
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

    //only gets called at the end of the game to prepare Map for saving.
    public void removeNonSavableAssets() {
        ArrayList<MapAsset> newObjects = new ArrayList<>();
        for (MapAsset asset : assets) {
            MapAssetType type = asset.getType();
            if (type == MapAssetType.TREE || type == MapAssetType.CLIFF || type == MapAssetType.HEADQUARTER) {
                asset.setOwner(null);
                newObjects.add(asset);
            }
        }
        assets = newObjects;
    }

    public boolean isTraversable(MobileUnit mobileUnit) {
        //TODO @kian handle siege tower
        if (!CellType.isTraversableByType(type)) return false;
        for (MapAsset mapAsset : assets) {
            if (mapAsset instanceof Building) {
                if (mapAsset.getType().equals(MapAssetType.KILLING_PIT)) continue;
                if (mapAsset instanceof EntranceBuilding) return ((EntranceBuilding) mapAsset).isOpen();
                if (mapAsset instanceof Wall) return hasLadder((Wall) mapAsset);
                if (mapAsset.getType().equals(MapAssetType.QUARRY) || mapAsset.getType().equals(MapAssetType.STORE_HOUSE))
                    return mobileUnit.getType().equals(MapAssetType.COW);
                return (mapAsset.getType().equals(MapAssetType.STAIRS));
            }
        }
        return true;
    }

    private boolean hasLadder(Wall wall) {
        return wall.hasLadder(Direction.EAST) || wall.hasLadder(Direction.WEST) || wall.hasLadder(Direction.NORTH) ||
                wall.hasLadder(Direction.SOUTH);
    }

    public boolean isTraversableInWall() {
        return hasWall();
    }

    public boolean isTraversableInGateHouse() {
        if (!CellType.isTraversableByType(type)) return false;
        return hasWall();
    }

    public void clear() {
        assets.clear();
    }

    public void addMapAsset(MapAsset obj) {
        assets.add(obj);
    }

    public void removeMapAsset(MapAsset obj) {
        assets.remove(obj);
    }

    public boolean isThereUnit() {
        for (MapAsset mapAsset : assets) {
            if (mapAsset instanceof MobileUnit) return true;
        }
        return false;
    }

    public boolean isThereUnit(Player player) {
        for (MapAsset mapAsset : assets) {
            if (mapAsset instanceof MobileUnit && !mapAsset.getOwner().equals(player)) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return assets.isEmpty();
    }

    public boolean containsType(MapAssetType type) {
        for (MapAsset asset : assets) {
            if (asset.getType().equals(type)) return true;
        }
        return false;
    }

    public boolean hasWall() {
        for (MapAsset mapAsset : this.assets) {
            if (mapAsset instanceof DefenseAndAttackBuilding) return true;
        }
        return false;
    }

    public boolean hasGateHouse() {
        for (MapAsset mapAsset : this.assets) {
            if (mapAsset instanceof EntranceBuilding) return true;
            if (mapAsset.getType().equals(MapAssetType.STAIRS)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Cell type: " + type.getName());

        for (MapAsset asset : assets) {
            output.append('\n').append(asset.toString());
        }
        return output.toString();
    }

}
