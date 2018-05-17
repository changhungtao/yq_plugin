package com.yq.service.inf;

import com.yq.model.FileInf;

import java.util.Map;

public interface FileService {
    FileInf selFileByMd5(String md5);

    void updateFile(Map map);

    void insertFile(FileInf fileInf);
}
