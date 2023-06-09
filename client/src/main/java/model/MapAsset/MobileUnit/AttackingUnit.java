package model.MapAsset.MobileUnit;

import model.Map.Cell;
import model.Map.Map;
import model.MapAsset.Building.DefenseAndAttackBuilding;
import model.MapAsset.MapAsset;
import model.User.Player;
import model.enums.AssetType.MapAssetType;
import model.enums.AssetType.Material;
import model.enums.AssetType.UnitState;
import model.enums.AttackTarget;
import utils.Vector2D;

import java.util.ArrayList;

public class AttackingUnit extends MobileUnit {
    public final ArrayList<Material> weapon;
    private final int attackDamage;
    private final int attackRange;
    private final boolean isAreaSplash;
    private final ArrayList<AttackTarget> targets;
    private UnitState state;
    private MapAsset selectedAttackTarget;
    private MapAsset nextRoundAttackTarget;

    public AttackingUnit(AttackingUnit reference, Vector2D coordinate, Player owner) {
        super(reference, coordinate, owner);
        this.attackDamage = reference.attackDamage;
        this.attackRange = reference.attackRange;
        this.targets = reference.targets;
        this.weapon = reference.weapon;
        this.isAreaSplash = reference.isAreaSplash;
        state = UnitState.STANDING;
    }

    public void processNextRoundDecision(Map map) {
        checkForTargetDeath();
        nextRoundAttackTarget = null;
        if (finalMoveDestination != null)
            return;
        int turnAttackRange = attackRange + getTurnAttackRange(map);
        if (selectedAttackTarget != null) {
            if (coordinate.getDistance(selectedAttackTarget.getCoordinate(), true) <= turnAttackRange) {
                finalMoveDestination = null;
                nextRoundAttackTarget = selectedAttackTarget;
            } else {
                finalMoveDestination = nextRoundAttackTarget.getCoordinate();
                nextRoundAttackTarget = null;
            }
            return;
        }
        MapAsset attackTarget = findTarget(map, turnAttackRange);
        if (attackTarget != null) {
            if (state == UnitState.OFFENSIVE)
                selectedAttackTarget = attackTarget;
            nextRoundAttackTarget = attackTarget;
            finalMoveDestination = null;
            return;
        }
        MapAsset triggeredAttackTarget = findTarget(map, state.getTriggerRange());
        if (triggeredAttackTarget != null) {
            nextRoundAttackTarget = null;
            finalMoveDestination = triggeredAttackTarget.getCoordinate();
            return;
        }
        nextRoundAttackTarget = null;
        finalMoveDestination = null;
    }

    private int getTurnAttackRange(Map map) {
        DefenseAndAttackBuilding standingTower = null;
        for (MapAsset asset : map.getCell(getCoordinate()).getAllAssets())
            if(asset instanceof DefenseAndAttackBuilding)
                standingTower = (DefenseAndAttackBuilding) asset;
        if(standingTower == null) return 0;
        return standingTower.getFireRange();
    }

    private void checkForTargetDeath() {
        if (selectedAttackTarget != null && selectedAttackTarget.getHitPoint() <= 0)
            selectedAttackTarget = null;
    }

    private MapAsset findTarget(Map map, int range) {
        ArrayList<Cell> inRangeCells = map.getNearbyCells(coordinate, range);
        for (Cell cell : inRangeCells) {
            for (MapAsset asset : cell.getAllAssets()) {
                Player assetOwner = asset.getOwner();
                if (assetOwner == null || assetOwner.equals(owner))
                    continue;
                MapAssetType enemyType = asset.getType();
                for (AttackTarget target : targets) {
                    for (MapAssetType assetType : target.getItems())
                        if (enemyType == assetType) return asset;
                }
            }
        }
        return null;
    }

    public ArrayList<Material> getWeapons() {
        return weapon;
    }

    @Override
    public void takeDamageFrom(AttackingUnit attacker) {
        if (selectedAttackTarget == null)
            selectedAttackTarget = attacker;
        super.takeDamageFrom(attacker);
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public ArrayList<AttackTarget> getTargets() {
        return targets;
    }

    public void setState(UnitState state) {
        this.state = state;
    }

    public void selectAttackTarget(MapAsset target) {
        selectedAttackTarget = target;
        super.selectMoveDestination(null);
    }

    public void selectMoveDestination(Vector2D dest) {
        selectedAttackTarget = null;
        super.selectMoveDestination(dest);
    }

    @Override
    public void selectPatrolPath(Vector2D v1, Vector2D v2) {
        selectedAttackTarget = null;
        super.selectPatrolPath(v1, v2);
    }

    public MapAsset getNextRoundAttackTarget() {
        if (nextRoundAttackTarget == null) return null;
        if (nextRoundAttackTarget.getHitPoint() <= 0)
            return null;
        return nextRoundAttackTarget;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", attack damage=" + attackDamage +
                ", attack range=" + attackRange +
                ", unit state=" + state.name().toLowerCase() +
                ", weapon=" + weapon +
                ", targets=" + targets;
    }
}
