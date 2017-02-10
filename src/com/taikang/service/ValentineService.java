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


    /**
     * 返回跳转页面标记信息
     * @param json
     * @return
     */
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
            if(StringUtils.isEmpty(selfOpenId)){
                log.debug("selfOpenId is empty!");
                throw new Exception();
            }
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
        return Tools.formatReturnJson(returnCode, returnMsg, resultJson);
    }
    //添加基础返回信息
    private void addInfo(JSONObject resultJson, Map<String, Object> map){
        log.info(map.get("theAnswer"));
        resultJson.put("theAnswer", null != map.get("theAnswer") ? JSONArray.parseArray(""+map.get("theAnswer")) : "");
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
                rank.add(index, JSONObject.parseObject(""+map.get(key)));
            }
        }
        resultJson.put("rank", rank);
    }

    /**
     * 出题
     * @param json
     * @return
     */
    public String setQuestion(JSONObject json) {
        String returnCode = "SUCCESS";
        String returnMsg = "出题成功";
        String returnItem = "";
//        JSONObject resultJson = new JSONObject();

        String openId = json.getString("openId");
        String name = json.getString("name");
        String headImg = json.getString("headImg");
        String gender = json.getString("gender");
        String theAnswer = json.getString("theAnswer");

        try {
            if(StringUtils.isEmpty(openId) || StringUtils.isEmpty(name) ||
                    StringUtils.isEmpty(gender) || StringUtils.isEmpty(theAnswer)){
                log.debug("param is empty!");
                throw new Exception();
            }
            JSONArray theAnswerArr = JSONArray.parseArray(theAnswer);
            if(theAnswerArr.size() == 3 && this.redis.hlen(ERedis.valentineP_ + openId) == 0){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", name);
                map.put("headImg", headImg);
                map.put("gender", gender);
                map.put("theAnswer", theAnswer);
                this.redis.hmset(ERedis.valentineP_ + openId, map);
            }else{
                returnCode = "FAIL";
                returnMsg = "出题失败";
            }
        } catch (Exception e){
            e.printStackTrace();
            returnCode = "ERROR";
            returnMsg = "出错了~";
        }
//        returnItem = resultJson.toJSONString();
        return Tools.formatReturnJson(returnCode, returnMsg, returnItem);
    }


    /**
     * 答题
     * @param json
     * @return
     */
    public String answerQuestion(JSONObject json) {
        String returnCode = "SUCCESS";
        String returnMsg = "答题成功";
        String returnItem = "";
        JSONObject resultJson = new JSONObject();

        String openId = json.getString("openId");
        String selfOpenId = json.getString("selfOpenId");
        String name = json.getString("name");
        String headImg = json.getString("headImg");
        String theAnswer = json.getString("theAnswer");
        //回答答案
        JSONArray answerArr = JSONArray.parseArray(theAnswer);
        try {
            if(StringUtils.isEmpty(openId) || StringUtils.isEmpty(name) ||
                    StringUtils.isEmpty(selfOpenId) || StringUtils.isEmpty(theAnswer)){
                log.debug("param is empty!");
                throw new Exception();
            }
            if(answerArr.size() != 3 ||
                    null != this.redis.hget(ERedis.valentineP_ + openId, ERedis.answer_ + selfOpenId)){
                /*
                    1两个ID不为空
                    2两个ID不相等
                    3回答个数等于三
                    4没有回答过该用户的题
                 */
                returnCode = "FAIL";
                returnMsg = "答题失败";
            } else {
                //正确答案
                JSONArray theAnswerArr = JSONArray.parseArray(""+this.redis.hget(ERedis.valentineP_ + openId, "theAnswer"));
                //默契度
                double score = Tools.calcScore(theAnswerArr, answerArr);
                //添加信息到缓存
                JSONObject answerJson = new JSONObject();
                answerJson.put("openId", selfOpenId);
                answerJson.put("name", name);
                answerJson.put("headImg", headImg);
                answerJson.put("score", score);
                answerJson.put("theAnswer", answerArr);
                this.redis.hset(ERedis.valentineP_ + openId, ERedis.answer_ + selfOpenId, answerJson.toJSONString());
                //返回排名等信息
                JSONObject tempJson = new JSONObject();
                this.addInfo(tempJson, this.redis.hgetAll(ERedis.valentineP_ + openId));
                resultJson.put("score", score);
                resultJson.put("rank", tempJson.getJSONArray("rank"));
            }

        } catch (Exception e){
            e.printStackTrace();
            returnCode = "ERROR";
            returnMsg = "出错了~";
        }
        return Tools.formatReturnJson(returnCode, returnMsg, resultJson);
    }
}
