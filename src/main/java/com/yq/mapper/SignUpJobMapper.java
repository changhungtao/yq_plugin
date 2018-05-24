package com.yq.mapper;

import com.yq.model.SignUpJob;

import java.util.List;
import java.util.Map;

public interface SignUpJobMapper {
    List<SignUpJob> selectJobs(Map map);

    SignUpJob selectJob(Map map);

    void insertJob(SignUpJob job);

    void updateJob(SignUpJob job);

    void deleteJob(SignUpJob job);
}
