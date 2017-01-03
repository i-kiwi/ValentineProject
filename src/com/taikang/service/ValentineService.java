package com.taikang.service;

import com.alibaba.fastjson.JSONObject;
import com.taikang.util.Tools;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by kiwi on 2017/1/3.
 */
@Service
public class ValentineService {

    public String getReturnInfo(JSONObject json) {
        String returnCode = "";
        String returnMsg = "";
        String returnItem = "";

        //出题人ID
        String openId = json.getString("openId");
        //自己的ID
        String selfOpenId = json.getString("selfOpenId");
        try {
//            String linkFlag = StringUtils.isEmpty(openId) ? "F" : "T";
        } catch (Exception e){
            e.printStackTrace();
        }
        return Tools.formatReturnJson(returnCode, returnMsg, returnItem);
    }
}
