package com.yq.model;

public class SignUpJob {
    private Integer job_id;
    private Integer user_id;
    private Integer yq_userid;
    private String yq_name;
    private Integer yq_groupid;
    private String loc_longitude;
    private String loc_latitude;
    private String time_week;
    private String time_hour;
    private String time_minute;
    private Integer loc_random_xs;
    private Integer time_random_xs;
    private Integer state;

    public Integer getJob_id() {
        return job_id;
    }

    public void setJob_id(Integer job_id) {
        this.job_id = job_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getYq_userid() {
        return yq_userid;
    }

    public void setYq_userid(Integer yq_userid) {
        this.yq_userid = yq_userid;
    }

    public String getYq_name() {
        return yq_name;
    }

    public void setYq_name(String yq_name) {
        this.yq_name = yq_name;
    }

    public Integer getYq_groupid() {
        return yq_groupid;
    }

    public void setYq_groupid(Integer yq_groupid) {
        this.yq_groupid = yq_groupid;
    }

    public String getLoc_longitude() {
        return loc_longitude;
    }

    public void setLoc_longitude(String loc_longitude) {
        this.loc_longitude = loc_longitude;
    }

    public String getLoc_latitude() {
        return loc_latitude;
    }

    public void setLoc_latitude(String loc_latitude) {
        this.loc_latitude = loc_latitude;
    }

    public String getTime_week() {
        return time_week;
    }

    public void setTime_week(String time_week) {
        this.time_week = time_week;
    }

    public String getTime_hour() {
        return time_hour;
    }

    public void setTime_hour(String time_hour) {
        this.time_hour = time_hour;
    }

    public String getTime_minute() {
        return time_minute;
    }

    public void setTime_minute(String time_minute) {
        this.time_minute = time_minute;
    }

    public Integer getLoc_random_xs() {
        return loc_random_xs;
    }

    public void setLoc_random_xs(Integer loc_random_xs) {
        this.loc_random_xs = loc_random_xs;
    }

    public Integer getTime_random_xs() {
        return time_random_xs;
    }

    public void setTime_random_xs(Integer time_random_xs) {
        this.time_random_xs = time_random_xs;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCronExpression(){
        //[秒] [分] [小时] [日] [月] [周] [年]
        //0 15 11 * * 2-6 *
        String cronExpression = String.format("0 %s %s ? * %s *", time_minute, time_hour, time_week);
        return cronExpression;
    }

    public String getTriggerName(){
        String triggerName = String.format("sign_up_job_%d", job_id);
        return triggerName;
    }

    public String getJobDetailName(){
        String jobDetailName = String.format("sign_up_%d_%d", yq_userid, job_id);
        return jobDetailName;
    }
}
