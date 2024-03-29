package com.yanggang;


import com.yanggang.handler.ForTemplateHandler;
import com.yanggang.handler.LineTemplateHandler;
import com.yanggang.io.FileIoObj;
import com.yanggang.service.TemplateEngineService;

public class App {

    public static void main(String[] args) {

        System.out.println("템플릿 엔진 Start...");
        FileIoObj fileIoObj = new FileIoObj();
        LineTemplateHandler lineTemplateHandler = new LineTemplateHandler();
        ForTemplateHandler forTemplateHandler = new ForTemplateHandler();

        TemplateEngineService templateEngineService
                = new TemplateEngineService(fileIoObj, lineTemplateHandler, forTemplateHandler);

        templateEngineService.run();
        System.out.println("템플릿 변환 End...!");
    }
}
