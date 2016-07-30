package com.sinenco.smartherald.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.sinenco.smartherald.R;
import com.sinenco.smartherald.ui.activity.ServiceSearchActivity;

/**
 * Created by JordanLeMagnifique on 22/03/2016.
 */
public class ServicesSearchListAdapter<T extends ParseObject> extends ParseQueryAdapter<T> {
    private Activity activity;


    public ServicesSearchListAdapter(Activity mActivity, QueryFactory<T> queryFactory) {
        super(mActivity, queryFactory);
        activity = mActivity;
    }




    @Override
    public View getItemView(T object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.service_search_list_item, null);
        }

        // Take advantage of ParseQueryAdapter's getItemView logic for
        // populating the main TextView/ImageView.
        // The IDs in your custom layout must match what ParseQueryAdapter expects
        // if it will be populating a TextView or ImageView for you.
        super.getItemView(object, v, parent);

        // Do additional configuration before returning the View.
        TextView nameView = (TextView) v.findViewById(R.id.name);
        nameView.setText(object.getString("name"));
        v.setOnClickListener(new OnItemClickListener( object ));
        return v;
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        T object ;

        OnItemClickListener(T mObject){
            object= mObject;
        }

        @Override
        public void onClick(View arg0) {
            ServiceSearchActivity sct = (ServiceSearchActivity)activity;
            sct.onItemClick(object);
        }
    }
}
