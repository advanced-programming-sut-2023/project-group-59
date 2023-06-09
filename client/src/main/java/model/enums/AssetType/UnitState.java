package model.enums.AssetType;

public enum UnitState {
    DEFENSIVE(3),
    STANDING(0),
    OFFENSIVE(10);
    private final int triggerRange;

    UnitState(int triggerRange) {
        this.triggerRange = triggerRange;
    }

    public static UnitState getState(String stateName) {
        for (UnitState m : UnitState.values()) {
            if (m.name().equalsIgnoreCase(stateName))
                return m;
        }
        return null;
    }

    public int getTriggerRange() {
        return triggerRange;
    }
}
