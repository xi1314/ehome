package com.zzu.ehome.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 */

public class OcrTypeData {
    @SerializedName("OCRTypeInquiry")
    List<OcrTypeBean> data;

    public List<OcrTypeBean> getData() {
        return data;
    }
}
