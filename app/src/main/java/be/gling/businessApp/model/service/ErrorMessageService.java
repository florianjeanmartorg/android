package be.gling.businessApp.model.service;

import be.gling.businessApp.model.util.ErrorMessage;

/**
 * Created by florian on 11/11/14.
 */
public class ErrorMessageService {

    public String getMessage(ErrorMessage errorMessage, Object... params) {

        String message = errorMessage.getMessage();

        for (int i = 0; i < params.length; i++) {
            message.replace("{" + i + "}", params[i].toString());
        }

        return message;
    }
}
