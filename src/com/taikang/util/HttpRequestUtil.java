package com.taikang.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kiwi on 2017/1/4.
 */
public class HttpRequestUtil {
    private static Logger log = Logger.getLogger(HttpRequestUtil.class);

    public static String send(String httpUrl, String method, String param) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("Accept-Charset", "utf-8");

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(param);
            out.flush();
            out.close();

            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return sbf.toString();
    }



}
