package be.gling.businessApp.view;

import be.gling.businessApp.model.dto.technical.DTO;

/**
 * Created by florian on 4/12/14.
 */
public interface RequestActionInterface {

    public void displayErrorMessage(String errorMessage);

    public void loadingAction(boolean loading);

    public void successAction(DTO successDTO);
}
