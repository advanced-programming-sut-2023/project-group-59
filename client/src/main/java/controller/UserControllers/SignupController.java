package controller.UserControllers;

import model.Stronghold;
import model.User.User;
import model.enums.User.Slogan;
import utils.Captcha;
import utils.FormatValidation;
import utils.Pair;
import view.enums.messages.UserMessage.SignupAndLoginMessage;

import java.util.HashMap;

public class SignupController {
    private final Stronghold stronghold = Stronghold.getInstance();
    private User newUser;

    public SignupAndLoginMessage signup(HashMap<String, String> inputs) {
        if (hasEmptyField(inputs))
            return SignupAndLoginMessage.EMPTY_FIELD;
        if (checkFormatOfInputs(inputs) != null)
            return checkFormatOfInputs(inputs);

        if (!inputs.get("password").equals(inputs.get("passwordConfirmation")))
            return SignupAndLoginMessage.CONFIRMATION_ERROR;
        if (stronghold.emailExists(inputs.get("email")))
            return SignupAndLoginMessage.EXISTED_EMAIL;
        if (stronghold.userExists(inputs.get("username")))
            return SignupAndLoginMessage.EXISTING_USERNAME;

        this.newUser = new User(inputs.get("username"), inputs.get("password"), inputs.get("email"),
                inputs.get("nickname"), inputs.get("slogan"));
        return SignupAndLoginMessage.SUCCESS_PROCESS;
    }

    public SignupAndLoginMessage pickQuestion(String question, String answer, String confirmation, String captcha) {
        if (!Captcha.isFilledCaptchaValid(captcha))
            return SignupAndLoginMessage.INVALID_CAPTCHA;
        if (!answer.equals(confirmation))
            return SignupAndLoginMessage.FAIL_PICKING_UP_QUESTION;
        Pair pair = new Pair(question, answer);
        Stronghold.getInstance().addUser(newUser);
        newUser.setPasswordRecovery(pair);
        return SignupAndLoginMessage.SUCCESS_CREATING_USER;
    }

    private boolean hasEmptyField(HashMap<String, String> inputs) {
        return inputs.get("password").equals("") || inputs.get("email").equals("")
                || inputs.get("email").equals("") || inputs.get("slogan").equals("");
    }

    private SignupAndLoginMessage checkFormatOfInputs(HashMap<String, String> inputs) {
        if (!FormatValidation.isFormatValid(inputs.get("username"), FormatValidation.USERNAME))
            return SignupAndLoginMessage.INVALID_USERNAME_FORMAT;

        if (!FormatValidation.isPasswordValid(inputs.get("password")).equals(SignupAndLoginMessage.SUCCESS_PROCESS))
            return FormatValidation.isPasswordValid(inputs.get("password"));
        if (!FormatValidation.isFormatValid(inputs.get("email"), FormatValidation.EMAIL))
            return SignupAndLoginMessage.INVALID_EMAIL_FORMAT;
        return null;
    }

    public String generateRandomSlogan() {
        return Slogan.values()[(int) (Math.random() * 5)].getSlogan();
    }

//    public void changeNullSloganToEmpty(HashMap<String, String> inputs) {
//        if (inputs.get("sloganTest") == null) inputs.replace("slogan", null, "");
//    }


    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }
}
