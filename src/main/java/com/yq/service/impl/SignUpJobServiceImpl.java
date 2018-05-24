package com.yq.service.impl;

import com.yq.mapper.SignUpJobMapper;
import com.yq.model.SignUpJob;
import com.yq.service.inf.SignUpJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("SignUpJobService")
public class SignUpJobServiceImpl implements SignUpJobService {
    @Autowired
    private SignUpJobMapper signUpJobMapper;

    @Override
    public List<SignUpJob> selectJobsByUserId(int user_id) {
        Map map = new HashMap();
        map.put("user_id", user_id);
        return signUpJobMapper.selectJobs(map);
    }

    @Override
    public List<SignUpJob> selectJobsByYqUserId(int yq_user_id) {
        Map map = new HashMap();
        map.put("yq_user_id", yq_user_id);
        return signUpJobMapper.selectJobs(map);
    }

    @Override
    public List<SignUpJob> selectJobsByYqGroupId(int yq_group_id) {
        Map map = new HashMap();
        map.put("yq_group_id", yq_group_id);
        return signUpJobMapper.selectJobs(map);
    }

    @Override
    public List<SignUpJob> selectJobs(Map map) {
        return signUpJobMapper.selectJobs(map);
    }

    @Override
    public List<SignUpJob> selectJobs() {
        Map map = new HashMap();
        return signUpJobMapper.selectJobs(map);
    }

    @Override
    public SignUpJob selectJob(int job_id) {
        Map map = new HashMap();
        map.put("job_id", job_id);
        return signUpJobMapper.selectJob(map);
    }

    @Override
    public void insertJob(SignUpJob job) {
        signUpJobMapper.insertJob(job);
    }

    @Override
    public void updateJob(SignUpJob job) {
        signUpJobMapper.updateJob(job);
    }

    @Override
    public void deleteJob(int job_id) {
        SignUpJob job = new SignUpJob();
        job.setJob_id(job_id);
        signUpJobMapper.deleteJob(job);
    }
}
