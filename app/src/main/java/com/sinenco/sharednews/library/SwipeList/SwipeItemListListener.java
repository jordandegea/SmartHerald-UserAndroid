package com.sinenco.sharednews.library.SwipeList;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.sinenco.sharednews.library.TouchListenerTool.TouchListenerCallback;
import com.sinenco.sharednews.listviewremovalanimation.BackgroundContainer;

import java.util.HashMap;

/**
 * Created by JordanLeMagnifique on 23/12/2015.
 */
public class SwipeItemListListener {

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    float mDownX;
    private int mSwipeSlop = -1;

    private Context mContext;

    boolean mSwiping = false;
    boolean mItemPressed = false;
    private ListView mListView;
    private BackgroundContainer mBackgroundContainer;
    private StableArrayAdapter mAdapter;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<>();


    private TouchListenerCallback onTouchCallback;
    private TouchListenerCallback onEndedCallback;
    private TouchListenerCallback onDownCallback;
    private TouchListenerCallback onUpCallback;
    private TouchListenerCallback onCancelCallback;
    private TouchListenerCallback onMoveCallback;

    private TouchListenerCallback onUserClickCallback;

    private TouchListenerCallback onUserRemoveCallback;

    public SwipeItemListListener(Context context, ListView pListView, BackgroundContainer backgroundContainer, StableArrayAdapter adapter) {
        mContext = context;
        mListView = pListView;
        mBackgroundContainer = backgroundContainer;
        mAdapter = adapter;
        initCallbacks();
    }



    public SwipeItemListListener clone() {
        SwipeItemListListener swipeItemListListener = new SwipeItemListListener(
                mContext, mListView, mBackgroundContainer, mAdapter
        );
        return swipeItemListListener;
    }

    public TouchListenerCallback getOnUserRemoveCallback() {
        return onUserRemoveCallback;
    }

    public void setOnUserRemoveCallback(TouchListenerCallback onUserRemoveCallback) {
        this.onUserRemoveCallback = onUserRemoveCallback;
    }

    public TouchListenerCallback getOnUserClickCallback() {
        return onUserClickCallback;
    }

    public void setOnUserClickCallback(TouchListenerCallback onUserClickCallback) {
        this.onUserClickCallback = onUserClickCallback;
    }

    public TouchListenerCallback getOnTouchCallback() {
        return onTouchCallback;
    }

    public TouchListenerCallback getOnEndedCallback() {
        return onEndedCallback;
    }

    public TouchListenerCallback getOnDownCallback() {
        return onDownCallback;
    }

    public TouchListenerCallback getOnUpCallback() {
        return onUpCallback;
    }

    public TouchListenerCallback getOnCancelCallback() {
        return onCancelCallback;
    }

    public TouchListenerCallback getOnMoveCallback() {
        return onMoveCallback;
    }

    private void initCallbacks() {
        /* OnTouch */
        onTouchCallback = new TouchListenerCallback() {
            public boolean call(final View v, MotionEvent event) {
                if (mSwipeSlop < 0) {
                    mSwipeSlop = ViewConfiguration.get(mContext).
                            getScaledTouchSlop();
                }
                return true;
            }
        };

        /* OnEnd */
        onEndedCallback = new TouchListenerCallback() {
            public boolean call(final View v, MotionEvent event) {
                return true;
            }
        };

        /* OnDown */
        onDownCallback = new TouchListenerCallback() {
            public boolean call(final View v, MotionEvent event) {
                if (mItemPressed) {
                    // Multi-item swipes not handled
                    return false;
                }
                mItemPressed = true;
                mDownX = event.getX();
                return true;
            }
        };

        /* OnUp */
        onUpCallback = new TouchListenerCallback() {
            public boolean call(final View v, final MotionEvent event) {
                if (mSwiping) {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    float fractionCovered;
                    float endX;
                    float endAlpha;
                    final boolean remove;
                    if (deltaXAbs > v.getWidth() * 3 / 4) {
                        // Greater than a quarter of the width - animate it out
                        fractionCovered = deltaXAbs / v.getWidth();
                        endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                        endAlpha = 0;
                        remove = true;
                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaXAbs / v.getWidth());
                        endX = 0;
                        endAlpha = 1;
                        remove = false;
                    }
                    // Animate position and alpha of swiped item
                    // NOTE: This is a simplified version of swipe behavior, for the
                    // purposes of this demo about animation. A real version should use
                    // velocity (via the VelocityTracker class) to send the item off or
                    // back at an appropriate speed.
                    long duration = (int) Math.abs((1 - fractionCovered) * SWIPE_DURATION);
                    mListView.setEnabled(false);
                    v.animate().setDuration(duration).
                            alpha(endAlpha).translationX(endX).
                            withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Restore animated values
                                    v.setAlpha(1);
                                    v.setTranslationX(0);
                                    if (remove) {

                                        if (onUserRemoveCallback != null) {
                                            if (!onUserRemoveCallback.call(v, event)) {
                                                animateRemoval(v, event);
                                            }
                                        }else {
                                            animateRemoval(v, event);
                                        }
                                    } else {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mListView.setEnabled(true);
                                    }
                                }
                            });
                } else {
                    if (onUserClickCallback != null) {
                        onUserClickCallback.call(v, event);
                    }
                }
                mItemPressed = false;
                return true;
            }
        };

        /* On Cancel */
        onCancelCallback = new TouchListenerCallback() {
            public boolean call(final View v, MotionEvent event) {
                v.setAlpha(1);
                v.setTranslationX(0);
                mItemPressed = false;
                return true;
            }
        };

        /* OnMove */
        onMoveCallback = new TouchListenerCallback() {
            public boolean call(final View v, MotionEvent event) {
                float x = event.getX() + v.getTranslationX();
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
                        mListView.requestDisallowInterceptTouchEvent(true);
                        mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                    }
                }
                if (mSwiping) {
                    v.setTranslationX(x - mDownX);
                    //v.setAlpha(1 - deltaXAbs / v.getWidth());
                }
                return true;
            }
        };

    }

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    public void animateRemoval(View viewToRemove, MotionEvent event) {

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        for (int i = 0; i < mAdapter.getCount(); ++i) {
            View child = mListView.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = mAdapter.getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }



        // Delete the item from the adapter
        int position = mListView.getPositionForView(viewToRemove);
        mAdapter.remove(mAdapter.getItem(position));

        final ViewTreeObserver observer = mListView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = mListView.getFirstVisiblePosition();
                for (int i = 0; i < mListView.getChildCount(); ++i) {
                    final View child = mListView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = mAdapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + mListView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mBackgroundContainer.hideBackground();
                                    mSwiping = false;
                                    mListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });

    }


}
