package com.sinenco.smartherald.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinenco.smartherald.R;

/**
 * Created by jordandegea on 06/08/16.
 */
public class AccountActivity extends AppCompatActivity {


    private TextView usernameLabel;
    private EditText usernameField;
    private TextView emailLabel;
    private EditText emailField;
    private Button createAnonymousButton;
    private Button logInButton;
    private Button signUpButton;
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        usernameLabel = (TextView) findViewById(R.id.usernameLabel);
        usernameField = (EditText) findViewById(R.id.usernameField);
        emailLabel = (TextView) findViewById(R.id.emailLabel);
        emailField = (EditText) findViewById(R.id.emailField);
        createAnonymousButton = (Button) findViewById(R.id.createAnonymousButton);
        logInButton = (Button) findViewById(R.id.logInButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        logOutButton = (Button) findViewById(R.id.logOutButton);

        populatesFields();
    }

    private void populatesFields(){
        if(ParseUser.getCurrentUser() == null){
            usernameLabel.setVisibility(View.INVISIBLE);
            usernameField.setVisibility(View.INVISIBLE);
            emailLabel.setVisibility(View.INVISIBLE);
            emailField.setVisibility(View.INVISIBLE);
            logOutButton.setVisibility(View.INVISIBLE);

            createAnonymousButton.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
        }else{
            usernameLabel.setVisibility(View.VISIBLE);
            usernameField.setVisibility(View.VISIBLE);
            emailLabel.setVisibility(View.VISIBLE);
            emailField.setVisibility(View.VISIBLE);
            logOutButton.setVisibility(View.VISIBLE);

            createAnonymousButton.setVisibility(View.INVISIBLE);
            logInButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);

            ParseUser user = ParseUser.getCurrentUser();
            if ( ParseAnonymousUtils.isLinked(user)){
                usernameField.setText("Anonymous");
                //emailTextField.text = ""
            }else{
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.accountToTutorial) {
            startTutorialIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startTutorialIntent() {

        Intent intent;
        intent = new Intent(AccountActivity.this, TutorialActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
