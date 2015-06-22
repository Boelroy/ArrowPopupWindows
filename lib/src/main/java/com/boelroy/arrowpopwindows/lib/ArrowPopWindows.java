package com.boelroy.arrowpopwindows.lib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
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
        mRootView = View.inflate(mContext, id, null);
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

        //get arrow to show
        View view = hideOtherArrowByDirect(direct);

        preShow();

        Rect anchorRect = getAnchorRectOnScreen(anchor);

        measureRootView();

        //it must be call after measureRootView
        final int arrowHeight = view.getMeasuredHeight();
        final int arrowWidth = view.getMeasuredWidth();

        int xPos = 0, yPos = 0, reMeasuredWidth = 0, reMeasuredHeight = 0, bodyWidth = 0, bodyHeight = 0;
        View showView = null, hideView = null;
        if(direct == SHOW_BLOW || direct == SHOW_TOP || direct == SHOW_VERTICAL_AUTO){
            xPos = anchorRect.centerX() - mMeasuredWidth / 2;
            reMeasuredWidth = mMeasuredWidth;
            bodyWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
            if(direct == SHOW_VERTICAL_AUTO){
                if(anchorRect.top > mVisibleHeight - anchorRect.bottom)
                    direct = SHOW_TOP;
                else
                    direct = SHOW_BLOW;
            }

            switch(direct){
                case SHOW_TOP:
                    yPos = anchorRect.top - mMeasuredHeight;
                    reMeasuredHeight = mMeasuredHeight < anchorRect.top ? mMeasuredHeight : anchorRect.top - 2 * arrowHeight;
                    showView = mArrowDown;
                    hideView = mArrowUp;
                    break;
                case SHOW_BLOW:
                    yPos = anchorRect.bottom;
                    reMeasuredHeight = mMeasuredHeight < (mVisibleHeight - anchorRect.bottom)?mMeasuredHeight:mVisibleHeight - anchorRect.top;
                    showView = mArrowUp;
                    hideView = mArrowDown;
                    break;
            }

            bodyHeight = reMeasuredHeight - 2 * arrowHeight;
        }else{
            yPos = anchorRect.centerY() - mMeasuredHeight / 2;
            reMeasuredHeight = mMeasuredHeight;
            bodyHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
            if (direct == SHOW_HORIZON_AUTO) {
                if (anchorRect.left > mVisibleWidth - anchorRect.right)
                    direct = SHOW_LEFT;
                else
                    direct = SHOW_RIGHT;
            }

            switch (direct) {
                case SHOW_LEFT:
                    xPos = anchorRect.left - mMeasuredWidth;
                    reMeasuredWidth = mMeasuredWidth < anchorRect.top ? mMeasuredWidth : anchorRect.left - 2 * arrowWidth;
                    showView = mArrowRight;
                    hideView = mArrowLeft;
                    break;
                case SHOW_RIGHT:
                    xPos = anchorRect.right;
                    reMeasuredWidth = mMeasuredWidth < (mVisibleWidth - anchorRect.right) ? mMeasuredWidth: mVisibleWidth - anchorRect.left;
                    showView = mArrowLeft;
                    hideView = mArrowRight;
                    break;
            }
            bodyWidth = reMeasuredWidth - 2 * arrowWidth;
        }

        preShow(reMeasuredWidth, reMeasuredHeight);

        //set body height and width
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mBody.getLayoutParams();
        lp.height = bodyHeight;
        lp.width = bodyWidth;
        mBody.setLayoutParams(lp);

        //set the arrow position
        int arrowPos = getArrowOffset(anchorRect, arrowWidth, arrowHeight, direct);
        showArrow(showView, hideView, arrowPos, direct);
        setAnimationStyle(mVisibleWidth, anchorRect.centerX(), direct);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private View hideOtherArrowByDirect(int direct){
        if(direct == SHOW_BLOW || direct == SHOW_TOP || direct == SHOW_VERTICAL_AUTO){
            mArrowLeft.setVisibility(View.GONE);
            mArrowRight.setVisibility(View.GONE);
            return mArrowUp;
        }else{
            mArrowDown.setVisibility(View.GONE);
            mArrowUp.setVisibility(View.GONE);
            if(direct == SHOW_LEFT){
                mArrowLeft.setVisibility(View.GONE);
                mArrowRight.setVisibility(View.VISIBLE);
                return mArrowRight;
            }else{
                mArrowRight.setVisibility(View.GONE);
                mArrowLeft.setVisibility(View.VISIBLE);
                return mArrowLeft;
            }
        }
    }

    /**
     * get the view's left and top on the screen.
     * @param anchor the view we want to get the position
     * @return  Rect that contains the left, top of the view
     */
    private Rect getAnchorRectOnScreen(View anchor){
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1],
                location[0]+anchor.getWidth(), location[1]+anchor.getHeight());
        return anchorRect;
    }

    /**
     *
     */
    private void measureRootView(){
        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if(!mMeasured){
            mMeasuredWidth = mRootView.getMeasuredWidth();
            mMeasuredHeight= mRootView.getMeasuredHeight();
            mMeasured = true;
        }

    }

    private int getArrowOffset(Rect anchorRect, int arrowWidth, int arrowHeihgt, int direct){
        int arrowPos, mMeasuredPos, anchorCenter, arrow;
        if(direct == SHOW_TOP || direct == SHOW_BLOW){
            mMeasuredPos = mMeasuredWidth;
            anchorCenter = anchorRect.centerX();
            arrow = arrowWidth;
        } else {
            mMeasuredPos = mMeasuredHeight;
            anchorCenter = anchorRect.centerY();
            arrow = arrowHeihgt;
        }
        if(mMeasuredPos / 2 > anchorCenter){
            arrowPos  = anchorCenter - arrow / 2;
        }else if(mMeasuredPos / 2 > mVisibleWidth - anchorCenter){
            arrowPos  = mMeasuredPos - (mVisibleWidth - anchorCenter) - arrow / 2;
        }
        else{
            arrowPos  = (mMeasuredPos - arrow) / 2;
        }

        return arrowPos;
    }

    private void showArrow(View showArrow, View hideView, int requestedX, int direct){
        showArrow.setVisibility(View.VISIBLE);
        hideView.setVisibility(View.INVISIBLE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        if(direct == SHOW_TOP || direct == SHOW_BLOW)
            param.leftMargin = requestedX;
        else
            param.topMargin = requestedX;
    }

    private void setAnimationStyle(int screenWidth, int requestedX, int direct){
        switch (direct){
            case SHOW_BLOW:
                mWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
                break;
            case SHOW_TOP:
                mWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
                break;
            case SHOW_LEFT:
                mWindow.setAnimationStyle(R.style.Animations_PopUpRightMenu_Center);
                break;
            case SHOW_RIGHT:
                mWindow.setAnimationStyle(R.style.Animations_PopUpLeftMenu_Center);
                break;
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
