package com.taikang.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

/**
 * Created by kiwi on 2017/1/3.
 */
public class Tools {

    private static Logger log = Logger.getLogger(Tools.class);

    public static String formatReturnJson(String returnCode, String returnMsg, Object returnItem){
        JSONObject json = new JSONObject();
        json.put("returnCode",returnCode);
        json.put("returnMsg",returnMsg);
        json.put("returnItem",returnItem);
        return json.toJSONString();
    }

    /**
     * 默契度计算
     * 默契度=基础默契度*随机系数n(0.9 <= n <= 1.2)
     * @param theAnswerArr
     * @param answerArr
     * @return
     */
    public static double calcScore(JSONArray theAnswerArr, JSONArray answerArr){
        //过滤错误答案
        theAnswerArr.retainAll(answerArr);
        //正确答案个数
        int rightCount = theAnswerArr.size();
        //基础比率
        double baseRate = 0;
        //随机系数(0.9 >= n >= 1.2)
        double randomRatio = (new Random().nextInt(12 + 1 - 9) + 9) / 10.0;
        switch (rightCount){
            case 0:
                baseRate = 0.35;
                break;
            case 1:
                baseRate = 0.50;
                break;
            case 2:
                baseRate = 0.65;
                break;
            case 3:
//                if(new Random().nextBoolean()){
//                    baseRate = 1;
//                    randomRatio = 1;
//                }else{
                    baseRate = 0.83;
//                }
                break;
        }
        return baseRate * randomRatio * 100;
    }

    /**
     * 读取配置文件
     * @param key
     * @return
     */
    public static String getString(String key){
        InputStream in = Tools.class.getClassLoader().getResourceAsStream("conf.properties");
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (Exception e){
            e.printStackTrace();
        }
        return p.getProperty(key);
    }

    public static JSONObject decryptParam(HttpServletRequest request){
        JSONObject json = new JSONObject();
        String decryptUrl = getString("decryptUrl");
        Enumeration<String> names = request.getParameterNames();
        while(null != names && names.hasMoreElements()){
//            JSONObject paramJson = JSON.parseObject(names.nextElement());
            String name = names.nextElement();
            String value = request.getParameter(name);
            log.debug("[name]>>>" + name + "\t" + "[value]>>>" + value);
            if(name.equals("reqInfo")){
                String decryptParam = HttpRequestUtil.send(decryptUrl, "POST", value);
                log.debug("[decryptParam]>>" + decryptParam);
                JSONObject paramJson = JSON.parseObject(decryptParam);
                for (String key : paramJson.keySet()) {
                    json.put(key, paramJson.getString(key));
                }
                continue;
            }
            json.put(name, value);
        }
        return json;
    }
}
