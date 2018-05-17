package com.yq.mapper;

import com.yq.model.FileInf;

import java.util.Map;

public interface FileMapper {
    FileInf selectFile(Map map);

    void updateFile(Map map);

    void insertFile(FileInf fileInf);
}
