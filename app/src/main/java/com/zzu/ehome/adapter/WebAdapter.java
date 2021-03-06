package com.zzu.ehome.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzu.ehome.R;
import com.zzu.ehome.application.Constants;
import com.zzu.ehome.bean.OrderInquiryByTopmd;
import com.zzu.ehome.bean.TreatmentSearch;
import com.zzu.ehome.utils.DateUtils;

import java.util.List;

import static com.zzu.ehome.R.id.holder;
import static com.zzu.ehome.R.id.parent;

/**
 * Created by Administrator on 2016/9/13.
 */
public class WebAdapter extends BaseListAdapter<TreatmentSearch> {
    private List<TreatmentSearch> mList;
    private Context mContext;

    public WebAdapter(Context context, List<TreatmentSearch> objects) {
        super(context, objects);
        this.mList = objects;
        this.mContext = context;
    }
    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        // current menu type
        if ( mList.get(position).isOpen()) {
            return 1;
        }else{
            return 0;
        }



    }

    @Override
    public View getGqView(int position, View convertView, ViewGroup parent) {
        ViewHolder0 holder0 = null;
        ViewHolder1 holder1 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case 0:
                    holder0 = new ViewHolder0();
                    convertView = getInflater().inflate(R.layout.appointment_item, null);
                    holder0.tvtitle = (TextView) convertView.findViewById(R.id.tvtitle);
                    holder0.name = (TextView) convertView.findViewById(R.id.tvname);
                    holder0.tvtime = (TextView) convertView.findViewById(R.id.tvtime);
                    holder0.ivhead = (ImageView) convertView.findViewById(R.id.ivhead);
                    holder0.iv_status=(ImageView)convertView.findViewById(R.id.iv_status);
                    convertView.setTag(holder0);
                    break;
                case 1:
                    holder1 = new ViewHolder1();
                    convertView = getInflater().inflate(R.layout.appointment_item, null);
                    holder1.tvtitle = (TextView) convertView.findViewById(R.id.tvtitle);
                    holder1.name = (TextView) convertView.findViewById(R.id.tvname);
                    holder1.tvtime = (TextView) convertView.findViewById(R.id.tvtime);
                    holder1.ivhead = (ImageView) convertView.findViewById(R.id.ivhead);
                    holder1.iv_status=(ImageView)convertView.findViewById(R.id.iv_status);
                    convertView.setTag(holder1);
                    break;
            }

        } else {
            switch (type){
                case 0:
                    holder0 = (ViewHolder0) convertView.getTag();
                    break;
                case 1:
                    holder1= (ViewHolder1) convertView.getTag();
                    break;

            }

        }
        switch (type) {
            case 0:
                TreatmentSearch item = getItem(position);
                holder0.name.setText(item.getName());
                holder0.tvtitle.setText(item.getHospital() + "  " + item.getJobName());
                ImageView imageView = holder0.ivhead;
                final String tag = (String) imageView.getTag(R.id.imageloader_uri);
                String uri = "";
                if (TextUtils.isEmpty(item.getDoctorIcon())||item.getDoctorIcon().contains("vine")) {
                    uri="";
                }else{
                    uri= Constants.ICON  + item.getDoctorIcon().replace("~", "").replace("\\", "/");
                }
                if (!uri.equals(tag)) {
                    imageView.setImageResource(R.drawable.icon_doctor);
                }
                imageView.setTag(R.id.imageloader_uri,uri);
                Glide.with(mContext)
                        .load(uri)
                        .centerCrop().error(R.drawable.icon_doctor)
                        .into(imageView);

                holder0.iv_status.setVisibility(View.INVISIBLE);
                holder0.tvtime.setText("预约时间：" + item.getTime()+item.getDay());
                break;
            case 1:
                TreatmentSearch item1 = getItem(position);
                ImageView imageView1 = holder1.ivhead;
                final String tag1 = (String) imageView1.getTag(R.id.imageloader_uri);
                String uri1 = "";
                if (TextUtils.isEmpty(item1.getDoctorIcon())||item1.getDoctorIcon().contains("vine")) {
                    uri1="";
                }else{
                    uri1= Constants.ICON+ item1.getDoctorIcon().replace("~", "").replace("\\", "/");
                }
                if (!uri1.equals(tag1)) {
                    imageView1.setImageResource(R.drawable.icon_doctor);
                }
                imageView1.setTag(R.id.imageloader_uri,uri1);
                Glide.with(mContext)
                        .load(uri1)
                        .centerCrop().error(R.drawable.icon_doctor)
                        .into(imageView1);
                holder1.name.setText(item1.getName());
                holder1.tvtitle.setText(item1.getHospital() + "  " + item1.getJobName());


                holder1.iv_status.setVisibility(View.VISIBLE);
                holder1.tvtime.setText("预约时间：" + item1.getTime()+item1.getDay());
                break;
        }

        return convertView;
    }

    public static class ViewHolder0 {
        private TextView tvtitle;
        private TextView name;
        private TextView tvtime;
        private ImageView ivhead;
        private ImageView iv_status;
    }
    public static class ViewHolder1 {
        private TextView tvtitle;
        private TextView name;
        private TextView tvtime;
        private ImageView ivhead;
        private ImageView iv_status;
    }
}
