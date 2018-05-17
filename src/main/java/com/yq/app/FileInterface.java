package com.yq.app;

import com.yq.commons.constants.ErrorCode;
import com.yq.commons.util.CommonUtil;
import com.yq.model.FileInf;
import com.yq.model.ResponseContent;
import com.yq.service.inf.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;

@Controller
public class FileInterface extends BaseInterface {
    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    private static final int BUFFER_SIZE = 1024 * 20;

    /*3.3.14 POST /api/upload*/
    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    public void UploadFilesInterface(@RequestParam("file") MultipartFile file,
                                     HttpServletRequest req, HttpServletResponse res) {
        ResponseContent content = new ResponseContent();
        try {
            String resPath = req.getScheme() + "://" + req.getServerName() + req.getContextPath() + "/api/download/";
            String md5 = saveFile(file, req.getSession().getServletContext().getRealPath("/") + "../upload/other/");
            if (StringUtils.isBlank(md5)) {
                setRequestErrorRes(res, "上传失败", ErrorCode.FAILUPLOAD);
                return;
            }
            content.setSuccess_message(resPath + md5);
            setResponseContent(res, content);
        } catch (Exception e) {
            setSystemErrorRes(res, e.toString(), ErrorCode.SYSTEM_ERROR);
        }
    }

    public String saveFile(MultipartFile file, String path) {
        String md5_code = null;
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            CommonUtil.ifNotExistsCreate(path);
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".", file.getOriginalFilename().length()));
            md5_code = passwordEncoder.encodePassword(file.getOriginalFilename(), CommonUtil.getRandomString(6));
            file.transferTo(new File(path + md5_code + suffix));

            String full_path = path.substring(path.lastIndexOf("upload") + 7) + md5_code + suffix;
            FileInf fileInf = new FileInf();
            fileInf.setFile_id(0);
            fileInf.setMd5(md5_code);
            fileInf.setFile_path(full_path);
            fileInf.setUpload_time(format.format(new Date()));
            fileService.insertFile(fileInf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return md5_code;
    }

    /*3.3.13 GET /api/download/{file_id}*/
    @RequestMapping(value = "/api/download/{file_id}", method = RequestMethod.GET)
    public void GetDownloadFile(@RequestParam("file_id") String md5,
                                HttpServletRequest req, HttpServletResponse res) {
        try {
           FileInf fileInf = fileService.selFileByMd5(md5);
            if (fileInf == null) {
                setParamWarnRes(res, "下载的文件不存在", ErrorCode.FILE_NOT_EXIST);
                return;
            }

            ServletContext context = req.getSession().getServletContext();
            String appPath = context.getRealPath("/") + "../upload/";
            String fullPath = appPath + fileInf.getFile_path();
            File downloadFile = new File(fullPath);
            if (!downloadFile.exists()) {
                setParamWarnRes(res, "下载的文件不存在", ErrorCode.FILE_NOT_EXIST);
                return;
            }

            FileInputStream inputStream = new FileInputStream(downloadFile);
            String mimeType = context.getMimeType(fullPath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            res.setContentType(mimeType);
            res.setContentLength((int) downloadFile.length());

            OutputStream outStream = res.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            setSystemErrorRes(res, "系统错误", ErrorCode.SYSTEM_ERROR);
            return;
        }
    }
}