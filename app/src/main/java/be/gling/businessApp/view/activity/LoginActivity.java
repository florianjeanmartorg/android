package be.gling.businessApp.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import be.gling.businessApp.R;
import be.gling.businessApp.model.dto.LoginSuccessDTO;
import be.gling.businessApp.model.dto.post.LoginDTO;
import be.gling.businessApp.model.dto.technical.DTO;
import be.gling.businessApp.model.util.Storage;
import be.gling.businessApp.model.util.exception.MyException;
import be.gling.businessApp.model.util.externalRequest.Request;
import be.gling.businessApp.model.util.externalRequest.RequestEnum;
import be.gling.businessApp.model.util.externalRequest.WebClient;
import be.gling.businessApp.view.RequestActionInterface;
import be.gling.businessApp.view.activity.technical.AbstractActivity;
import be.gling.businessApp.view.dialog.DialogConstructor;
import be.gling.businessApp.view.widget.technical.Field;
import be.gling.businessApp.view.widget.technical.Form;

public class LoginActivity extends AbstractActivity implements RequestActionInterface {

    public static final String EXTRA_EMAIL = "email";
    private Form form = null;
    private Dialog loadingDialog;
    private CallbackManager callbackManager;

    /**
     * build
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);

        loadingDialog = DialogConstructor.dialogLoading(this);

        setContentView(R.layout.activity_login);


        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        FacebookSdk.sdkInitialize(this.getApplicationContext());

         callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                WebClient<LoginSuccessDTO> webClient = new WebClient<LoginSuccessDTO>(RequestEnum.LOGIN_FACEBOOK,
                        LoginSuccessDTO.class);

                webClient.setParams("token",loginResult.getAccessToken().getToken());

                Request request = new Request(LoginActivity.this, webClient);

                //execute request
                request.execute();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                //TODO
                Log.w("LOG","ERROR : "+exception.toString());
                int i = 0;
                // App code
            }
        });

        // to registration
        findViewById(R.id.b_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                finish();
            }
        });


        //build form
        try {
            form = new Form(this, new LoginDTO(),
                    new Field.FieldProperties(LoginDTO.class.getDeclaredField("email"), R.string.g_email, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
                    new Field.FieldProperties(LoginDTO.class.getDeclaredField("password"), R.string.g_password, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
        insertPoint.addView(form, ViewGroup.LayoutParams.WRAP_CONTENT);

        // login action
        findViewById(R.id.b_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    DTO dto = form.control();

                    if (dto != null) {

                        //create request
                        WebClient<LoginSuccessDTO> webClient = new WebClient<LoginSuccessDTO>(RequestEnum.LOGIN,
                                dto,
                                LoginSuccessDTO.class);

                        Request request = new Request(LoginActivity.this, webClient);

                        //execute request
                        request.execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    @Override
    public void displayErrorMessage(String errorMessage) {
        findViewById(R.id.error_message_container).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.error_message)).setText(errorMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loadingAction(boolean loading) {

        form.setEnabled(!loading);
        if (loading) {
            loadingDialog.show();

            // create animation and add to the refresh item
            findViewById(R.id.error_message_container).setVisibility(View.GONE);
        } else {
            loadingDialog.cancel();
        }
    }

    @Override
    public void successAction(DTO successDTO) {

        Storage.store(this, (LoginSuccessDTO) successDTO);
        Intent intent = new Intent(LoginActivity.this, MAIN_ACTIVITY);
        //startActivityForResult(intent,0);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
