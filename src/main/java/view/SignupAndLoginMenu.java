package view;

import controller.SignupAndLoginController;
import utils.FormatValidation;
import utils.SignupAndLoginUtils;
import view.enums.commands.SignupAndLoginCommands;
import view.enums.messages.SignupAndLoginMessages;


import java.util.HashMap;
import java.util.regex.Matcher;

public class SignupAndLoginMenu {
    private final SignupAndLoginController controller;

    public SignupAndLoginMenu(SignupAndLoginController controller) {
        this.controller = controller;
    }

    public String run() {
        String nextCommand;
        Matcher matcher;
        while (true) {
            nextCommand = Menu.getScanner().nextLine();
            SignupAndLoginCommands typeOfCommand = SignupAndLoginCommands.getCommand(nextCommand);
            if (typeOfCommand == null) {
                SignupAndLoginMessages.INVALID_COMMAND.printMessage();
                continue;
            }
            if (typeOfCommand.equals(SignupAndLoginCommands.LOGOUT)) break;

            matcher = SignupAndLoginCommands.getMatcher(nextCommand, typeOfCommand);
            switch (typeOfCommand) {
                case LOGIN:
                    loginCall(matcher);
                    break;
                case CHANGE_PASSWORD:
                    changePasswordCall(matcher);
                    break;
                case CREATE_USER:
                    createUserCall(matcher);
                    break;
            }
        }
        return null;
    }

    private void createUserCall(Matcher matcher) {
        HashMap<String, String> inputs = SignupAndLoginUtils.getInputs(matcher, SignupAndLoginCommands.CREATE_USER.getRegex());
        boolean randomSlogan = inputs.get("slogan").equals("random");
        SignupAndLoginMessages message = controller.signup(inputs);

        if (message.equals(SignupAndLoginMessages.RANDOM_PASSWORD)) {
            randomPasswordSuggestion(inputs, randomSlogan);
            message = controller.signup(inputs);
        }

        if (message.equals(SignupAndLoginMessages.EXISTING_USERNAME))
            if (isUsernameSuggestionAccept(inputs))
                message = controller.signup(inputs);

        message.printMessage();
        if (message.equals(SignupAndLoginMessages.SUCCESS_PROCESS)) {
            if (randomSlogan) printRandomSlogan(inputs);
            pickUpQuestion(inputs);
        }
    }

    private void loginCall(Matcher matcher) {
        HashMap<String, String> inputs = SignupAndLoginUtils.getInputs(matcher, SignupAndLoginCommands.LOGIN.getRegex());
        SignupAndLoginMessages messages = controller.login(inputs);
            messages.printMessage();
            if (messages.equals(SignupAndLoginMessages.TOO_MANY_ATTEMPTS))
                tooManyAttemptsError();
           if (messages.equals(SignupAndLoginMessages.SUCCESS_PROCESS))
               System.out.println("success");//TODO DIBA ->
    }

    private void changePasswordCall(Matcher matcher) {
        SignupAndLoginMessages messages = controller.getCurrentUser(matcher.group("username"));
        messages.printMessage();
            String nextCommand = Menu.getScanner().nextLine();
            System.out.println(controller.currentUser.getPasswordRecoveryQuestion());
            if (controller.currentUser.isRecoveryPasswordCorrect(nextCommand)) {
                recoveryPassword();
            } else System.out.println("Incorrect answer!");
    }

    private boolean isUsernameSuggestionAccept(HashMap<String, String> inputs) {
        String username = inputs.get("username");
        inputs.replace("username", username, SignupAndLoginUtils.generateRandomUsername(username));
        System.out.println("Do you want to continue  registration process with " + inputs.get("username") + "?");
        return (FormatValidation.isFormatValid(Menu.getScanner().nextLine(), FormatValidation.YES));
    }

    private void randomPasswordSuggestion(HashMap<String, String> inputs, boolean randomSlogan) {
        if (randomSlogan) {
            printRandomSlogan(inputs);
            randomSlogan = false;
        }
        System.out.println("Your random password is: " + inputs.get("password"));
        System.out.print("Please re-enter your password here:\t");
        inputs.replace("passwordConfirmation", inputs.get("passwordConfirmation"), Menu.getScanner().nextLine());
    }

    private void printRandomSlogan(HashMap<String, String> inputs) {
        System.out.println("your random slogan is: " + inputs.get("slogan"));
    }
    private void  pickUpQuestion(HashMap<String, String> inputs) {
        SignupAndLoginMessages message = SignupAndLoginMessages.FAIL_PICKING_UP_QUESTION;
        do {
            SignupAndLoginMessages.PICKING_QUESTION.printMessage();
            String nextCommand = Menu.getScanner().nextLine();
            Matcher matcher = SignupAndLoginCommands.getMatcher(nextCommand, SignupAndLoginCommands.QUESTION_PICK);
            if (matcher == null)
                System.out.println("some error founds");
            else {
                HashMap<String, String> pickQuestionInputs =
                        SignupAndLoginUtils.getInputs(matcher, SignupAndLoginCommands.QUESTION_PICK.getRegex());
                pickQuestionInputs.put("username", inputs.get("username"));
                 message = controller.pickQuestion(pickQuestionInputs);
                message.printMessage();
            }
        } while (!message.equals(SignupAndLoginMessages.FAIL_PICKING_UP_QUESTION));
    }
    private void  tooManyAttemptsError() {
        int minutes = (int) (controller.getTimeUntilLogin() / 60);
        int seconds = (int) (controller.getTimeUntilLogin() % 60);
        System.out.println("Too many failed attempts. Please wait " + minutes + " minutes and " +
                seconds + " seconds before trying again");
    }
    private void recoveryPassword() {
        String nextCommand;
        while (true) {
            System.out.println("Enter new Password:");
            nextCommand = Menu.getScanner().nextLine();
            if (!FormatValidation.isFormatValid(nextCommand, FormatValidation.PASSWORD_LENGTH)) {
                SignupAndLoginMessages.PASSWORD_WEEK_LENGTH.printMessage();
            } else if (!FormatValidation.isFormatValid(nextCommand, FormatValidation.PASSWORD_LETTERS))
                SignupAndLoginMessages.PASSWORD_WEEK_LETTERS_PROBLEM.printMessage();
            else {
                controller.currentUser.setPassword(nextCommand);
                break;
            }
        }
    }
}

