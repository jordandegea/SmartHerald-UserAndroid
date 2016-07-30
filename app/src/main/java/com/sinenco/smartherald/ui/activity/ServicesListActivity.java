package com.sinenco.smartherald.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.sinenco.smartherald.R;
import com.sinenco.smartherald.ui.adapter.ServicesListAdapter;

import java.util.HashMap;
import java.util.Map;

public class ServicesListActivity extends AppCompatActivity {

    private static final int PARSE_LOGIN_REQUEST_CODE = 1;

    private ServicesListAdapter<ParseObject> adapter ;
    private ServicesListActivity self;
    private LoadContentTask contentLoader ;
    @Override
    protected void onStart() {
        super.onStart();
        fetchUser();
    }

    private boolean fetchUser(){

        //Log.d("ServicesListActivity", "fetch User");
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            try {
                currentUser.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        }else {
            startLogIn();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        self = this ;

        setContentView(R.layout.activity_services_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        adapter = new ServicesListAdapter<ParseObject>(self, new ServicesListAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("Subscription");
                query.whereEqualTo("user", currentUser);
                query.include("service");
                return query;
            }
        });
        adapter.setAutoload(false);


        adapter.setTextKey("name");

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        if ( fetchUser() ) {
            loadContent();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchServiceIntent();
            }
        });


    }

    private void loadContent(){
        if ( contentLoader != null){
            contentLoader.cancel(true);
        }

        contentLoader = new LoadContentTask();
        contentLoader.execute();
    }


    private void startLogIn(){
        ParseLoginBuilder builder = new ParseLoginBuilder(this);
        startActivityForResult(builder.build(), PARSE_LOGIN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == PARSE_LOGIN_REQUEST_CODE) {
            fetchUser();
            loadContent();
            Map<String, Object> update_params = new HashMap<String, Object>();
            update_params.put("installationId", ParseInstallation.getCurrentInstallation().getObjectId());
            ParseCloud.callFunctionInBackground("update_installation", update_params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ParseUser.logOut();
            fetchUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*****************
     * This function used by adapter
     ****************/
    public void onItemClick(ParseObject object) {
        startServiceIntent(object);
    }


    private void startServiceIntent(ParseObject object) {

        Intent intent;
        intent = new Intent(ServicesListActivity.this, MessagesListActivity.class);
        intent.putExtra("service", object.getParseObject("service").getObjectId());
        startActivityForResult(intent, 2);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private void startSearchServiceIntent(){

        Intent intent;
        intent = new Intent(ServicesListActivity.this, ServiceSearchActivity.class);
        startActivityForResult(intent, 1);
    }



    private class LoadContentTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

           adapter.loadObjects();
            return "OK";

        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
