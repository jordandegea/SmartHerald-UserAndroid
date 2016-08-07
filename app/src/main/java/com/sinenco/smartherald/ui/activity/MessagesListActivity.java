package com.sinenco.smartherald.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinenco.smartherald.R;
import com.sinenco.smartherald.ui.adapter.MessagesListAdapter;

public class MessagesListActivity extends AppCompatActivity {


    private ParseObject serviceObject = null ;
    private MessagesListAdapter<ParseObject> adapter;
    private String serviceId ;
    private MessagesListActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        Intent intent = getIntent();

        if (intent == null) {
            this.finish();
        }

        serviceId = intent.getStringExtra("service");
        if ( serviceId == null){
            this.finish();
        }

        this.setTitle("Loading...");

        setContentView(R.layout.activity_messages_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //TODO : Hide for the moment
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : Afficher tous les messages
            }
        });
*/
        new onCreateTask().execute();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            // LOGIN OK
        }else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*****************
     * This function used by adapter
     ****************/
    public void onItemClick(ParseObject object) {
        startMessageIntent(object);
    }


    private void startMessageIntent(ParseObject object) {

        Intent intent;
        intent = new Intent(MessagesListActivity.this, MessageActivity.class);
        intent.putExtra("message", object.getObjectId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    private class onCreateTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Service");

            query.getInBackground(serviceId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        serviceObject = object;
                        setTitle(serviceObject.getString("name"));
                        adapter.loadObjects();
                    } else {
                        finish();
                    }
                }
            });


            adapter = new MessagesListAdapter<ParseObject>(self, new MessagesListAdapter.QueryFactory<ParseObject>() {
                public ParseQuery<ParseObject> create() {
                    // Here we can configure a ParseQuery to our heart's desire.
                    ParseQuery query = new ParseQuery("Message");
                    query.whereEqualTo("service", serviceObject);
                    query.whereEqualTo("sent", true);
                    query.orderByDescending("updatedAt");
                    return query;
                }
            });
            adapter.setAutoload(false);

            adapter.setTextKey("summary");

            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
        }
    }
}
