package com.sinenco.sharednews.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.sinenco.sharednews.R;
import com.sinenco.sharednews.ui.activity.MessagesListActivity;

/**
 * Created by JordanLeMagnifique on 22/03/2016.
 */
public class MessagesListAdapter<T extends ParseObject> extends ParseQueryAdapter<T> {

    private Activity activity;

    public MessagesListAdapter(Activity mActivity, QueryFactory<T> queryFactory) {
        super(mActivity, queryFactory);
        activity =  mActivity;
    }



    @Override
    public View getItemView(T object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.messages_list_item, null);
        }

        // Take advantage of ParseQueryAdapter's getItemView logic for
        // populating the main TextView/ImageView.
        // The IDs in your custom layout must match what ParseQueryAdapter expects
        // if it will be populating a TextView or ImageView for you.
        super.getItemView(object, v, parent);

        // Do additional configuration before returning the View.
        TextView nameView = (TextView) v.findViewById(R.id.name);
        nameView.setText(object.getString("summary"));
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
            MessagesListActivity sct = (MessagesListActivity)activity;
            sct.onItemClick(object);
        }
    }
}
