package be.gling.businessApp.model.dto;

import be.gling.businessApp.model.dto.technical.DTO;
import be.gling.businessApp.model.util.annotation.NotNull;

/**
 * Created by florian on 3/05/15.
 */
public class FacebookAuthenticationDTO extends DTO {

    private String userId;

    @NotNull
    private String token;

    private LangDTO lang;

    public FacebookAuthenticationDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "FacebookAuthenticationDTO{" +
                "userId=" + userId +
                ", token='" + token + '\'' +
                '}';
    }

    public LangDTO getLang() {
        return lang;
    }

    public void setLang(LangDTO lang) {
        this.lang = lang;
    }
}
