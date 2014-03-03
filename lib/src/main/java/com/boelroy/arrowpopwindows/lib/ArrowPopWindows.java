package com.boelroy.arrowpopwindows.lib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static android.widget.PopupWindow.OnDismissListener;
/**
 * Created by boelroy on 14-2-22.
 */
public class ArrowPopWindows extends PopupWindows implements OnDismissListener{
    private Context mContext;

    private ViewGroup mBody;
    private ImageView mArrowUp;
    private ImageView mArrowDown;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;

    private int mVisibleHeight;
    private int mVisibleWidth;
    private int mMeasuredHeight;
    private int mMeasuredWidth;

    private boolean mMeasured;

    private OnViewCreateListener mOnViewCreateListener;
    private OnDismissListener mOnDismissListener;

    public final static int SHOW_LEFT = 0;
    public final static int SHOW_RIGHT = 1;
    public final static int SHOW_TOP = 2;
    public final static int SHOW_BLOW = 3;
    public final static int SHOW_VERTICAL_AUTO = 4;
    public final static int SHOW_HORIZON_AUTO = 5;

    public ArrowPopWindows(Activity activity, int resId, OnViewCreateListener listener) {
        super(activity);
        mContext = activity;
        setRootViewId(R.layout.arrow_layout);

        Rect rect= new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        mVisibleHeight = rect.height();
        mVisibleWidth = rect.width();
        mOnViewCreateListener = listener;
        setBodyView(resId);
    }

    private void setRootViewId(int id){
        mRootView = (ViewGroup) View.inflate(mContext, id, null);
        mBody = (ViewGroup) mRootView.findViewById(R.id.arrow_body);

        mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
        mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
        mArrowLeft = (ImageView) mRootView.findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) mRootView.findViewById(R.id.arrow_right);

        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(mRootView);
    }

    public void setBodyView(int resId){
        ViewGroup v = (ViewGroup)ViewGroup.inflate(mContext, resId, null);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(mOnViewCreateListener != null){
            mOnViewCreateListener.onViewCreate(v);
        }
        mBody.addView(v);
    }

    public void show(View anchor){
        show(anchor, SHOW_VERTICAL_AUTO);
    }

    public void show(View anchor, int direct){

        if(direct > SHOW_HORIZON_AUTO || direct < SHOW_LEFT){
            direct = SHOW_VERTICAL_AUTO;
        }
        View view = hideOtherArrowByDirect(direct);

        preShow();

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0]+anchor.getWidth(), location[1]+anchor.getHeight());
        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(!mMeasured){
            mMeasuredWidth = mRootView.getMeasuredWidth();
            mMeasuredHeight= mRootView.getMeasuredHeight();
            mMeasured = true;
        }

        final int arrowHeight = view.getMeasuredHeight();
        final int arrowWidth = view.getMeasuredWidth();

        int xPos = 0, yPos = 0, reMeasuredWidth = 0, reMeasuredHeight = 0, bodyWidth = 0, bodyHeight = 0;
        View showView = null;
        View hideView = null;
        if(direct == SHOW_BLOW || direct == SHOW_TOP || direct == SHOW_VERTICAL_AUTO){
            xPos = anchorRect.centerX() - mMeasuredWidth / 2;
            reMeasuredWidth = mMeasuredWidth;
            bodyWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
            switch(direct){
                case SHOW_TOP:
                    yPos = anchorRect.top - mMeasuredHeight;
                    reMeasuredHeight = mMeasuredHeight < anchorRect.top ? mMeasuredHeight : anchorRect.top - arrowHeight;
                    showView = mArrowDown;
                    hideView = mArrowUp;
                    break;
                case SHOW_BLOW:
                    yPos = anchorRect.bottom;
                    reMeasuredHeight = mMeasuredHeight < (mVisibleHeight - anchorRect.bottom)?mMeasuredHeight:mVisibleHeight - anchorRect.top;
                    showView = mArrowUp;
                    hideView = mArrowDown;
                    break;
                case SHOW_VERTICAL_AUTO:
                    if(anchorRect.top > mVisibleHeight - anchorRect.bottom){
                        yPos = anchorRect.top - mMeasuredHeight;
                        reMeasuredHeight = mMeasuredHeight < anchorRect.top ? mMeasuredHeight : anchorRect.top - arrowHeight;
                        showView = mArrowDown;
                        hideView = mArrowUp;
                    }else{
                        yPos = anchorRect.bottom;
                        reMeasuredHeight = mMeasuredHeight < (mVisibleHeight - anchorRect.bottom)?mMeasuredHeight:mVisibleHeight - anchorRect.top;
                        showView = mArrowUp;
                        hideView = mArrowDown;
                    }
            }
            bodyHeight = reMeasuredHeight - arrowHeight;
        }else{

        }

        preShow(reMeasuredWidth, reMeasuredHeight);
        mBody.setLayoutParams(new RelativeLayout.LayoutParams(bodyWidth, bodyHeight));

        int arrowPos;
        if(mMeasuredWidth / 2 > anchorRect.centerX()){
            arrowPos  = anchorRect.centerX() - arrowWidth / 2;
        }else if(mMeasuredWidth / 2 > mVisibleWidth - anchorRect.centerX()){
            arrowPos  = mMeasuredWidth - (mVisibleWidth - anchorRect.centerX()) - arrowWidth / 2;
        }
        else{
            arrowPos  = (mMeasuredWidth - arrowWidth) / 2;
        }

        showArrow(showView, hideView, arrowPos);
        setAnimationStyle(mVisibleWidth, anchorRect.centerX(), direct);
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private View hideOtherArrowByDirect(int direct){
        if(direct == SHOW_BLOW || direct == SHOW_TOP || direct == SHOW_VERTICAL_AUTO){
            mArrowLeft.setVisibility(View.GONE);
            mArrowRight.setVisibility(View.GONE);
            if(direct == SHOW_TOP){
                mArrowUp.setVisibility(View.GONE);
                mArrowDown.setVisibility(View.VISIBLE);
                return mArrowDown;
            }else{
                mArrowDown.setVisibility(View.GONE);
                mArrowUp.setVisibility(View.VISIBLE);
                return mArrowUp;
            }
        }else{
            mArrowDown.setVisibility(View.GONE);
            mArrowUp.setVisibility(View.GONE);
            if(direct == SHOW_LEFT){
                mArrowLeft.setVisibility(View.GONE);
                mArrowRight.setVisibility(View.VISIBLE);
            }else{
                mArrowRight.setVisibility(View.GONE);
                mArrowLeft.setVisibility(View.VISIBLE);
            }
        }
        return null;
    }

    private void showArrow(View showArrow, View hideView, int requestedX){
        showArrow.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        param.leftMargin = requestedX;
    }

    private void setAnimationStyle(int screenWidth, int requestedX, int direct){
        switch (direct){
            case SHOW_BLOW:
                mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
                break;
            case SHOW_TOP:
                mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
                break;
            case SHOW_VERTICAL_AUTO:

        }
    }

    public void setOnDismissListener(OnDismissListener listener){
        mOnDismissListener = listener;
    }

    public void setOnViewCreateListener(OnViewCreateListener listener){
        mOnViewCreateListener = listener;
    }

    @Override
    public void onDismiss() {
        if(mOnDismissListener != null){
            mOnDismissListener.onDismiss();
        }
    }

    public interface OnViewCreateListener{
        public void onViewCreate(ViewGroup viewGroup);
    }

    public interface OnDismissListener{
        public void onDismiss();
    }

}
