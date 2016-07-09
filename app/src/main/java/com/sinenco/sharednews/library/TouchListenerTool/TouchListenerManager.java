package com.sinenco.sharednews.library.TouchListenerTool;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by JordanLeMagnifique on 23/12/2015.
 */
public class TouchListenerManager implements View.OnTouchListener {


    private TouchListenerCallback onTouchCallback;
    private TouchListenerCallback onEndedCallback;
    private TouchListenerCallback onDownCallback;
    private TouchListenerCallback onUpCallback;
    private TouchListenerCallback onCancelCallback;
    private TouchListenerCallback onMoveCallback;

    public TouchListenerManager(
            TouchListenerCallback touchCallback,
            TouchListenerCallback endedCallback,
            TouchListenerCallback downCallback,
            TouchListenerCallback upCallback,
            TouchListenerCallback cancelCallback,
            TouchListenerCallback moveCallback
    ) {
        this.onTouchCallback = touchCallback;
        this.onEndedCallback = endedCallback;
        this.onDownCallback = downCallback;
        this.onUpCallback = upCallback;
        this.onCancelCallback = cancelCallback;
        this.onMoveCallback = moveCallback;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (onTouchCallback != null && !onTouchCallback.call(v, event)) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onDownCallback != null) {
                    onDownCallback.call(v, event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (onCancelCallback != null)
                    onCancelCallback.call(v, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (onMoveCallback != null)
                    onMoveCallback.call(v, event);
                break;
            case MotionEvent.ACTION_UP:
                if (onUpCallback != null)
                    onUpCallback.call(v, event);
                break;
            default:
                return false;
        }
        if (onEndedCallback != null)
            onEndedCallback.call(v, event);
        return true;
    }
}
