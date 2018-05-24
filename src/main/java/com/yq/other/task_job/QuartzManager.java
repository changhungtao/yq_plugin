package com.yq.other.task_job;

import com.yq.model.SignUpJob;
import com.yq.service.inf.SignUpJobService;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class QuartzManager implements BeanFactoryAware {
    private Logger log = Logger.getLogger(QuartzManager.class);
    private Scheduler scheduler;
    private static BeanFactory beanFactory = null;

    @Autowired
    private SignUpJobService signUpJobService;

    @SuppressWarnings("unused")
    private void reScheduleJob() throws Exception, ParseException {
        log.info("start reScheduleJob......");
        List<SignUpJob> jobs = signUpJobService.selectJobs();
        if (jobs != null && jobs.size() > 0) {
            for (SignUpJob job : jobs) {
                configQuatrz(job);
            }
        }
    }

    public boolean configQuatrz(SignUpJob job) {
        boolean result = false;
        try {
            CronTriggerBean trigger = (CronTriggerBean) scheduler.getTrigger(
                    job.getTriggerName(), Scheduler.DEFAULT_GROUP);
            if(trigger != null){
                modify(job, trigger);
            }else{
                if(job.getState() == 1){
                    create(job);
                }
            }
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    public void create(SignUpJob job) throws Exception{
        MethodInvokingJobDetailFactoryBean mjdfb = new MethodInvokingJobDetailFactoryBean();
        mjdfb.setName(job.getJobDetailName());
        mjdfb.setTargetObject(Class.forName("com.yq.other.task_job.SignUp").newInstance());
        mjdfb.setTargetMethod("execute");
        mjdfb.setConcurrent(false);
        mjdfb.afterPropertiesSet();

        JobDetail jobDetail = new JobDetail();
        jobDetail = (JobDetail) mjdfb.getObject();
        jobDetail.setName(job.getJobDetailName());
        scheduler.addJob(jobDetail, true);

        CronTriggerBean c = new CronTriggerBean();
        c.setCronExpression(job.getCronExpression());
        c.setName(job.getTriggerName());
        c.setJobDetail(jobDetail);
        c.setJobName(job.getJobDetailName());
        scheduler.scheduleJob(c);
        scheduler.rescheduleJob(job.getTriggerName(), Scheduler.DEFAULT_GROUP, c);
        log.info(new Date() + ":新建" + job.getTriggerName() + "计划任务");
    }

    public void modify(SignUpJob job, CronTriggerBean trigger) throws Exception{
        if(!(job.getState() == 1)){
            remove(job, trigger);
            return;
        }
        if (trigger.getCronExpression().equalsIgnoreCase(job.getCronExpression())){
            return;
        }
        trigger.setCronExpression(job.getCronExpression());
        scheduler.rescheduleJob(job.getTriggerName(), Scheduler.DEFAULT_GROUP, trigger);
        log.info(new Date() + ": 更新" + job.getTriggerName() + "计划任务");
    }

    public void remove(SignUpJob job, CronTriggerBean trigger) throws Exception{
        scheduler.pauseTrigger(trigger.getName(), trigger.getGroup());
        scheduler.unscheduleJob(trigger.getName(), trigger.getGroup());
        scheduler.deleteJob(trigger.getJobName(), trigger.getJobGroup());
        log.info(new Date() + ":删除" + job.getTriggerName() + "计划任务");
    }

    public Scheduler getScheduler(){
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.beanFactory = factory;
    }

    public BeanFactory getBeanFactory(){
        return beanFactory;
    }
}
