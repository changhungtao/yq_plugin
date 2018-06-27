package com.yq.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yq.commons.youqu.YQClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestHttpClient {
    public static Logger logger = Logger.getLogger(Log4jTest.class);

    public static void main(String[] args)
            throws IOException {
//        get_location();
//        doPost();
        YQClient client = new YQClient();
        client.getUserInf("18827059632", "fiberhome", false);
        return;
    }

    private static void get_location() throws IOException {
        double longitude = 114.391847;
        double latitude = 30.518829;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            String url = "http://api.map.baidu.com/geocoder/v2/?ak=Es0Zdh4LrqUwnh8ylnxCXd44oNFZhcxA&location=30.518829,114.391847&output=json&pois=1";
            HttpGet httpGet = new HttpGet(url);
//            HttpParams params = httpGet.getParams();
//            params.setParameter("ak", "Es0Zdh4LrqUwnh8ylnxCXd44oNFZhcxA");
//            params.setParameter("location", "30.518829,114.391847");
//            params.setParameter("output", "json");
//            params.setParameter("pois", "1");
//            httpGet.setParams(params);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                logger.info(result);
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(result).getAsJsonObject();
                logger.info(json.get("result").getAsJsonObject().get("formatted_address").toString());
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

    }

    private static void doPost(){
        String url = "http://iyouqu.com.cn:8080/app/group/service.do";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            String text = "{\"groupId\":\"2001116\",\"userName\":\"张洪涛\",\"userId\":\"9593\",\"msgId\":\"APP086\",\"position\":\"在马来西亚签到啦！\",\"longitude\":114.397534,\"latitude\":30.521728}";
//            JsonObject parameters = new JsonObject();
//            parameters.add("text", JsonUtil.gson.toJsonTree(signUpMsg));
//            String body = JsonUtil.Object2EncodeJsonSting(parameters);
//            StringEntity reqEntity = new StringEntity(body);
//            reqEntity.setContentType("application/x-www-form-urlencoded");


            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("text", text));
            logger.info(parameters);
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            uefEntity.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(uefEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK){
                logger.error("签到失败，请求的返回码为：" + statusCode);
            } else {
                logger.info("签到成功，请求的返回码为：" + statusCode);
            }
            logger.error(EntityUtils.toString(entity, "UTF-8"));
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return;
    }


//    private static void doGet(String url, JsonObject json, String charset) throws IOException {
//        HttpGet httpGet = new HttpGet(url);
//        HttpResponse httpResponse = client.execute(httpGet);
//
//        System.out.println("GET Response Status:: "
//                + httpResponse.getStatusLine().getStatusCode());
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                httpResponse.getEntity().getContent()));
//
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = reader.readLine()) != null) {
//            response.append(inputLine);
//        }
//        reader.close();
//
//        System.out.println(response.toString());
//
//    }
}