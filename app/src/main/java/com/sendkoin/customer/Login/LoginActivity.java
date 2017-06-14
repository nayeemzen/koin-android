package com.sendkoin.customer.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockApplication;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.R;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 5/29/17.
 */

public class LoginActivity extends AppCompatActivity implements LoginContract.View{

  @Inject
  LoginPresenter mPresenter;
  @BindView(R.id.fb_login)
  FancyButton facebookLoginBtn;
  @BindView(R.id.sms_login)
  FancyButton smsLoginBtn;

  private static final String TAG = "LoginActivity";
  private Unbinder unbinder;
  private CallbackManager callbackManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);
    unbinder = ButterKnife.bind(this);
    setUpDagger();
    setupFancyFBLoginBtn();
    setUpFancySMSLoginBtn();
    initFacebookLoginCallback();
  }

  private void setUpFancySMSLoginBtn() {
    smsLoginBtn.setText("Continue with SMS          ");
    smsLoginBtn.setBackgroundColor(Color.parseColor("#008489"));
    smsLoginBtn.setFocusBackgroundColor(Color.parseColor("#37B3B8"));
    smsLoginBtn.setTextSize(17);
    smsLoginBtn.setRadius(15);
    smsLoginBtn.setIconResource("\uf0e6");
    smsLoginBtn.setIconPosition(FancyButton.POSITION_LEFT);
    smsLoginBtn.setFontIconSize(26);
    smsLoginBtn.setIconPadding(10,10,15,13);
    smsLoginBtn.setPadding(10,10,10,10);
    smsLoginBtn.setCustomTextFont("Nunito-Bold.ttf");
  }

  private void setupFancyFBLoginBtn() {
    facebookLoginBtn.setText("Continue with Facebook");
    facebookLoginBtn.setBackgroundColor(Color.parseColor("#3b5998"));
    facebookLoginBtn.setFocusBackgroundColor(Color.parseColor("#5474b8"));
    facebookLoginBtn.setTextSize(17);
    facebookLoginBtn.setRadius(15);
    facebookLoginBtn.setIconResource("\uf082");
    facebookLoginBtn.setIconPosition(FancyButton.POSITION_LEFT);
    facebookLoginBtn.setFontIconSize(26);
    facebookLoginBtn.setIconPadding(10,10,15,13);
    facebookLoginBtn.setPadding(10,10,10,10);
    facebookLoginBtn.setCustomTextFont("Nunito-Bold.ttf");

  }

  private void initFacebookLoginCallback() {
    callbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().registerCallback(callbackManager,
        new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
            mPresenter.loginWithFacebook(loginResult.getAccessToken().getToken());
          }

          @Override
          public void onCancel() {
            Log.d(TAG, "Facebook login cancelled");
          }

          @Override
          public void onError(FacebookException error) {
            Log.e(TAG, "Facebook login error!", error);
          }
        });
  }

  private void setUpDagger() {
    DaggerLoginComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .loginModule(new LoginModule(this))
        .build()
        .inject(this);
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @OnClick(R.id.fb_login)
  public void loginWithFacebook(){
    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
  }

  @OnClick(R.id.sms_login)
  public void loginWithSMS(){
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
  }

  @Override
  public void loginWithFbFailure(String errorMessage) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void loginWithFbSuccess(String name) {
    Toast.makeText(this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();
    ((KoinApplication) getApplication()).getNetComponent().sessionManager().putAuthAttempts(0);
    Intent intent= new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.unsubscribe();
    unbinder.unbind();
  }
}
