package com.taikang.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taikang.dao.RedisBaseDao;
import com.taikang.dto.ERedis;
import com.taikang.util.Tools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;


/**
 * Created by kiwi on 2017/1/3.
 */
@Service
public class ValentineService {

    @Autowired
    private RedisBaseDao redis;
    private Logger log = Logger.getLogger(ValentineService.class);

    public String getReturnInfo(JSONObject json) {
        String returnCode = "SUCCESS";
        String returnMsg = "获取成功";
        String returnItem = "";
        JSONObject resultJson = new JSONObject();

        //出题人ID
        String openId = json.getString("openId");
        //自己的ID
        String selfOpenId = json.getString("selfOpenId");

        try {
            //若出题人ID不为空，并且出过题则标记为true
            Map<String, Object> map = null;
            boolean linkFlag = StringUtils.hasLength(openId) &&
                    !(map = this.redis.hgetAll(ERedis.valentineP_ + openId)).isEmpty();

            resultJson.put("linkFlag", linkFlag);

            if(!linkFlag){
                //未标记，返回本人是否出过题标识
                Map<String, Object> selfMap = this.redis.hgetAll(ERedis.valentineP_ + selfOpenId);
                boolean questionFlag = !selfMap.isEmpty();
                resultJson.put("questionFlag", questionFlag);
                if(questionFlag){
                    this.addInfo(resultJson, selfMap);
                }
            }else{
                //已标记
                boolean masterFlag = openId.equals(selfOpenId);
                resultJson.put("masterFlag", masterFlag);
                if(masterFlag){
                    //出题者本人
                    this.addInfo(resultJson, map);
                }else {
                    //不是出题者本人
                    //是否答过题标识
                    boolean answeredFlag = map.containsKey(ERedis.answer_ + selfOpenId);
                    resultJson.put("answeredFlag", answeredFlag);
                    this.addInfo(resultJson, map);
                    if(answeredFlag){
                        //已答
                        resultJson.put("answer",JSONObject.parseObject("" + map.get(ERedis.answer_ + selfOpenId)).getJSONArray("theAnswer"));
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            returnCode = "ERROR";
            returnMsg = "出错了~";
        }
        log.debug("[resultJson] >>> " + resultJson.toJSONString());
        returnItem = resultJson.toJSONString();
        return Tools.formatReturnJson(returnCode, returnMsg, returnItem);
    }

    //添加基础返回信息
    private void addInfo(JSONObject resultJson, Map<String, Object> map){
        resultJson.put("theAnswer", map.get("theAnswer"));
        resultJson.put("name", map.get("name"));
        resultJson.put("gender", map.get("gender"));
        resultJson.put("headImg", map.get("headImg"));
        JSONArray rank = new JSONArray();
        List<Integer> scoreList = new ArrayList<Integer>();
        for (String key : map.keySet()){
            if(key.startsWith(ERedis.answer_.toString())){
                Integer score = JSONObject.parseObject("" + map.get(key)).getInteger("score");
                scoreList.add(score);
                Collections.sort(scoreList, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                });
                int index = scoreList.indexOf(score);
                rank.add(index, map.get(key));
            }
        }
        resultJson.put("rank", rank);
    }


}
