package com.yq.other.task_job;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yq.commons.ucpaas.SysConfig;
import com.yq.commons.util.JsonUtil;
import com.yq.model.SignUpJob;
import com.yq.model.YouQuSignUpMsg;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SignUp {
    private Logger log = Logger.getLogger(SignUp.class);
    private int sleep_time_cs = SysConfig.getInstance().getPropertyInt("signup.sleep_time_cs");
    private float random_longitude = Float.parseFloat(SysConfig.getInstance().getProperty(
            "signup.random_longitude", "0"));
    private float random_latitude = Float.parseFloat(SysConfig.getInstance().getProperty(
            "signup.random_latitude", "0"));
    private YouQuSignUpMsg msg = new YouQuSignUpMsg();

    public void execute(SignUpJob job) {
        log.debug("开始签到定时任务: " + job.getTriggerName() + " " + job.getJobDetailName());
        msg = new YouQuSignUpMsg();
        msg.setGroupId(job.getYq_groupid());
        msg.setMsgId("APP086");
        msg.setUserId(job.getYq_userid());
        msg.setUserName(job.getYq_name());

        int sleep_time = getRandSleepTime(job);




        log.debug("随机等待 " + sleep_time + " 秒.");
        try{
            Thread.sleep(sleep_time * 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        translate_loc(job);
        sign(msg);
    }

    private float getLongitude(SignUpJob job) {
        Random rand = new Random();
        int symbol = (int) Math.pow(-1, rand.nextInt(10));
        float random_long = random_longitude * job.getLoc_random_xs() * rand.nextFloat() * symbol;
        float longitude = Float.parseFloat(job.getLoc_longitude());
        return longitude + random_long;
    }

    private float getLatitude(SignUpJob job) {
        Random rand = new Random();
        int symbol = (int) Math.pow(-1, rand.nextInt(10));
        float random_lat = random_latitude * job.getLoc_random_xs() * rand.nextFloat() * symbol;
        float latitude = Float.parseFloat(job.getLoc_latitude());
        return latitude + random_lat;
    }

    private Integer getRandSleepTime(SignUpJob job) {
        Random rand = new Random();
        return rand.nextInt(job.getTime_random_xs() * sleep_time_cs);
    }

    private void translate_loc(SignUpJob job) {
        float longitude = getLongitude(job);
        float latitude = getLatitude(job);
        msg.setLatitude(latitude);
        msg.setLongitude(longitude);
        String url = String.format("http://api.map.baidu.com/geocoder/v2/?" +
                "ak=Es0Zdh4LrqUwnh8ylnxCXd44oNFZhcxA" +
                "&location=%f,%f&output=json&pois=1", latitude, longitude);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                log.info(result);
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(result).getAsJsonObject();
                String address = json.get("result").getAsJsonObject().get("formatted_address").toString();
                address = address.substring(1, address.length()-1);
                msg.setPosition(String.format("在%s签到啦！", address));
                log.info(msg.getPosition());
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private void sign(YouQuSignUpMsg signUpMsg){
        String url = "http://iyouqu.com.cn:8080/app/group/service.do";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            String text = JsonUtil.Object2EncodeJsonSting(signUpMsg);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("text", text));
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
                log.error("签到失败，HttpCode为：" + statusCode + "，BodyCode为：" + code);
            } else {
                log.info("签到成功，HttpCode为：" + statusCode + "，BodyCode为：" + code);
            }
            log.info(result);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
