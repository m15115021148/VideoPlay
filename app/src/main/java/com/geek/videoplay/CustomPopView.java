package com.geek.videoplay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/20.
 */

public class CustomPopView extends PopupWindow {
    private Context mContext;
    private List<String> list;//数据
    private LinearLayout container;//整个布局
    private LinearLayout popLayout;//中间布局
    private View view;//视图
    private OnItemListener mCallBack;

    public CustomPopView(Context context, List<String> values, OnItemListener callBack) {
        this.mContext = context;
        this.list = values;
        this.mCallBack = callBack;
        initData();
    }

    /**
     * 创建视图
     */
    private void initData() {
        view = LayoutInflater.from(mContext).inflate(R.layout.custom_pop_down_up, null);
        container = (LinearLayout) view.findViewById(R.id.pop_container);
        popLayout = (LinearLayout) view.findViewById(R.id.pop_layout);
        for (final String str : list) {
            TextView txt = new TextView(mContext);
            txt.setText(str);
            txt.setTextSize(16);
            txt.setTextColor(Color.parseColor("#007AFF"));
            txt.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 15, 0, 15);
            popLayout.addView(txt, params);
            View bg = new View(mContext);
            bg.setBackgroundColor(Color.parseColor("#55cdcdcd"));
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
            params1.setMargins(30, 0, 30, 0);
            popLayout.addView(bg, params1);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack!=null){
                        mCallBack.onClick(str);
                    }
                }
            });
        }
        TextView cancel = (TextView) view.findViewById(R.id.pop_cancle);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xB0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /**
     * 从下往上
     */
    public void showBottom() {
        container.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);
        this.setAnimationStyle(R.style.PupDownToUp);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = popLayout.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        this.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
    }


    public interface OnItemListener{
        void onClick(String values);
    }

}
