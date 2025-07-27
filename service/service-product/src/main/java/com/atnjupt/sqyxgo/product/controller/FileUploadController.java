package com.atnjupt.sqyxgo.product.controller;

import com.atnjupt.sqyxgo.common.result.Result;
import com.atnjupt.sqyxgo.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName:FileUploadController
 * Package: com.atnjupt.sqyxgo.product.controller
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/19 16:45
 * @Version 1.0
 */
@Api(tags = "文件上传接口")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    //文件上传
    @ApiOperation("文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) throws Exception{
        return Result.ok(fileUploadService.fileUpload(file));
    }
}