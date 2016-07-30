package com.sinenco.smartherald.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinenco.smartherald.R;

public class MessageActivity extends AppCompatActivity {


    private ParseObject messageObject ;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();

        if (intent == null) {
            this.finish();
        }

        final String messageId = intent.getStringExtra("message");
        if ( messageId == null){
            this.finish();
        }

        this.setTitle("Loading...");



        final MessageActivity self = this ;


        webView = (WebView) findViewById(R.id.webView);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.getInBackground(messageId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    messageObject = object ;
                    //webview.getSettings().setJavaScriptEnabled(true);
                    webView.loadData(object.getString("content"), "text/html", "UTF-8");
                } else {
                    self.finish();
                }
            }
        });





    }

}
