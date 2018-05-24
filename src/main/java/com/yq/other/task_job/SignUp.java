package com.yq.other.task_job;

import com.yq.config.SpringContextHolder;
import com.yq.model.SignUpJob;
import com.yq.service.inf.SignUpJobService;

import java.util.List;

public class SignUp {
    SignUpJobService signUpJobService = (SignUpJobService) SpringContextHolder.getBean("SignUpJobService");

    public void execute() {
        List<SignUpJob> job_list = signUpJobService.selectJobs();
        for (com.yq.model.SignUpJob job : job_list) {
            System.out.println(job.getJob_id());
        }
        System.out.println("Calling Task Job.");
    }
}
