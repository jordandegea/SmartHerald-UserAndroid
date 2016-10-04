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
                    String content = object.getString("content");

                    content = content.replace("{$js_jquery}", "http://code.jquery.com/jquery-3.1.1.min.js");
                    content = content.replace("{$bootswatch_cerulean}", "https://cdnjs.cloudflare.com/ajax/libs/bootswatch/3.3.7/cerulean/bootstrap.min.css");
                    content = content.replace("{$js_jquery3.1.1}", "http://code.jquery.com/jquery-3.1.1.min.js");
                    content = content.replace("{$bootstrap}", "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css");
                    content = content.replace("{$js_bootstrap}", "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js");

                    webView.loadData(content, "text/html", "UTF-8");
                } else {
                    self.finish();
                }
            }
        });





    }

}
