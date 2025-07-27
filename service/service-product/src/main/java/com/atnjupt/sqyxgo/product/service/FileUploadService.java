package com.atnjupt.sqyxgo.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName:FileUploadService
 * Package: com.atnjupt.sqyxgo.product.service
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/19 16:46
 * @Version 1.0
 */
public interface FileUploadService {

    //文件上传
    String fileUpload(MultipartFile file) throws Exception;
}
