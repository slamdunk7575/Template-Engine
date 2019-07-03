package com.yanggang.handler;

import org.json.simple.JSONObject;

public interface TemplateHandler {

    // 템플릿 문자를 데이터로 변환
    String convertTemplateToData(JSONObject userObj, String templateLine);
}
