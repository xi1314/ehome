package com.zzu.ehome.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zzu.ehome.R;
/**
 * Created by Administrator on 2017/1/5.
 */

public class PlatmDialogEnsureCancelView extends MyDialogView {
    private TextView  mTextMsg, mTextEnsure, mTextCancel;
    private OnClickListener mClickListenerEnsure, mClickListenerCancle;

    public PlatmDialogEnsureCancelView(Context context) {
        super(context);
        initView();
    }

    public PlatmDialogEnsureCancelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PlatmDialogEnsureCancelView(Context context, AttributeSet attrs, Boolean isLong) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        removeAllViews();
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.dialog_default_ensure_platm, this, false);
        mTextMsg = (TextView) layout.findViewById(R.id.dialog_default_click_text_msg);
        mTextEnsure = (TextView) layout.findViewById(R.id.dialog_default_click_ensure);
        mTextCancel = (TextView) layout.findViewById(R.id.dialog_default_click_cancel);
        mTextEnsure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mClickListenerEnsure != null) {
                    mClickListenerEnsure.onClick(v);
                }
                if (mExitDialog != null) {
                    mExitDialog.exitDialog();
                }
            }
        });

        mTextCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mClickListenerCancle != null) {
                    mClickListenerCancle.onClick(v);
                }
                if (mExitDialog != null) {
                    mExitDialog.exitDialog();
                }
            }
        });

        this.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public DialogInterface.OnClickListener getPositiveButtonListener() {
        return null;
    }

    /**
     * 确定按钮点击事件
     */
    public PlatmDialogEnsureCancelView setOnClickListenerEnsure(OnClickListener clickListener) {
        mClickListenerEnsure = clickListener;

        return this;

    }

    /**
     * 取消按钮点击事件
     */
    public PlatmDialogEnsureCancelView setOnClickListenerCancle(OnClickListener clickListener) {
        mClickListenerCancle = clickListener;

        return this;
    }

    /**
     * 确定按钮点击事件
     */
    public PlatmDialogEnsureCancelView setDialogMsg(String title, String msg, String ensure) {

        if (!TextUtils.isEmpty(msg)) {
            mTextMsg.setText(msg);
        }
        if (!TextUtils.isEmpty(ensure)) {
            mTextEnsure.setText(ensure);
        }
        return this;
    }

    @Override
    public DialogInterface.OnClickListener getNegativeButtonListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                dialog.cancel();
            }
        };
    }

}