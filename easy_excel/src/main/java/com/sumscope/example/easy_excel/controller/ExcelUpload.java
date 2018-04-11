package com.sumscope.example.easy_excel.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.io.BaseEncoding;
import com.sumscope.example.easy_excel.JsonUtil;
import com.sumscope.example.easy_excel.ValidationHandler;
import com.sumscope.example.easy_excel.model.User;
import com.sumscope.example.easy_excel.model.UserExcelListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

/**
 * @author xuejian.sun
 * @date 2018/4/11
 */
@Slf4j
@RestController
@RequestMapping(value = "/excel")
public class ExcelUpload {

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public void upload(@RequestBody String string) throws Exception {
        Map map = JsonUtil.readValue(string, Map.class);
        String fileName = (String)map.get("fileName");
        String fileContent = (String) map.get("excelContent");
        fileContent = fileContent.substring(fileContent.indexOf("base64,") + "base64,".length());
        byte[] bytes = BaseEncoding.base64().decode(fileContent);
        InputStream in = new ByteArrayInputStream(bytes);
        try {
            UserExcelListener userExcelListener = new UserExcelListener();

            ExcelReader reader = new ExcelReader(in,ExcelTypeEnum.XLSX,null,userExcelListener);

            reader.read();
        }catch (Exception e){

        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/uploadfile",method = RequestMethod.POST)
    public void uploadfile(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("文件名:{}",file.getName());
        log.info("文件内容类型{}",file.getContentType());
        log.info("原文件名{}",file.getOriginalFilename());
        ImportParams importParams = new ImportParams();
        ValidationHandler validationHandler = new ValidationHandler();
        importParams.setDataHanlder(validationHandler);
        importParams.setNeedVerfiy(true);
        importParams.setSheetNum(2);
        ExcelImportResult<User> result = ExcelImportUtil.importExcelMore(file.getInputStream(), User.class, importParams);
        result.getFailList().forEach(user -> log.info("失败信息{}",user.getName()));
        result.getList().forEach(user -> log.info("成功信息:{}",user));
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public void get(){
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("easypoi.xlsx");
        try {
            UserExcelListener userExcelListener = new UserExcelListener();

            ExcelReader reader = new ExcelReader(in,ExcelTypeEnum.XLSX,null,userExcelListener);
            reader.getSheets().forEach(sheet -> reader.read(sheet));
        }catch (Exception e){

        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}