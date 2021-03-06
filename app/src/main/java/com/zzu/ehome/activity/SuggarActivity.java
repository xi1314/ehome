package com.zzu.ehome.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zzu.ehome.R;
import com.zzu.ehome.adapter.SuggarAdapter;
import com.zzu.ehome.fragment.BloodSugarFragment;

import java.util.ArrayList;
import java.util.List;

public class SuggarActivity extends Activity {
    private ListView listView;
    private TextView tv_cancel;
    private TextView tv_ok;
    private int index = 0;
    private List<String> list;
    private SuggarAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggar);
        initViews();
        initEvent();
        initData();

    }

    public void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
    }

    public void initData() {

        list = new ArrayList<String>();
        list.add("空腹血糖");
        list.add("餐后血糖");
        list.add("随机血糖");

        mAdapter = new SuggarAdapter(this, list);
        listView.setAdapter(mAdapter);
    }


    public void initEvent() {

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getCurrent() != -1) {
                    Intent intent = new Intent();

                    intent.putExtra("times", list.get(mAdapter.getCurrent()));
                    setResult(BloodSugarFragment.ADD_TIMES, intent);
                }
                finish();

            }
        });

    }
}
