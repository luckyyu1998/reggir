package com.wang.reggir.controller;


import com.wang.reggir.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@Slf4j
@Controller
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String path;

    @ResponseBody
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filName = UUID.randomUUID().toString() + suffix;

        //判断文件夹是否存在，若不存在则进行创建
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            //临时文件转存到指定位置
            file.transferTo(new File(path + filName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filName);

    }

    @ResponseBody
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(path + name));
            ServletOutputStream outputStream = response.getOutputStream();
            //设定二进制文件类型，似乎不设置也行
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
