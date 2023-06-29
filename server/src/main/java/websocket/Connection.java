package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.ChatManager;
import database.Database;
import model.Request;
import model.User;
import model.chatRoom.Chat;
import utils.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Connection extends Thread {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private User loggedInUser;

    public Connection(Socket socket) {
        this.socket = socket;
        System.out.println("Connection from " + socket.getInetAddress() + socket.getPort());
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closedConnection() {
        System.out.println("Disconnected " + socket.getInetAddress() + socket.getPort());
        if (loggedInUser != null) {
            loggedInUser.setSocket(null);
            loggedInUser = null;
        }
    }

    @Override
    public void run() {
        Request request;
        try {
            while (true) {
                try {
                    request = new Gson().fromJson(inputStream.readUTF(), Request.class);
                } catch (EOFException e) {
                    closedConnection();
                    return;
                }
                switch (request.getType()) {
                    case "connect":
                        handelConnection(request);
                        break;
                    case "user_query":
                        handelUsersQuery(request);
                        break;
                    case "user_change":
                        handelUsersChange(request);
                        break;
                    case "chat":
                        handelChat(request);
                        break;
                    case "friend":
                        handelFriend(request);
                        break;
                    case "scoreboard":
                        handelScoreboard();
                        break;
                    case "lobby":
                        handelLobby();
                        break;
                    case "game":
                        handelGame();
                        break;
                    default:
                        outputStream.writeUTF("400: bad request");
                }
            }
        } catch (SocketException socketException) {
            closedConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handelConnection(Request request) throws IOException {
        User user = Database.getInstance().getUser(request.getParameters().get("username"));
        if (user == null) {
            outputStream.writeUTF("400: no_user");
            return;
        }
        switch (request.getCommand()) {
            case "login":
                if (user.isOnline()) {
                    outputStream.writeUTF("400: Already logged in");
                    return;
                }
                user.setSocket(socket);
                loggedInUser = user;
                outputStream.writeUTF("200: Sign in success");
                break;
            case "logout":
                loggedInUser = null;
                user.setSocket(null);
                outputStream.writeUTF("200: Log out success");
                break;
            default:
                outputStream.writeUTF("400: bad request");
        }
    }

    private void handelUsersQuery(Request request) throws IOException {
        switch (request.getCommand()) {
            case "get_user":
                User user = Database.getInstance().getUser(request.getParameters().get("username"));
                if (user == null) outputStream.writeUTF("400: no_user");
                else outputStream.writeUTF(new Gson().toJson(user));
                break;
            case "user_exists":
                outputStream.writeUTF(String.valueOf(
                        Database.getInstance().userExists(request.getParameters().get("username"))));
                break;
            case "email_exists":
                outputStream.writeUTF(String.valueOf(
                        Database.getInstance().emailExists(request.getParameters().get("email"))));
                break;
            case "get_users":
                outputStream.writeUTF(new Gson().toJson(Database.getInstance().getUsers()));
                break;
            case "get_rankings":
                outputStream.writeUTF(new Gson().toJson(Database.getInstance().getUserRankings()));
                break;
            case "get_user_rank":
                outputStream.writeUTF(String.valueOf(
                        Database.getInstance().getUserRank(request.getParameters().get("username"))));
                break;
            case "get_online_users":
                outputStream.writeUTF(new Gson().toJson(Database.getInstance().getOnlineUsers()));
                break;
            case "user_online":
                outputStream.writeUTF(String.valueOf(
                        Database.getInstance().getUser(request.getParameters().get("username")).isOnline()));
                break;
            default:
                outputStream.writeUTF("400: bad request");
        }
    }

    private void handelUsersChange(Request request) throws IOException {
        if (request.getCommand().equals("add")) {
            Database.getInstance().addUser(new Gson().fromJson(request.getParameters().get("user"), User.class));
            outputStream.writeUTF("200: successfully added");
            return;
        }
        User user = Database.getInstance().getUser(request.getParameters().get("username"));
        if (user == null) {
            outputStream.writeUTF("400: no_user");
            return;
        }
        switch (request.getCommand()) {
            case "highscore":
                user.setHighScore(Integer.parseInt(request.getParameters().get("highscore")));
                break;
            case "username":
                user.changeUsername(request.getParameters().get("new_username"));
                break;
            case "nickname":
                user.changeNickname(request.getParameters().get("nickname"));
                break;
            case "slogan":
                user.changeSlogan(request.getParameters().get("slogan"));
                break;
            case "email":
                user.changeEmail(request.getParameters().get("email"));
                break;
            case "password_recovery":
                user.setPasswordRecovery(new Gson().fromJson(request.getParameters().get("recovery"), Pair.class));
                break;
            case "setPassword":
                user.setPassword(request.getParameters().get("password"));
                break;
            case "removeSlogan":
                user.removeSlogan();
                break;
            case "set_avatar":
                user.setAvatarPath(request.getParameters().get("avatar_path"));
                break;
            default:
                outputStream.writeUTF("400: bad request");
                return;
        }
        outputStream.writeUTF("200: success");
    }

    private void handelChat(Request request) throws IOException {
        switch (request.getCommand()) {
            case "update_chat":
                Chat chat = new Gson().fromJson(request.getParameters().get("chat"), Chat.class);
                ChatManager.updateChat(chat, chat.getChatMode());
                ChatManager.notifyAllMembers();
                outputStream.writeUTF("200: successfully updated");
                break;
            case "get_global_chat":
                String out = new Gson().toJson(ChatManager.loadGlobalChat());
                outputStream.writeUTF(out);
                break;
            case "get_private_chats":
                Gson gson = new GsonBuilder().serializeNulls().create();
                JsonObject mainObject = new JsonObject();
                JsonArray usersArray = new JsonArray();
                for (Chat pvChat : ChatManager.loadPrivateChats())
                    usersArray.add(gson.toJsonTree(pvChat).getAsJsonObject());
                mainObject.add("chats", usersArray);
                outputStream.writeUTF(gson.toJson(mainObject));
                break;
            case "get_room_chats":
                Gson gson1 = new GsonBuilder().serializeNulls().create();
                JsonObject mainObject1 = new JsonObject();
                JsonArray usersArray1 = new JsonArray();
                for (Chat pvChat : ChatManager.loadRoomChats())
                    usersArray1.add(gson1.toJsonTree(pvChat).getAsJsonObject());
                mainObject1.add("chats", usersArray1);
                outputStream.writeUTF(gson1.toJson(mainObject1));
                break;
            case "load_chat":
                String out1 = new Gson().toJson(ChatManager.loadChat(request.getParameters().get("chat_id"),
                        new Gson().fromJson(request.getParameters().get("chat_type"), Chat.ChatMode.class)));
                outputStream.writeUTF(out1);
                break;

        }
    }

    private void handelFriend(Request request) throws IOException {
        User user = Database.getInstance().getUser(request.getParameters().get("username"));
        if (user == null) {
            outputStream.writeUTF("400: no_user");
            return;
        }
        switch (request.getCommand()) {
            case "add_friend":
                user.addFriend((new Gson().fromJson(request.getParameters().get("user"), User.class)));
                break;
            case "remove_friend":
                user.removeFriend((new Gson().fromJson(request.getParameters().get("user"), User.class)));
                break;
            case "add_sender":
                user.addSender((new Gson().fromJson(request.getParameters().get("user"), User.class)));
                break;
            case "remove_sender":
                user.removeSender((new Gson().fromJson(request.getParameters().get("user"), User.class)));
                break;
            default:
                outputStream.writeUTF("400: bad request");
        }
        outputStream.writeUTF("200: success");
    }

    private void handelScoreboard() {

    }

    private void handelLobby() {

    }

    private void handelGame() {

    }
}
