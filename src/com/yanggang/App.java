package com.yanggang;


import com.yanggang.io.FileIoObj;
import com.yanggang.service.TemplateEngineService;

public class App {

    public static void main(String[] args) {

        FileIoObj fileIoObj = new FileIoObj();
        TemplateEngineService templateEngineService = new TemplateEngineService(fileIoObj);

        templateEngineService.run();
    }
}
