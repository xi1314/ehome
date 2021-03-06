package com.zzu.ehome.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;
import com.zzu.ehome.R;
import com.zzu.ehome.utils.CommonUtils;
import com.zzu.ehome.utils.JsonAsyncTaskOnComplete;
import com.zzu.ehome.utils.JsonAsyncTask_Info;
import com.zzu.ehome.utils.RequestMaker;
import com.zzu.ehome.utils.SharePreferenceUtil;
import com.zzu.ehome.utils.ToastUtils;
import com.zzu.ehome.view.ContainsEmojiEditText;
import com.zzu.ehome.view.HeadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zzu on 2016/4/15.
 */
public class AdviceActivity extends BaseActivity {
    private ContainsEmojiEditText editText;
    private Button btn_ok;
    private String userid;
    private RequestMaker requestMaker;
    private final String mPageName = "AdviceActivity";


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_advice);
        userid = SharePreferenceUtil.getInstance(AdviceActivity.this).getUserId();
        requestMaker = RequestMaker.getInstance();
        initViews();
        initEvent();
        if(!CommonUtils.isNotificationEnabled(AdviceActivity.this)){
            showTitleDialog("请打开通知中心");
        }
    }

    public void initViews() {
        editText = (ContainsEmojiEditText) findViewById(R.id.editText);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        setLeftWithTitleViewMethod(R.mipmap.icon_arrow_left, "意见反馈", new HeadView.OnLeftClickListener() {
            @Override
            public void onClick() {
                finishActivity();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    public void initEvent() {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setEnabled(false);
                doUploade();

            }
        });


    }

    private void doUploade() {
       if(!isNetWork){
            showNetWorkErrorDialog();
            return;
        }
        String msg = editText.getText().toString().toString();
        if (TextUtils.isEmpty(msg)) {
            show("请输入内容！");
            btn_ok.setEnabled(true);
            return;
        }
        if (msg.contains("<") || msg.contains(">") || msg.contains("&")) {
            show("不允许输入非法字符！");
            btn_ok.setEnabled(true);
            return;
        }
        requestMaker.FeedBackInsert(userid, msg, new JsonAsyncTask_Info(AdviceActivity.this, true, new JsonAsyncTaskOnComplete() {
            @Override
            public void processJsonObject(Object result) {
                JSONObject mySO = (JSONObject) result;
                try {
                    JSONArray array = mySO.getJSONArray("FeedBackInsert");
                    JSONObject jsonObject = (JSONObject) array.get(0);
                    String msg = jsonObject.getString("MessageCode");
                    if ("0".equals(msg)) {
                        ToastUtils.showMessage(AdviceActivity.this, "提交成功");
                        finishActivity();
                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                if(btn_ok!=null)
                btn_ok.setEnabled(true);
            }
        }));
    }
}
