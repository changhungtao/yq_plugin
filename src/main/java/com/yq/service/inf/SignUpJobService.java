package com.yq.service.inf;

import com.yq.model.SignUpJob;

import java.util.List;
import java.util.Map;

public interface SignUpJobService {
    List<SignUpJob> selectJobsByUserId(int user_id);

    List<SignUpJob> selectJobsByYqUserId(int yq_user_id);

    List<SignUpJob> selectJobsByYqGroupId(int yq_group_id);

    List<SignUpJob> selectJobs(Map map);

    List<SignUpJob> selectJobs();

    SignUpJob selectJob(int job_id);

    void insertJob(SignUpJob job);

    void updateJob(SignUpJob job);

    void deleteJob(int job_id);
}
