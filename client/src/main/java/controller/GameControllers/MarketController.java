package controller.GameControllers;

import model.Game.Store.StoreMaterial;
import model.User.Player;
import model.enums.AssetType.Material;
import view.GameMenus.MarketMenus.MarketMenu;
import view.enums.messages.GameMessage.MarketMessage;

public class MarketController {
    private final Player currentPlayer;
    private MarketMenu menu;

    public MarketController(Player player) {
        this.currentPlayer = player;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public MarketMessage buy(String materialName, int amount) {
        StoreMaterial storeMaterial = StoreMaterial.getInstance(materialName);
        Material material = Material.getMaterial(materialName);
        if (storeMaterial == null) return MarketMessage.INVALID_MATERIAL;
        int gold = (int) currentPlayer.getGovernance().getGold().get();

        if (gold < storeMaterial.getPrice() * amount) return MarketMessage.NOT_ENOUGH_GOLD;
        if (currentPlayer.getGovernance().getStorageRemainingCapacity(material) < amount)
            return MarketMessage.NOT_ENOUGH_SPACE;
        currentPlayer.getGovernance().changeStorageStock(material, amount);
        currentPlayer.getGovernance().changeGold((-1) * storeMaterial.getPrice() * amount);
        return MarketMessage.BUY_SUCCESS;
    }

    public MarketMessage sell(String materialName, int amount) {
        StoreMaterial storeMaterial = StoreMaterial.getInstance(materialName);
        Material material = Material.getMaterial(materialName);
        if (storeMaterial == null) return MarketMessage.INVALID_MATERIAL;
        int stock = currentPlayer.getGovernance().getStorageStock(material);
        if (stock < amount) return MarketMessage.NOT_ENOUGH_STOCK;
        currentPlayer.getGovernance().changeStorageStock(material, amount * (-1));
        int change = (int) (storeMaterial.getPrice() * amount * 0.8);
        currentPlayer.getGovernance().changeGold(change);
        return MarketMessage.BUY_SUCCESS;
    }

    private String confirmBuyMessage(String materialName, int amount, int totalPrice) {
        String output = "Do you confirm the following purchase : \n";
        output += "material name : " + materialName + "\n";
        output += "amount : " + amount + "\n";
        output += "total price : " + totalPrice;
        return output;
    }

    private String confirmSellMessage(String materialName, int amount, int totalPrice) {
        String output = "Do you confirm the following retail : \n";
        output += "material name : " + materialName + "\n";
        output += "amount : " + amount + "\n";
        output += "total price : " + totalPrice;
        return output;
    }
}
