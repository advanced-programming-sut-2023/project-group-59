package controller.MapControllers;

import model.Map.Map;
import model.User;
import view.MapMenus.BuildingPlacementMenu.BuildingPlacementMenu;

public class BuildingPlacementController {
    private User currentUser;
    private Map map;
    private String currentBuildingType;

    public BuildingPlacementController(User currentUser, Map map) {
        this.currentUser = currentUser;
        this.map = map;
    }

    public void run() {
        BuildingPlacementMenu menu = new BuildingPlacementMenu(this);
        while (true) {
            if (menu.run().equals("back")) return;
        }
    }
}

