package com.sinenco.sharednews.library.SwipeList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JordanLeMagnifique on 02/11/2015.
 */
public class StableArrayAdapter<T> extends ArrayAdapter<T> {

    HashMap<T, Integer> mIdMap = new HashMap<T, Integer>();
    View.OnTouchListener mTouchListener;

    int index = 0;

    public StableArrayAdapter(Context context, int textViewResourceId,
                              ArrayList<T> objects/*, View.OnTouchListener listener*/) {

        super(context, textViewResourceId, objects);
        //mTouchListener = listener;
        for (index = 0; index < objects.size(); ++index) {
            mIdMap.put(objects.get(index), index);
        }
    }


    @Override
    public long getItemId(int position) {
        T item = getItem(position);
        return mIdMap.get(item);
    }

    /*

    @Override
    public boolean hasStableIds() {
        return true;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        addListenerOnGetView(view, convertView);
        return view;
    }

    protected void addListenerOnGetView(View view, View convertView){
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
            view.setOnTouchListener(mTouchListener);
        }

    }
}