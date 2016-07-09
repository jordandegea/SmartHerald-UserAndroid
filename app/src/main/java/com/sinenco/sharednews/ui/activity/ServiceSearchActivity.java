package com.sinenco.sharednews.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.ListView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinenco.sharednews.R;
import com.sinenco.sharednews.ui.adapter.ServicesSearchListAdapter;

import java.util.HashMap;
import java.util.Map;

public class ServiceSearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ServicesSearchListAdapter<ParseObject> adapter;
    private ServiceSearchActivity self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.onActionViewExpanded();

        listView = (ListView) findViewById(R.id.listView);

        self = this;

        adapter = new ServicesSearchListAdapter<ParseObject>(this, new ServicesSearchListAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                // Here we can configure a ParseQuery to our heart's desire.
                String canonicalNameSearch = searchView.getQuery().toString().toLowerCase();
                canonicalNameSearch = canonicalNameSearch.replace(' ', '+');
                ParseQuery query = new ParseQuery("Service");
                query.whereContains("canonicalName", canonicalNameSearch);
                query.orderByDescending("canonicalName");
                return query;
            }
        });

        adapter.setAutoload(false);

        adapter.setTextKey("name");

        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                adapter = new ServicesSearchListAdapter<ParseObject>(self, new ServicesSearchListAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {
                        String canonicalNameSearch = newText.toLowerCase();
                        canonicalNameSearch = canonicalNameSearch.replace(' ', '+');

                        ParseQuery query = new ParseQuery("Service");
                        query.whereContains("canonicalName", canonicalNameSearch);
                        query.orderByDescending("canonicalName");
                        return query;
                    }
                });

                adapter.setAutoload(false);

                adapter.setTextKey("name");

                listView.setAdapter(adapter);

                adapter.loadObjects();
                return true;
            }
        });

    }


    /*****************
     * This function used by adapter
     ****************/
    public void onItemClick(final ParseObject object) {
        final ServiceSearchActivity selfP = this;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceId",object.getObjectId());
        ParseCloud.callFunctionInBackground("subscribe", params,
                new FunctionCallback<Object>() {
                    public void done(Object object, ParseException e) {
                        if (e == null) {
                            Map<String, Object> update_params = new HashMap<String, Object>();
                            update_params.put("installationId", ParseInstallation.getCurrentInstallation().getObjectId());
                            ParseCloud.callFunctionInBackground("update_installation", update_params);
                            selfP.finish();
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(selfP)
                                    .setTitle("Subscription")
                                    .setMessage("An error occured when trying to subscribe. Retry later. ")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });
    }


}
