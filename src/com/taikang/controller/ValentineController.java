package com.taikang.controller;

import com.alibaba.fastjson.JSONObject;
import com.taikang.dao.RedisBaseDao;
import com.taikang.service.ValentineService;
import com.taikang.util.HttpRequestUtil;
import com.taikang.util.Tools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by kiwi on 2017/1/3.
 */
@Controller
public class ValentineController {

    @Autowired
    private ValentineService valentineService;
    private Logger log = Logger.getLogger(ValentineController.class);

    @Autowired
    private RedisBaseDao redis;

    @RequestMapping(value = "test.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("---测试----------开始");
        log.info(redis.get("foo"));
        redis.set("foo","bar");
        log.info("---测试----------结束");
    }
    /**
     * 返回跳转页面标记信息
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "getReturnInfo.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void getReturnInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info("---返回跳转页面标记信息----------开始");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();

        String returnJson = "";
        try {
//            String param = request.getParameter("param");
//            log.debug(request.getParameterMap());
//            String decryptUrl = Tools.getString("decryptUrl");
//            param = HttpRequestUtil.send(decryptUrl, "POST", param);
//            log.info("[param]>>>"+param);

//            JSONObject json = JSONObject.parseObject(param);
            JSONObject json = Tools.decryptParam(request);
            log.debug(json.toJSONString());

            returnJson = this.valentineService.getReturnInfo(json);
        } catch (Exception e){
            e.printStackTrace();
            returnJson = Tools.formatReturnJson("ERROR", "出错了~", "");
        }
        out.print(returnJson);
        out.flush();
        out.close();
        log.info(returnJson);
        log.info("---返回跳转页面标记信息----------结束");
    }


    /**
     * 出题
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "setQuestion.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void setQuestion(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info("---出题----------开始");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();

        String returnJson = "";
        try {
//            String param = request.getParameter("param");
////            String decryptUrl = Tools.getString("decryptUrl");
////            param = HttpRequestUtil.send(decryptUrl, "POST", param);
//            log.debug("[param]>>>"+param);

            JSONObject json = Tools.decryptParam(request);
            log.debug(json.toJSONString());

            returnJson = this.valentineService.setQuestion(json);
        } catch (Exception e){
            e.printStackTrace();
            returnJson = Tools.formatReturnJson("ERROR", "出错了~", "");
        }
        out.print(returnJson);
        out.flush();
        out.close();
        log.info(returnJson);
        log.info("---出题----------结束");
    }


    /**
     * 答题
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "answerQuestion.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void answerQuestion(HttpServletRequest request, HttpServletResponse response) throws Exception{
        log.info("---答题----------开始");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();

        String returnJson = "";
        try {
//            String param = request.getParameter("param");
////            String decryptUrl = Tools.getString("decryptUrl");
////            param = HttpRequestUtil.send(decryptUrl, "POST", param);
//            log.debug("[param]>>>"+param);

            JSONObject json = Tools.decryptParam(request);
            log.debug(json.toJSONString());

            returnJson = this.valentineService.answerQuestion(json);
        } catch (Exception e){
            e.printStackTrace();
            returnJson = Tools.formatReturnJson("ERROR", "出错了~", "");
        }
        out.print(returnJson);
        out.flush();
        out.close();
        log.info(returnJson);
        log.info("---答题----------结束");
    }


}
