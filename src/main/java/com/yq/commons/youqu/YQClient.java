package com.yq.commons.youqu;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yq.commons.util.JsonUtil;
import com.yq.model.YQUser;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class YQClient {
    private Logger log = Logger.getLogger(YQClient.class);

    //POST  http://common.iyouqu.com.cn:8080/app/user/service.do
    //text={"device":"PRA-AL00","mobile":"18827059632","msgId":"APP129",
    // "password":"9c413668cae66b8dfbfd28ff26b3f18e","pushType":1,
    // "registrationId":"77f68d23bf1e270b0df93f81c59afd4c",
    // "system":"7.0","systemType":"1","version":"V2.2.7"}
    public String getUserInf(String username, String password, boolean isMd5){
        MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
        String md5_password = password;
        if(isMd5 == false) {
            md5_password = passwordEncoder.encodePassword(password, "");
        }
        String url="http://common.iyouqu.com.cn:8080/app/user/service.do";
        String params=String.format("{\"mobile\":\"%s\",\"msgId\":\"APP129\",\"password\":\"%s\"," +
                "\"pushType\":1,\"system\":\"7.0\",\"systemType\":\"2\",\"version\":\"V2.2.7\"}",
                username, md5_password);
        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("text", params));
            log.info(parameters);
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            uefEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(uefEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(entity, "UTF-8");
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(result).getAsJsonObject();
            int code = json.get("code").getAsInt();

            if (statusCode != HttpStatus.SC_OK || code != 0){
                log.error("登录失败，HttpCode为：" + statusCode + "，BodyCode为：" + code);
            } else {
                log.info("登录成功，HttpCode为：" + statusCode + "，BodyCode为：" + code);
            }
            log.info(result);
            EntityUtils.consume(entity);
            JsonObject userInfo = json.get("resultMap").getAsJsonObject().get("userInfo").getAsJsonObject();
            YQUser user = (YQUser) JsonUtil.JsonSting2DecodeObject(userInfo.toString(), YQUser.class);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }
}
