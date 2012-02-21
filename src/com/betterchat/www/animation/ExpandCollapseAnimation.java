 package com.betterchat.www.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Class for handling collapse and expand animations.
 * @author Esben Gaarsmand
 *
 */
public class ExpandCollapseAnimation extends Animation {
    private View mAnimatedView;
    private int mEndHeight;
	private int mStartVisibility;

	/**
	 * Initializes expand collapse animation. If the passed view is invisible/gone the animation will be a drop down, 
	 * if it is visible the animation will be collapse from bottom
	 * @param view The view to animate
	 * @param duration
	 */ 
    public ExpandCollapseAnimation(View view, int duration) {
        setDuration(duration);
        mAnimatedView = view;
        mEndHeight = mAnimatedView.getLayoutParams().height;
        mStartVisibility = mAnimatedView.getVisibility();
        if(mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {
        	mAnimatedView.setVisibility(View.VISIBLE);
        	mAnimatedView.getLayoutParams().height = 0;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
        	if(mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {
        		mAnimatedView.getLayoutParams().height = (int) (mEndHeight * interpolatedTime);
        	} else {
        		mAnimatedView.getLayoutParams().height = mEndHeight - (int) (mEndHeight * interpolatedTime);
        	}
        	mAnimatedView.requestLayout();
        } else {
        	if(mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {
        		mAnimatedView.getLayoutParams().height = mEndHeight;
        		mAnimatedView.requestLayout();
        	} else {
        		mAnimatedView.getLayoutParams().height = 0;
        		mAnimatedView.setVisibility(View.GONE);
        		mAnimatedView.requestLayout();
        		mAnimatedView.getLayoutParams().height = mEndHeight;
        	}
        }
    }
}
