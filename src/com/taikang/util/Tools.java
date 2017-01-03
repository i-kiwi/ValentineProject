package com.taikang.util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by kiwi on 2017/1/3.
 */
public class Tools {

    public static String formatReturnJson(String returnCode, String returnMsg, String returnItem){
        JSONObject json = new JSONObject();
        json.put("returnCode",returnCode);
        json.put("returnMsg",returnMsg);
        json.put("returnItem",returnItem);
        return json.toJSONString();
    }
}
