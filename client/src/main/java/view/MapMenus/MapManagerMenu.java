package view.MapMenus;

import controller.UserControllers.MainController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Stronghold;
import network.Connection;
import network.Request;
import view.Main;
import view.UserMenus.LoginMenu;
import view.UserMenus.MainMenu;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapManagerMenu extends Application {

    private static Stage mainStage;
    public ScrollPane mapListScroll;
    public ScrollPane receivedRequestScroll;
    public TextField usernameField;
    public TextField mapNameField;
    public Label errorMessageText;

    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL url = LoginMenu.class.getResource("/FXML/Mapfxml/mapManager.fxml");
        AnchorPane anchorPane = FXMLLoader.load(url);
        anchorPane.setBackground(new Background(new BackgroundImage(new Image(
                MainMenu.class.getResource("/assets/backgrounds/mapManager.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1, 1, true, true, false, false))));
        Scene scene = new Scene(anchorPane);
        mainStage = stage;
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public void initialize(){
        loadMaps();
        loadRequests();
    }

    public void loadMaps(){
        MainController.currentUser.updateLists();
        ArrayList<String> maps = MainController.currentUser.getMapList();
        VBox vBox = new VBox();
        int i=1;
        for (String map : maps) {
            String output = (i++) + ". Map id: " + map;
            Label label = new Label(output);
            label.setStyle("-fx-text-fill: white");
            vBox.getChildren().add(label);
        }
        mapListScroll.setContent(vBox);
    }

    public void loadRequests(){
        HashMap<String, String> requests = MainController.currentUser.getPendingMaps();
        VBox vBox = new VBox();
        int i=1;
        for (String mapId : requests.keySet()){
            String output = (i++) + ". Map id: " + mapId + ", sender : " + requests.get(mapId);
            HBox hBox = new HBox();
            hBox.setSpacing(5);
            Label label = new Label(output);
            label.setStyle("-fx-text-fill: white");
            Label accept = new Label("accept");
            label.setStyle("-fx-text-fill: white");
            Label reject = new Label("reject");
            label.setStyle("-fx-text-fill: white");
            accept.setOnMouseClicked(e -> acceptRequest(mapId));
            reject.setOnMouseClicked(e -> rejectRequest(mapId));
            hBox.getChildren().addAll(label, accept, reject);
            vBox.getChildren().add(hBox);
        }
        receivedRequestScroll.setContent(vBox);
    }

    public void acceptRequest(String id){
        MainController.currentUser.acceptMap(id);
        loadRequests();
        loadMaps();
    }

    public void rejectRequest(String mapId){
        MainController.currentUser.rejectMap(mapId);
        loadRequests();
        loadMaps();
    }

    public void sendRequest(){
        String mapId = mapNameField.getText();
        String user = usernameField.getText();
        mapNameField.clear();
        usernameField.clear();
        if (mapId.equals("")) printError("Enter map id");
        else if (user.equals("")) printError("Enter user id");
        else if (!Stronghold.getInstance().userExists(user)) printError("No such user");
        else if (!MainController.currentUser.getMapList().contains(mapId)) printError("No such map");
        else {
            Request request = new Request();
            request.setType("map");
            request.setCommand("send_map");
            request.addParameter("id", mapId);
            request.addParameter("user", user);
            Connection.getInstance().sendRequest(request);
        }
    }
    public void back() throws Exception {
        MainMenu.mainController.menu.start(Main.mainStage);
    }

    public void printError(String text) {
        errorMessageText.setText(text);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> errorMessageText.setText(""))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
}
