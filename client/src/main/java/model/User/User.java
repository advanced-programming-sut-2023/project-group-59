package model.User;

import com.google.gson.Gson;
import model.Stronghold;
import network.Connection;
import network.Request;
import utils.*;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String nickname;
    private String slogan;
    private String email;
    private Pair passwordRecovery;
    private String avatarPath;
    private final ArrayList<User> friends;
    private final ArrayList<User> senders;
    private int highScore;

    public User(String username, String password, String email, String nickname, String slogan) {
        friends = new ArrayList<>();
        senders = new ArrayList<>();
        this.username = username;
        this.password = PasswordConverter.encodePassword(password);
        this.email = email;
        this.nickname = nickname;
        this.slogan = slogan;
        this.highScore = 0;
        this.avatarPath = User.class.getResource("/assets/avatars/defaults/default.png").toExternalForm();
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSlogan() {
        return slogan;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("highscore");
        request.addParameter("username", username);
        request.addParameter("highscore", String.valueOf(highScore));
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordRecoveryQuestion() {
        return passwordRecovery.x;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(PasswordConverter.encodePassword(password));
    }

    public boolean isRecoveryPasswordCorrect(String recoveryAnswer) {
        return passwordRecovery.y.equals(recoveryAnswer);
    }

    public void changeUsername(String username) {
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("username");
        request.addParameter("username", this.username);
        this.username = username;
        request.addParameter("new_username", username);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("nickname");
        request.addParameter("username", this.username);
        request.addParameter("nickname", nickname);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void changeSlogan(String slogan) {
        this.slogan = slogan;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("slogan");
        request.addParameter("username", this.username);
        request.addParameter("slogan", slogan);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void changeEmail(String email) {
        this.email = email;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("email");
        request.addParameter("username", this.username);
        request.addParameter("email", email);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPasswordRecovery(Pair passwordRecovery) {
        this.passwordRecovery = passwordRecovery;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("password_recovery");
        request.addParameter("username", this.username);
        request.addParameter("recovery", new Gson().toJson(passwordRecovery));
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(String newPass) {
        this.password = PasswordConverter.encodePassword(newPass);
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("setPassword");
        request.addParameter("username", this.username);
        request.addParameter("password", password);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSlogan() {
        this.slogan = "";
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("removeSlogan");
        request.addParameter("username", this.username);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
        Request request = new Request();
        request.setType("user_change");
        request.setCommand("set_avatar");
        request.addParameter("username", this.username);
        request.addParameter("avatar_path", avatarPath);
        String result = Connection.getInstance().sendRequest(request);
        if (result.startsWith("400")) try {
            throw new Exception("User doesn't exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public int getRank() {
        return Stronghold.getInstance().getUserRank(this);
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public void addSender(User user) {
        senders.add(user);
    }

    public void removeFriend(User user) {
        friends.remove(user);
    }

    public void removeSender(User user) {
        senders.remove(user);
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public ArrayList<User> getSenders() {
        return senders;
    }

    public boolean isFriend(User user) {
        return friends.contains(user);
    }

    public boolean isHaveRequestFrom(User user) {
        return senders.contains(user);
    }

    @Override
    public String toString() {
        return "username : " + username +
                "\nnickname : " + nickname +
                "\nslogan : " + slogan +
                "\nemail : " + email +
                "\nhighscore : " + highScore;
    }
}
