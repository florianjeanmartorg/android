package be.gling.businessApp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.facebook.FacebookSdk;

import be.gling.businessApp.R;
import be.gling.businessApp.model.dto.LoginSuccessDTO;
import be.gling.businessApp.model.service.AccountService;
import be.gling.businessApp.model.util.Storage;
import be.gling.businessApp.model.util.exception.MyException;
import be.gling.businessApp.model.util.externalRequest.RequestEnum;
import be.gling.businessApp.model.util.externalRequest.WebClient;
import be.gling.businessApp.view.activity.technical.AbstractActivity;

import java.util.Date;

/**
 * Created by florian on 23/11/14.
 */
public class WelcomeActivity extends AbstractActivity {

    public static final boolean DEV_MODE = false;

    /**
     * build
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);

        //test authentication
        String authenticationKey = AccountService.getAuthenticationKey(this);

        //test connections
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();


        if (authenticationKey != null) {
            Storage.setAuthenticationKey(authenticationKey);
            LoadDataRequest loadDataRequest = new LoadDataRequest();
            loadDataRequest.execute();


        } else {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        }
    }


    /**
     * login request
     */
    private class LoadDataRequest extends AsyncTask<String, Void, Void> {

        private LoginSuccessDTO loginSuccessDTO;
        private String          errorMessage;

        private LoadDataRequest() {
        }

        /**
         * send request
         */
        @Override
        protected Void doInBackground(String... params) {

            WebClient<LoginSuccessDTO> webClient = new WebClient<LoginSuccessDTO>(RequestEnum.LOAD_DATA, LoginSuccessDTO.class);

            try {
                loginSuccessDTO = webClient.sendRequest();
            } catch (MyException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
            return null;
        }

        /**
         * after execution
         */
        @Override
        protected void onPostExecute(Void result) {

            if (errorMessage != null) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            } else {
                //store data and change interface
                Storage.store(WelcomeActivity.this, loginSuccessDTO);
                startActivity(new Intent(WelcomeActivity.this, MAIN_ACTIVITY));
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //display
        }

    }
}
