package com.yq.service.impl;

import com.yq.mapper.SchoolMapper;
import com.yq.model.ClassInf;
import com.yq.model.School;
import com.yq.service.inf.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("SchoolService")
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    @Override
    public School selSchoolBySchoolId(int school_id) {
        Map map = new HashMap<String, Object>(1);
        map.put("school_id", school_id);
        return schoolMapper.selectSchool(map);
    }

    @Override
    public void updateSchool(Map map) {
        schoolMapper.updateSchool(map);
    }

    @Override
    public void insertSchool(School school) {
        schoolMapper.insertSchool(school);
    }

    @Override
    public ClassInf selClassByClassId(int class_id) {
        Map map = new HashMap<String, Object>(1);
        map.put("class_id", class_id);
        return schoolMapper.selectClass(map);
    }

    @Override
    public void updateClass(Map map) {
        schoolMapper.updateClass(map);
    }

    @Override
    public void insertClass(ClassInf classInf) {
        schoolMapper.insertClass(classInf);
    }

    @Override
    public void deleteClass(int class_id) {
        Map map = new HashMap<String, Object>(1);
        map.put("class_id", class_id);
        schoolMapper.deleteClass(map);
    }
}
