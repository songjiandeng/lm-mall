package com.macro.mall.common.util;


import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    public static String invokePostWithMap(String url, Map<String, Object> paramsMap) {

        // 发送http请求这里是使用第三方库（apache.http）,
        // 也可使用自己项目封装的，注意ContentType要用application/x-www-form-urlencoded
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }

        String result = "";
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            post.setEntity(stringEntity);

            response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            System.out.println("请求三方接口 返回: " + result);
        } catch (Exception ex) {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }

}
