package com.yanggang.service;

import java.util.List;
import java.util.Queue;

public interface TemplateEngine {

    // Data를 Template와 결합한다.
    // public String combine(Queue<String> templateQueue, String inputData);
    public String combine(List<String> templateList, String inputData);

    // Template Engine 실행
    public void run();

}
