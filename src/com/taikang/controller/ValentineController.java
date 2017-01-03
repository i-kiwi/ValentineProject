package com.taikang.controller;

import com.alibaba.fastjson.JSONObject;
import com.taikang.service.ValentineService;
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

    @RequestMapping(value = "getReturnInfo.do", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void getReturnInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();

        String returnCode = "";
        String returnMsg = "";
        String returnItem = "";
        String returnJson = "";
        try {
            String param = request.getParameter("param");
            // TODO 解密
            JSONObject json = JSONObject.parseObject(param);
            log.debug(json.toJSONString());

            returnJson = this.valentineService.getReturnInfo(json);
        } catch (Exception e){
            e.printStackTrace();
            returnCode = "ERROR";
            returnMsg = "服务器异常";
            returnJson = Tools.formatReturnJson(returnCode, returnMsg, returnItem);
        }
        out.print(returnJson);
        out.flush();
        out.close();
    }
}
