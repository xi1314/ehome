package com.zzu.ehome.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzu.ehome.R;
import com.zzu.ehome.activity.SelectDateAndTime;
import com.zzu.ehome.activity.SuggarActivity;
import com.zzu.ehome.activity.SupperBaseActivity;
import com.zzu.ehome.application.Constants;
import com.zzu.ehome.bean.BloodSuggarBean;
import com.zzu.ehome.bean.BloodSuggarDa;
import com.zzu.ehome.bean.RefreshEvent;
import com.zzu.ehome.bean.User;
import com.zzu.ehome.db.EHomeDao;
import com.zzu.ehome.db.EHomeDaoImpl;
import com.zzu.ehome.reciver.EventType;
import com.zzu.ehome.reciver.RxBus;
import com.zzu.ehome.utils.CommonUtils;
import com.zzu.ehome.utils.JsonAsyncTaskOnComplete;
import com.zzu.ehome.utils.JsonAsyncTask_Info;
import com.zzu.ehome.utils.JsonTools;
import com.zzu.ehome.utils.OnSelectItemListener;
import com.zzu.ehome.utils.RequestMaker;
import com.zzu.ehome.utils.SharePreferenceUtil;
import com.zzu.ehome.utils.ToastUtils;
import com.zzu.ehome.view.ScaleMarkView;
import com.zzu.ehome.view.ScaleMarkView.OnValueChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by zzu on 2016/4/12.
 * 血糖
 */
public class BloodSugarFragment extends BaseFragment {
    private View view;
    private ScaleMarkView scaleMark;
    private TextView mtvmm, tvcltime, mtvmg, TVmm, TVmg;
    private RelativeLayout rlchecktime;
    private CheckBox cbgl;
    float tvmm;
    float tvmg;
    BigDecimal b;
    private TextView tvcltimepart;
    public static final int ADD_TIMES = 0x35;
    private RelativeLayout rltimes;
    private Button btnSuggar;
    private RequestMaker requestMaker;
    private String userid, mBloodSugarValue, mMonitorPoint, mTime,cardNo;
    //1 mm 2 mg
    private int type;
    float value;
    private ImageView ivbloods;
    private TextView tvlv, tvxcheck;
    public static int p = -1;
    private EHomeDao dao;
    private User dbUser;
    private OnSelectItemListener mListener;
    private SupperBaseActivity activity;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_bloodsugar, null);
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        dao = new EHomeDaoImpl(getActivity());
        dbUser=dao.findUserInfoById(userid);
        cardNo=dbUser.getUserno();
        requestMaker = RequestMaker.getInstance();
        initViews();
        initEvents();

        if (!CommonUtils.isNotificationEnabled(getActivity())) {
            activity.showTitleDialog("请打开通知中心");
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(SupperBaseActivity)context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnArticleSelectedListener");
        }
    }

    public void initViews() {

        scaleMark = (ScaleMarkView) view.findViewById(R.id.scale_mark1);
        mtvmm = (TextView) view.findViewById(R.id.blood_num);
        cbgl = (CheckBox) view.findViewById(R.id.checkBox);
        rlchecktime = (RelativeLayout) view.findViewById(R.id.rl_blood_sugar_time);
        tvcltime = (TextView) view.findViewById(R.id.tv_cl_time);
        mtvmg = (TextView) view.findViewById(R.id.tvmg);
        scaleMark.setZOrderOnTop(true);
        tvmm = 5;
        scaleMark.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        scaleMark.setPreMarkValue(1f);
        scaleMark.setDefaultValue(tvmm);
        scaleMark.setMinValue(0);
        scaleMark.setMaxValue(33);
        mtvmm.setVisibility(View.VISIBLE);
        mtvmg.setVisibility(View.INVISIBLE);
        TVmm = (TextView) view.findViewById(R.id.tvmol);
        TVmg = (TextView) view.findViewById(R.id.tvmgdl);
        tvcltimepart = (TextView) view.findViewById(R.id.tv_cl_time_part);
        rltimes = (RelativeLayout) view.findViewById(R.id.rl_times);
        btnSuggar = (Button) view.findViewById(R.id.btn_savesuggar);
        ivbloods = (ImageView) view.findViewById(R.id.iv_bloodsuggar);
        tvlv = (TextView) view.findViewById(R.id.tv_bloodlv);
        tvlv.setTextColor(getResources().getColor(R.color.actionbar_color));
        tvxcheck = (TextView) view.findViewById(R.id.tv_xuetangcheck);
        mMonitorPoint = "空腹";

    }

    public void initEvents() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        tvcltime.setText(df.format(new Date()));
        mTime = df.format(new Date());
        b = new BigDecimal(tvmm);
        tvmm = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        mtvmm.setVisibility(View.VISIBLE);
        TVmm.setVisibility(View.VISIBLE);
        mtvmg.setVisibility(View.INVISIBLE);
        TVmg.setVisibility(View.INVISIBLE);

        scaleMark.setOnValueChangedListener(new OnValueChangedListener() {

            @Override
            public void onValueChanged(ScaleMarkView view, BigDecimal oldValue,
                                       BigDecimal newValue) {
                if (type == 1) {
                    tvmm = newValue.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                    tvmg = tvmm * 18;

                } else {
                    tvmg = newValue.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                    tvmm = tvmg / 18;

                }
                value = tvmm;
                mtvmm.setText(tvmm + "");
                mtvmg.setText(tvmg + "");

                if (oldValue.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue() != 0) {

                    checkSuager(value);

                }


            }
        });

        rlchecktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.isNetWork){
                    activity.showNetWorkErrorDialog();
                    return;
                }
                Intent intenttime = new Intent(getActivity(), SelectDateAndTime.class);
                startActivityForResult(intenttime, Constants.ADDTTIME);

            }
        });
        cbgl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mtvmm.setVisibility(View.INVISIBLE);
                    TVmm.setVisibility(View.INVISIBLE);
                    type = 2;
                    TVmg.setVisibility(View.VISIBLE);
                    mtvmg.setVisibility(View.VISIBLE);


                    tvmg = tvmm * 18;
                    if (tvmg > 594) {
                        tvmg = 7 * 18;
                    }
                    scaleMark.setValue(tvmg);
                    scaleMark.setMaxValue(594);

                } else {
                    mtvmm.setVisibility(View.VISIBLE);
                    TVmm.setVisibility(View.VISIBLE);
                    mtvmg.setVisibility(View.INVISIBLE);
                    TVmg.setVisibility(View.INVISIBLE);
                    tvmm = tvmg / 18;
                    if (tvmm > 33) {
                        tvmm = 7;
                    }
                    type = 1;
                    scaleMark.setValue(tvmm);
                    scaleMark.setMaxValue(33);
                }
            }
        });
        rltimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.isNetWork){
                    activity.showNetWorkErrorDialog();
                    return;
                }
                Intent intenttimes = new Intent(getActivity(), SuggarActivity.class);
                startActivityForResult(intenttimes, ADD_TIMES);
            }
        });


        //监听订阅事件

        btnSuggar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.isNetWork){
                    activity.showNetWorkErrorDialog();

                }else {
                    if (cbgl.isChecked()) {
                        mBloodSugarValue = tvmg + "";
                        type = 2;
                    } else {
                        mBloodSugarValue = tvmm + "";
                        type = 1;
                    }
                    if (tvlv.getText().equals("请滑动刻度尺")) {
                        ToastUtils.showMessage(getActivity(), "请滑动刻度尺");
                        return;
                    }
                    if (TextUtils.isEmpty(mMonitorPoint)) {
                        ToastUtils.showMessage(getActivity(), "请选择测量时段！");
                        return;
                    }
                    btnSuggar.setEnabled(false);
                    if (mMonitorPoint.contains("空腹")) {
                        addBloodSugar(0);
                    } else if (mMonitorPoint.contains("餐后")) {
                        addBloodSugar(1);
                    } else {
                        addBloodSugar(2);
                    }
                }


            }
        });
        tvxcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.isNetWork){
                    activity.showNetWorkErrorDialog();
                    return;
                }
                if (cbgl.isChecked()) {
                    cbgl.setChecked(false);
                } else {
                    cbgl.setChecked(true);
                }
            }
        });
        getBloodSuggar();
    }

    /**
     * 0,空腹；1，餐后；2，随机；
     * @param typeBlood
     */
    private void addBloodSugar(int typeBlood){
        requestMaker.BloodSugarInsert(cardNo,userid, mBloodSugarValue, typeBlood+"", mTime, String.valueOf(type), new JsonAsyncTask_Info(getActivity(), true, new JsonAsyncTaskOnComplete() {
            public void processJsonObject(Object result) {
                String value = result.toString();
                btnSuggar.setEnabled(true);

                try {
                    JSONObject mySO = (JSONObject) result;
                    JSONArray array = mySO.getJSONArray("BloodSugarInsert");
                    if (array.getJSONObject(0).getString("MessageCode")
                            .equals("0")) {
                        RxBus.getInstance().send(new EventType(Constants.HealthData));
                        ToastUtils.showMessage(getActivity(), "保存成功!");
                        if (p == -1) {
//                            EventBus.getDefault().post(new RefreshEvent(getResources().getInteger(R.integer.refresh_manager_data)));
                            EventBus.getDefault().post(new RefreshEvent(getResources().getInteger(R.integer.refresh_suggar)));
                            getActivity().finish();
                        } else {
                            if (p <= 2) {

                                mListener.selectItem(p + 1);
                            } else {
                                Intent intentD = new Intent();
                                intentD.setAction("action.DateOrFile");
                                intentD.putExtra("msgContent", "Date");
                                getActivity().sendBroadcast(intentD);

//                                EventBus.getDefault().post(new RefreshEvent(getResources().getInteger(R.integer.refresh_manager_data)));
                                getActivity().finish();
                            }
                        }
                    } else {
                        ToastUtils.showMessage(getActivity(), array.getJSONObject(0).getString("MessageContent"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                if(btnSuggar!=null)
                btnSuggar.setEnabled(true);
            }

        }));
    }

    private void checkSuager(float value) {
        if (mMonitorPoint.contains("餐后")) {
            if (Float.compare(value, 7.6F) >= 0 && Float.compare(value, 11.1F) <= 0) {
                changeImgeView(ivbloods, tvlv, 1);
            } else if (Float.compare(value, 11.1F) > 0) {
                changeImgeView(ivbloods, tvlv, 2);
            } else {
                changeImgeView(ivbloods, tvlv, 0);
            }

        } else if(mMonitorPoint.contains("空腹")){
            if (Float.compare(value, 3.9F) < 0) {
                changeImgeView(ivbloods, tvlv, 0);
            } else if (Float.compare(value, 3.9F) >= 0 && Float.compare(value, 6.1F) <= 0) {
                changeImgeView(ivbloods, tvlv, 1);
            } else {
                changeImgeView(ivbloods, tvlv, 2);
            }
        }
        else {
            if (Float.compare(value, 3.9F) < 0) {
                changeImgeView(ivbloods, tvlv, 0);
            } else if (Float.compare(value, 3.9F) >= 0 && Float.compare(value, 11.1F) <= 0) {
                changeImgeView(ivbloods, tvlv, 1);
            } else {
                changeImgeView(ivbloods, tvlv, 2);
            }

        }


    }

    private void getBloodSuggar() {
        requestMaker.HealthDataInquirywWithPageType(userid,cardNo, "1", "1", "BloodSugar", new JsonAsyncTask_Info(getActivity(), true, new JsonAsyncTaskOnComplete() {
            @Override
            public void processJsonObject(Object result) {

                try {
                    JSONObject mySO = (JSONObject) result;
                    String resultValue = result.toString();
                    JSONArray array = mySO
                            .getJSONArray("HealthDataInquiryWithPage");
                    if (array.getJSONObject(0).has("MessageCode")) {
                        tvmm = 7;
                        value = tvmm;
                        mtvmm.setText(tvmm + "");
                        type = 1;
                    } else {

                        BloodSuggarDa date = JsonTools.getData(result.toString(), BloodSuggarDa.class);
                        List<BloodSuggarBean> list = date.getData();

                        if(Integer.valueOf(list.get(0).getHoursAfterMeal())==0){
                            mMonitorPoint = "空腹血糖";
                        }else if(Integer.valueOf(list.get(0).getHoursAfterMeal())==1){
                            mMonitorPoint = "餐后血糖";
                        }else{
                            mMonitorPoint = "随机血糖";
                        }
                        tvcltimepart.setText(mMonitorPoint);
                        type = Integer.valueOf(list.get(0).getType());
                        switch (type) {
                            case 1:
                                tvmm = Float.valueOf(list.get(0).getBloodSugarValue());
                                mtvmm.setText(list.get(0).getBloodSugarValue());
                                value = tvmm;
                                tvmg = tvmm * 18;

                                scaleMark.setValue(tvmm);
                                checkSuager(value);
                                cbgl.setChecked(false);
                                break;
                            case 2:
                                tvmg = Float.valueOf(list.get(0).getBloodSugarValue());

                                mtvmg.setText(list.get(0).getBloodSugarValue());
                                scaleMark.setValue(tvmg);
                                value = tvmg / 18;
                                tvmm = value;

                                cbgl.setChecked(true);

                                checkSuager(tvmg / 18);

                                break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onError(Exception e) {

            }
        }));
    }

    private void changeImgeView(ImageView iv, TextView tv, int lv) {

        switch (lv) {
            case 0:
                iv.setImageResource(R.drawable.icon_dixuetang);
                tv.setText("低血糖");
                break;
            case 1:
                iv.setImageResource(R.drawable.pic_circle_g_b);
                tv.setText("血糖正常");
                break;
            case 2:
                iv.setImageResource(R.drawable.pic_circle_o_r);
                tv.setText("血糖偏高");
                break;


        }

    }

    public static Fragment getInstance() {
        return new BloodSugarFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REQUEST_CALENDAR && data != null) {
            String time = data.getStringExtra("time");
            if (!TextUtils.isEmpty(time)) {
                tvcltime.setText(time);
                mTime = time;
            }

        }
        if (resultCode == ADD_TIMES && data != null) {
            String times = data.getStringExtra("times");
            if (!TextUtils.isEmpty(times)) {
                tvcltimepart.setText(times);
                mMonitorPoint = times;
                checkSuager(tvmm);
            }

        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
