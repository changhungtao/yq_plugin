package com.yq.other.task_job;

import com.yq.config.SpringContextHolder;
import com.yq.service.inf.UserService;

public class TaskJob {
    UserService userService = (UserService) SpringContextHolder.getBean("UserService");

    public void SendMeasureRemind() {
        /*long max_measured_at = System.currentTimeMillis() / 1000 - 3 * 24 * 3600;
        List<Integer> user_id_list = userService.selUserIdByMaxMeasuredTime(max_measured_at);
        List<PushService> push_service_list = userService.selPushServiceByUserId(user_id_list);

        String msg_content = "您已经三天没有进行健康测量了，请时刻关注您的健康哟！";
        AndroidPushMessage msg = new AndroidPushMessage(1, msg_content);

        AndroidPushMessageForPhone aPush = new AndroidPushMessageForPhone();
        IosPushMessageForPhone iPush = IosPushMessageForPhone.getInstance();
        for (PushService push_service : push_service_list) {
            if (push_service.getDevice() == BeanProperty.ComputeType.ANDROID) {
                aPush.pushMsgToSingleDevice(msg, push_service);
            } else {
                iPush.sendMessage(push_service.getPush_token(), msg_content);
            }
        }*/
        System.out.println("Calling Task Job.");
    }
}
