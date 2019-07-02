package com.yanggang;


import com.yanggang.handler.ForTemplateHandler;
import com.yanggang.handler.LineHandler;
import com.yanggang.io.FileIoObj;
import com.yanggang.service.TemplateEngineService;
import com.yanggang.service.TemplateEngineService_Refactor;

public class App {

    public static void main(String[] args) {

        FileIoObj fileIoObj = new FileIoObj();
        LineHandler lineHandler = new LineHandler();
        ForTemplateHandler forTemplateHandler = new ForTemplateHandler();

        // TemplateEngineService templateEngineService = new TemplateEngineService(fileIoObj);
        TemplateEngineService_Refactor templateEngineService
                = new TemplateEngineService_Refactor(fileIoObj, lineHandler, forTemplateHandler);

        templateEngineService.run();
    }
}
