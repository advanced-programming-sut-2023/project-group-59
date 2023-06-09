package model.Map;

import controller.GameControllers.GraphicsController;
import controller.GameControllers.MoveController;
import model.MapAsset.MapAsset;
import model.MapAsset.MobileUnit.MobileUnit;
import model.User.Player;
import model.enums.AssetType.MapAssetType;
import model.enums.CellType;
import utils.Vector2D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;


@SuppressWarnings("ClassEscapesDefinedScope")
public class Map {
    private transient GraphicsController graphicsController;
    private final String name;
    private final Vector2D size;
    private final Vector<Vector2D> headQuarters;
    private final Vector<Vector2D> storeHouses;
    private int playerCount;
    private Cell[][] map;

    public Map(Vector2D size, String name) {
        this.name = name;
        this.size = size;
        initializeMap();
        playerCount = 0;
        headQuarters = new Vector<>();
        storeHouses = new Vector<>();
    }

    public Vector<Vector2D> getHeadQuarters() {
        return headQuarters;
    }

    public Vector<Vector2D> getStoreHouses() {
        return storeHouses;
    }

    public void setMap(Cell[][] map) {
        this.map = map;
    }

    private void initializeMap() {
        map = new Cell[size.y][size.x];
        for (int y = 0; y < size.y; y++) {
            map[y] = new Cell[size.x];
            for (int x = 0; x < size.x; x++) {
                map[y][x] = new Cell(new Vector2D(x, y), CellType.FIELD);
            }
        }
    }

    public void setGraphicsController(GraphicsController graphicsController) {
        this.graphicsController = graphicsController;
    }

    public String getName() {
        return name;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public Vector2D getSize() {
        return size;
    }

    public void addMapObject(Vector2D coordinate, MapAsset obj) {
        getCell(coordinate).addMapAsset(obj);
        if (obj.getType() == MapAssetType.HEADQUARTER)
            headQuarters.add(obj.getCoordinate());
        if (obj.getType() == MapAssetType.STORE_HOUSE)
            storeHouses.add(obj.getCoordinate());
        graphicsController.updateCellGrid(getCell(coordinate));
    }

    public void removeMapObject(Vector2D coordinate, MapAsset obj) {
        getCell(coordinate).removeMapAsset(obj);
        graphicsController.updateCellGrid(getCell(coordinate));
    }

    //only gets called at the end of the game to prepare Map for saving.
    public void removeNonSavableAssets() {
        for (Cell[] cells : map) {
            for (Cell cell : cells) {
                cell.removeNonSavableAssets();
                graphicsController.updateCellGrid(cell);
            }
        }
    }

    public ArrayList<Cell> getNearbyCells(Vector2D center, int radius) {
        ArrayList<Cell> nearbyCells = new ArrayList<>();
        nearbyCells.add(getCell(center));
        for (int r = 1; r <= radius; r++) {
            for (int j = -r; j <= r; j++) {
                Vector2D cellCoord = new Vector2D(center.x + j, center.y + r);
                if (isInMap(cellCoord))
                    nearbyCells.add(getCell(cellCoord));
                cellCoord = new Vector2D(center.x + j, center.y - r);
                if (isInMap(cellCoord))
                    nearbyCells.add(getCell(cellCoord));
            }
            for (int j = -r + 1; j <= r - 1; j++) {
                Vector2D cellCoord = new Vector2D(center.x + r, center.y + j);
                if (isInMap(cellCoord))
                    nearbyCells.add(getCell(cellCoord));
                cellCoord = new Vector2D(center.x - r, center.y + j);
                if (isInMap(cellCoord))
                    nearbyCells.add(getCell(cellCoord));
            }
        }
        return nearbyCells;
    }

    public ArrayList<Cell> getNeighbors(Vector2D point) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        if (isInMap(new Vector2D(point.x + 1, point.y)))
            neighbors.add(this.getCell(new Vector2D(point.x + 1, point.y)));
        if (isInMap(new Vector2D(point.x - 1, point.y)))
            neighbors.add(this.getCell(new Vector2D(point.x - 1, point.y)));
        if (isInMap(new Vector2D(point.x, point.y + 1)))
            neighbors.add(this.getCell(new Vector2D(point.x, point.y + 1)));
        if (isInMap(new Vector2D(point.x, point.y - 1)))
            neighbors.add(this.getCell(new Vector2D(point.x, point.y - 1)));
        return neighbors;
    }

    public LinkedList<Vector2D> getTraversePath(MobileUnit currentUnit, Vector2D destination) {
        MoveController moveController = new MoveController(this);
        return moveController.findShortestPath(currentUnit, currentUnit.getCoordinate(), destination);
    }

    public boolean isTraversable(MobileUnit mobileUnit, Cell current, Cell destination) {
        if (current.hasWall()) return destination.isTraversableInWall();
        else if (current.hasGateHouse()) return destination.isTraversableInGateHouse();
        return destination.isTraversable(mobileUnit);
    }

    public void changeCellTypeTo(Vector2D coordinate, CellType type) {
        map[coordinate.y][coordinate.x].setType(type);
        graphicsController.updateCellGrid(map[coordinate.y][coordinate.x]);
    }

    public void clearCell(Vector2D coordinate) {
        getCell(coordinate).clear();
        graphicsController.updateCellGrid(getCell(coordinate));
    }

    public Cell getCell(Vector2D coordinate) {
        return map[coordinate.y][coordinate.x];
    }

    public Vector2D getStoreHouseCoordinate(int index) {
        if (storeHouses.size() <= index) return null;
        return storeHouses.get(index);
    }

    public Vector2D getHeadQuarterCoordinate(int index) {
        if (headQuarters.size() <= index) return null;
        return headQuarters.get(index);
    }

    public boolean isInMap(Vector2D coordinate) {
        return coordinate.x >= 0 && coordinate.y >= 0 && coordinate.y < size.y && coordinate.x < size.x;
    }

    public Vector2D[] findCowPatrolPath(Player currentPlayer) {
        Vector2D storeHouseCoord = null;
        Vector2D quarryCoord = null;
        Vector2D currentCoord = new Vector2D(0, 0);
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                currentCoord.x = x;
                currentCoord.y = y;
                if (cellHasAsset(currentCoord, MapAssetType.STORE_HOUSE, currentPlayer, false)) {
                    storeHouseCoord = new Vector2D(currentCoord.x, currentCoord.y);
                    if (quarryCoord != null)
                        return new Vector2D[]{quarryCoord, storeHouseCoord};
                }
                if (cellHasAsset(currentCoord, MapAssetType.QUARRY, currentPlayer, false)) {
                    quarryCoord = new Vector2D(currentCoord.x, currentCoord.y);
                    if (storeHouseCoord != null)
                        return new Vector2D[]{quarryCoord, storeHouseCoord};
                }
            }
        }
        return null;
    }

    public boolean cellHasAsset(Vector2D coordinate, MapAssetType mapAssetType, Player owner, boolean excludeOwner) {
        for (MapAsset asset : getCell(coordinate).getAllAssets()) {
            if (asset.getType() == mapAssetType) {
                if (owner == null)
                    return true;
                if (excludeOwner && !asset.getOwner().equals(owner))
                    return true;
                if (!excludeOwner && asset.getOwner().equals(owner))
                    return true;
            }
        }
        return false;
    }

    public Cell[][] getMap() {
        return map;
    }

    public String getMapId() {
        //TODO fix map ID
        return "1001";
    }
}
