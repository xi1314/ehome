package com.zzu.ehome.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.zzu.ehome.R;
import com.zzu.ehome.adapter.DoctorListAdapter;
import com.zzu.ehome.bean.DoctorBeanDes;
import com.zzu.ehome.bean.DortorlIst;
import com.zzu.ehome.utils.CommonUtils;
import com.zzu.ehome.utils.JsonAsyncTaskOnComplete;
import com.zzu.ehome.utils.JsonAsyncTask_Info;
import com.zzu.ehome.utils.JsonTools;
import com.zzu.ehome.utils.RequestMaker;
import com.zzu.ehome.view.HeadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mersens on 2016/8/9.
 */
public class DoctorListActivity extends BaseActivity {
    private ListView listView;
    private DoctorListAdapter adapter;
    private RequestMaker requestMaker;
    private String hosid, depid, DepartmentName;
    List<DoctorBeanDes> mList;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestMaker = RequestMaker.getInstance();
        setContentView(R.layout.layout_doctor_list);
        hosid = this.getIntent().getStringExtra("HospitalID");
        depid = this.getIntent().getStringExtra("DepartmentID");
        DepartmentName = this.getIntent().getStringExtra("DepartmentName");
        initViews();
        initEvent();
       if(!isNetWork){
            showNetWorkErrorDialog();
            return;
        }
        initDatas();
        if(!CommonUtils.isNotificationEnabled(DoctorListActivity.this)){
            showTitleDialog("请打开通知中心");
        }
    }

    public void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        setLeftWithTitleViewMethod(R.mipmap.icon_arrow_left, DepartmentName, new HeadView.OnLeftClickListener() {
            @Override
            public void onClick() {
                finishActivity();
            }
        });

    }

    public void initEvent() {

    }

    public void initDatas() {
        requestMaker.DepertDoctorByTopmd(hosid, depid, new JsonAsyncTask_Info(
                DoctorListActivity.this, true, new JsonAsyncTaskOnComplete() {
            @Override
            public void processJsonObject(Object result) {
                String str = result.toString();
                Log.d("DepertDoctorByTopmd", "processJsonObject() called with: result = [" + result.toString() + "]");

                try {
                    JSONObject mySO = (JSONObject) result;
                    JSONArray array = mySO
                            .getJSONArray("DepertDoctorByTopmd");
                    if(listView!=null) {
                        if (array.getJSONObject(0).has("MessageCode")) {
//                        Toast.makeText(DoctorListActivity.this, array.getJSONObject(0).getString("MessageContent").toString(),
//                                Toast.LENGTH_SHORT).show();
                            show(array.getJSONObject(0).getString("MessageContent"));
                        } else {
                            DortorlIst date = JsonTools.getData(result.toString(), DortorlIst.class);
                            mList = date.getData();
                            adapter = new DoctorListAdapter(DoctorListActivity.this, mList, hosid, depid, DepartmentName);
                            listView.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        }));

    }

}
