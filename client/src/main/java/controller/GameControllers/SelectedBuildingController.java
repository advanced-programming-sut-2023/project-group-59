package controller.GameControllers;

import model.ConstantManager;
import model.Game.Game;
import model.Map.Cell;
import model.Map.Map;
import model.MapAsset.Building.Building;
import model.MapAsset.Building.EntranceBuilding;
import model.MapAsset.Building.ProductionBuilding;
import model.MapAsset.Building.TrainingAndEmploymentBuilding;
import model.MapAsset.MapAsset;
import model.MapAsset.MobileUnit.AttackingUnit;
import model.MapAsset.MobileUnit.MobileUnit;
import model.User.Player;
import model.enums.AssetType.MapAssetType;
import model.enums.AssetType.Material;
import utils.Vector2D;
import view.enums.messages.GameMessage.SelectedBuildingMessage;

import java.util.ArrayList;

public class SelectedBuildingController {
    private final Building building;
    private final Player player;
    private final Map map;
    private final Vector2D coordinate;
    private static boolean isModifiable;

    public SelectedBuildingController(Building building, Game game) {
        this.building = building;
        this.player = game.getCurrentPlayer();
        this.map = game.getMap();
        coordinate = building.getCoordinate();
    }

    public Building getBuilding() {
        return building;
    }

    public SelectedBuildingMessage repair() {
        if (building.getHitPoint() == building.getMaxHitPoint()) return SelectedBuildingMessage.HP_FULL;
        if (player.getGovernance().getStorageStock(building.getNeededMaterial()) < materialNeededForRepair())
            return SelectedBuildingMessage.MATERIAL_NEEDED;
        if (isThereEnemy(1))
            return SelectedBuildingMessage.ENEMY_EXIST;
        player.getGovernance().changeStorageStock(building.getNeededMaterial(), -1 * materialNeededForRepair());
        building.repair();
        return SelectedBuildingMessage.SUCCESS_REPAIR;
    }

    public String showInfo() {
        return building.toString();
    }

    public SelectedBuildingMessage deleteBuilding() {
        if (map.getCell(coordinate).isThereUnit())
            return SelectedBuildingMessage.NOT_ALLOWED_TO_DELETE;
        map.removeMapObject(coordinate, building);
        player.getGovernance().removeAsset(building);
        return SelectedBuildingMessage.DELETED_BUILDING;
    }

    public SelectedBuildingMessage changeProductionMode() {
        if (!(building instanceof ProductionBuilding))
            return SelectedBuildingMessage.INVALID_COMMAND_FOR_BUILDING;

        ((ProductionBuilding) building).changeProductionMode();
        if (((ProductionBuilding) building).getProductionMode())
            return SelectedBuildingMessage.RESUME_PRODUCTION;
        else return SelectedBuildingMessage.STOP_PRODUCTION;
    }

    public SelectedBuildingMessage createUnit(MapAssetType type) {
        MobileUnit sampleMobileUnit = (MobileUnit) ConstantManager.getInstance().getAsset(type);

        AttackingUnit sampleAttackingUnit = null;
        if (sampleMobileUnit instanceof AttackingUnit)
            sampleAttackingUnit = (AttackingUnit) sampleMobileUnit;
        if (!isModifiable) {
            if (sampleAttackingUnit != null && !isWeaponEnough(sampleAttackingUnit, 1))
                return SelectedBuildingMessage.WEAPON_NEEDED;
            if (!isGoldEnough(sampleMobileUnit.getCost(), 1))
                return SelectedBuildingMessage.GOLD_NEEDED;

            if (sampleMobileUnit.getEngineersCount() != 0) {
                int numberOfEngineers = player.getGovernance().getEngineers();
                if (numberOfEngineers < sampleMobileUnit.getEngineersCount())
                    return SelectedBuildingMessage.ENGINEER_NEEDED;
                else addEngineersToSiegeTent(sampleMobileUnit.getEngineersCount());
            }
            player.getGovernance().changeGold(-1 * sampleMobileUnit.getCost());
        }
        if (sampleAttackingUnit != null) {
            if (sampleAttackingUnit.getWeapons() != null) {
                for (Material weapon : sampleAttackingUnit.getWeapons())
                    if (!isModifiable) player.getGovernance().changeStorageStock(weapon, -1);
            }
        }
        MobileUnit mobileUnit;
        if (sampleAttackingUnit == null)
            mobileUnit = new MobileUnit(sampleMobileUnit, new Vector2D(coordinate.x, coordinate.y), player);
        else
            mobileUnit = new AttackingUnit(sampleAttackingUnit, new Vector2D(coordinate.x, coordinate.y), player);
        map.getCell(mobileUnit.getCoordinate()).addMapAsset(mobileUnit);
        player.getGovernance().addAsset(mobileUnit);

        return SelectedBuildingMessage.SUCCESS_CREATING_UNIT;
    }

    public SelectedBuildingMessage changeGate() {
        if (building instanceof EntranceBuilding) {
            if (isThereEnemy(0)) return SelectedBuildingMessage.ENEMY_IN_GATE;
            if (((EntranceBuilding) building).isOpen())
                ((EntranceBuilding) building).close();
            else ((EntranceBuilding) building).open();
            if (((EntranceBuilding) building).isOpen()) return SelectedBuildingMessage.SUCCESS_OPEN;
            return SelectedBuildingMessage.SUCCESS_CLOSE;
        }
        return SelectedBuildingMessage.INVALID_COMMAND_FOR_BUILDING;
    }

    public SelectedBuildingMessage setFoodRate(int foodRate) {
        if (!building.getType().equals(MapAssetType.FOOD_STORAGE))
            return SelectedBuildingMessage.INVALID_COMMAND_FOR_BUILDING;

        if (foodRate > 2 || foodRate < -2)
            return SelectedBuildingMessage.INVALID_FOOD_RATE;
        player.getGovernance().setFoodRate(foodRate);
        return SelectedBuildingMessage.FOOD_RATE_CHANGE_SUCCESS;
    }

    private void addEngineersToSiegeTent(int x) {
        for (MapAsset mapAsset : player.getGovernance().getUnits()) {
            if (mapAsset.getType().equals(MapAssetType.ENGINEER)) {
                x--;
                player.getGovernance().removeAsset(mapAsset);
                map.getCell(mapAsset.getCoordinate()).removeMapAsset(mapAsset);
                if (x == 0) return;
            }
        }
    }

    public SelectedBuildingMessage setTaxRate(int taxRate) {
        if (!building.getType().equals(MapAssetType.HEADQUARTER))
            return SelectedBuildingMessage.INVALID_COMMAND_FOR_BUILDING;
        if (taxRate > 8 || taxRate < -3)
            return SelectedBuildingMessage.INVALID_TAX_RATE;
        player.getGovernance().setTaxRate(taxRate);
        return SelectedBuildingMessage.TAX_RATE_CHANGE_SUCCESS;
    }


    private boolean isUnitMatchWithBuilding(String type) {
        MapAssetType person = MapAssetType.getPerson(type);
        if (person == null)
            return false;
        ArrayList<MapAssetType> persons = ((TrainingAndEmploymentBuilding) building).getPeopleType();
        for (MapAssetType unit : persons) {
            if (unit.equals(person)) return true;
        }
        return false;
    }

    private boolean isWeaponEnough(AttackingUnit attackingUnit, int count) {
        if (attackingUnit.getWeapons() == null) return true;
        for (Material weapon : attackingUnit.getWeapons()) {
            if (player.getGovernance().getStorageStock(weapon) < count) return false;
        }
        return true;
    }

    private boolean isGoldEnough(int gold, int count) {
        return (player.getGovernance().getGold().get() >= gold * count);
    }

    private int materialNeededForRepair() {
        return (int) ((building.getMaxHitPoint() - building.getHitPoint()) / building.getMaxHitPoint()
                * building.getNumberOfMaterialNeeded());
    }

    private boolean isThereEnemy(int number) {
        ArrayList<Cell> cells = map.getNearbyCells(building.getCoordinate(), number);
        for (Cell cell : cells) {
            for (MapAsset mapAsset : cell.getAllAssets()) {
                if (!mapAsset.getOwner().equals(player)) return true;
            }
        }
        return false;
    }

    public static void setIsModifiable(boolean isModifiable) {
        SelectedBuildingController.isModifiable = isModifiable;
    }
}
