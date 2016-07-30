package com.sinenco.smartherald.library.SwipeList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sinenco.smartherald.library.TouchListenerTool.TouchListenerManager;
import com.sinenco.smartherald.listviewremovalanimation.BackgroundContainer;

import java.util.ArrayList;

/**
 * Created by JordanLeMagnifique on 02/08/2015.
 */
public abstract class SwipeListAdapter<T> extends StableArrayAdapter<T> implements View.OnClickListener {

    protected static LayoutInflater inflater = null;
    protected Resources res;
    protected T tempValues = null;
    protected int i = 0;
    protected TouchListenerManager mListener;
    protected SwipeItemListListener mSwipeItemListListener;

    protected Activity activity;
    protected ArrayList<T> data;

    public SwipeListAdapter(
            Context context,
            Activity a,
            ArrayList d,
            Resources resLocal,
            int textViewResourceId,
            ListView listView,
            BackgroundContainer backgroundContainer) {
        super(context, textViewResourceId, d);

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSwipeItemListListener = new SwipeItemListListener(context, listView, backgroundContainer, this);
        mListener = new TouchListenerManager(
                mSwipeItemListListener.getOnTouchCallback(),
                mSwipeItemListListener.getOnEndedCallback(),
                mSwipeItemListListener.getOnDownCallback(),
                mSwipeItemListListener.getOnUpCallback(),
                mSwipeItemListListener.getOnCancelCallback(),
                mSwipeItemListListener.getOnMoveCallback()
        );
    }


    public void animateRemoval(View viewToRemove, MotionEvent event) {
        mSwipeItemListListener.animateRemoval(viewToRemove,event);
    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public T getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    protected abstract View getViewOnInflate(int position, View convertView, ViewGroup parent);
    protected abstract Object getViewOnNullConvert(int position, View convertView, ViewGroup parent);

    protected abstract Object getViewOnNotNullConvert(int position, View convertView, ViewGroup parent);

    protected abstract void getViewOnEmptyList(int position, View convertView, ViewGroup parent, Object holder);

    protected abstract void getViewOnFillingList(int position, View convertView, ViewGroup parent, Object holder, SwipeItemListListener swipeItemListListener);

    public View getView(int position, View convertView, ViewGroup parent) {
        //View vi = super.getView(position, convertView, parent);
        View vi = convertView;
        Object holder;

        if (convertView == null) {
            vi = this.getViewOnInflate(position,vi,parent);
            holder = this.getViewOnNullConvert(position, vi, parent);
        } else
            holder = this.getViewOnNotNullConvert(position, vi, parent);


        if (data.size() <= 0) {
            this.getViewOnEmptyList(position, vi, parent, holder);
        } else {
            SwipeItemListListener swipeItemListListener = mSwipeItemListListener.clone();
            TouchListenerManager touchListener = new TouchListenerManager(
                    swipeItemListListener.getOnTouchCallback(),
                    swipeItemListListener.getOnEndedCallback(),
                    swipeItemListListener.getOnDownCallback(),
                    swipeItemListListener.getOnUpCallback(),
                    swipeItemListListener.getOnCancelCallback(),
                    swipeItemListListener.getOnMoveCallback()
            );

            this.getViewOnFillingList(position, vi, parent, holder, swipeItemListListener);

            vi.setOnTouchListener(touchListener);
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        //Log.v("SwipeListAdapter", "=====Row button clicked=====");
    }


}
