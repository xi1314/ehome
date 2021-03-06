package com.zzu.ehome.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.zzu.ehome.R;
import com.zzu.ehome.bean.User;
import com.zzu.ehome.db.EHomeDao;
import com.zzu.ehome.db.EHomeDaoImpl;
import com.zzu.ehome.utils.ScreenUtils;
import com.zzu.ehome.utils.SharePreferenceUtil;
import com.zzu.ehome.view.DialogTips;

import static com.zzu.ehome.R.id.llback;


/**
 * Created by Administrator on 2016/9/9.
 */
public class EcgDesActivity extends BaseSimpleActivity implements View.OnClickListener{
    private ImageView ivhome;
    private ImageView        ivback;
    private Button btn_ecg;
    private LinearLayout  llback;
    private EHomeDao dao;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_ecg_layout);
        intiView();
        intiEvnents();
    }
    private void intiView(){
        ivhome=(ImageView)findViewById(R.id.ivhome);
        ViewGroup.LayoutParams para;
        para = ivhome.getLayoutParams();
        dao=new EHomeDaoImpl(EcgDesActivity.this);
        para.width = ScreenUtils.getScreenWidth(EcgDesActivity.this);
        para.height = para.width*1600/750-ScreenUtils.dip2px(EcgDesActivity.this,48);
        ivhome.setLayoutParams(para);
        btn_ecg=(Button)findViewById(R.id.btn_des);

        ivback=(ImageView)findViewById(R.id.ivback);
        llback=(LinearLayout)findViewById(R.id.llback);
    }
    private void intiEvnents(){
        btn_ecg.setOnClickListener(this);

        llback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(!isNetWork){
            showNetWorkErrorDialog();
            return;
        }
        switch (v.getId()){
            case R.id.btn_des:
                if(checkCardNo(SharePreferenceUtil.getInstance(EcgDesActivity.this).getUserId())) {
                    startActivity(new Intent(EcgDesActivity.this,ECGActivity1.class));
                }
                break;
            case R.id.llback:
                finish();
                break;

        }
    }
    /**
     * 如果用户信息不完善，显示提示框
     */
    public void completeInfoTips() {
        DialogTips dialog = new DialogTips(this, "", "用户信息缺失，请先完善信息",
                "去完善", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                startActivity(new Intent(EcgDesActivity.this, PersonalCenterInfo.class));
            }
        });

        dialog.show();
        dialog = null;
    }
    private boolean  checkCardNo(String  userid){
        User dbUser=dao.findUserInfoById(userid);
        if (TextUtils.isEmpty(dbUser.getUserno())) {
            completeInfoTips();
            return false;
        }else{
            return true;
        }
    }
}
