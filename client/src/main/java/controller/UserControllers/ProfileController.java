package controller.UserControllers;

import model.Stronghold;
import model.User.User;
import utils.Captcha;
import view.enums.messages.UserMessage.ProfileMessage;

public class ProfileController {
    private final User currentUser;

    public ProfileController(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ProfileMessage changeUsername(String newUsername) {
        if (Stronghold.getInstance().userExists(newUsername)) return ProfileMessage.USERNAME_TAKEN;
        currentUser.changeUsername(newUsername);
        return ProfileMessage.USERNAME_CHANGE_SUCCESS;
    }

    public ProfileMessage changeNickname(String newNickname) {
        currentUser.changeNickname(newNickname);
        return ProfileMessage.NICKNAME_CHANGE_SUCCESS;
    }

    public ProfileMessage changePassword(String newPass, String oldPass, String captcha) {
        if(!Captcha.isFilledCaptchaValid(captcha)) return ProfileMessage.INVALID_CAPTCHA;
        if (!currentUser.isPasswordCorrect(oldPass)) return ProfileMessage.PASSWORD_INCORRECT;
        if (oldPass.equals(newPass)) return ProfileMessage.PASSWORD_NOT_NEW;
        currentUser.setPassword(newPass);
        return ProfileMessage.PASSWORD_CHANGE_SUCCESS;
    }

    public ProfileMessage changeEmail(String newEmail) {
        if (Stronghold.getInstance().emailExists(newEmail)) return ProfileMessage.EMAIL_EXISTS;
        currentUser.changeEmail(newEmail);
        return ProfileMessage.EMAIL_CHANGE_SUCCESS;
    }

    public ProfileMessage changeSlogan(String newSlogan) {
        currentUser.changeSlogan(newSlogan);
        return ProfileMessage.SLOGAN_CHANGE_SUCCESS;
    }

    public ProfileMessage removeSlogan() {
        currentUser.removeSlogan();
        return ProfileMessage.SLOGAN_REMOVAL_SUCCESS;
    }

    public int displayHighscore() {
        return currentUser.getHighScore();
    }

    public int displayRank() {
        return Stronghold.getInstance().getUserRank(currentUser);
    }

    public String displaySlogan() {
        return currentUser.getSlogan();
    }

    public String displayUserInfo() {
        return currentUser.toString();
    }
}
