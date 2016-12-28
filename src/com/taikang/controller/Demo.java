package com.taikang.controller;

import com.taikang.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
@RequestMapping("tt")
public class Demo {

    @Autowired
    private User user;
//    @Autowired
//    private RedisBaseDao redisBaseDao;
    @Autowired
    private JedisPoolConfig jedisPoolConfig;

    @RequestMapping("tt.do")
    @ResponseBody
    public void tt(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            out.print(user.getName());
            out.flush();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
