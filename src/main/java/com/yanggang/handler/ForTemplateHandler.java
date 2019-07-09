package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.yanggang.utils.Utils.removeSpecialTag;


public class ForTemplateHandler {

    private TemplateHandler templateHandler;

    public ForTemplateHandler(TemplateHandler templateHandler) {
        this.templateHandler = templateHandler;
    }

    public String convertForTemplate(JSONObject userObj, List<String> forTemplateList) {

        StringBuilder forTemplateResult = new StringBuilder();

        Map<String, Object> templateInfo = forTemplateList.stream()
                .filter(forTemplateLine -> forTemplateLine.startsWith("<? for"))
                .map(forTemplateLine -> getTemplateInfo(userObj, forTemplateLine))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));


        forTemplateList.stream()
                .filter(forTemplateLine -> !forTemplateLine.startsWith("<? for"))
                .filter(forTemplateLine -> !forTemplateLine.equals("<? endfor ?>"))
                .forEach(forTemplateLine -> {

                    Integer forTemplateCount = (Integer) templateInfo.get("forTemplateCount");

                    IntStream.range(0, forTemplateCount).forEach(i -> {
                        String exchangedTemplate = exchangeTemplate(templateInfo, forTemplateLine, i);
                        // 변환된 템플릿 문자열을 LineTemplateHandler에게 요청해서 파싱함.
                        // 예: Membership Id : <?= USERS.membership.id ?>\n
                        String parsingResult = templateHandler.convertTemplateToData(userObj, exchangedTemplate);
                        forTemplateResult.append(parsingResult);
                    });
                });

        return forTemplateResult.toString();
    }



    /**
     * 예: <? for ID in USERS.*.membership.id ?> 템플릿의 불필요한 태그를 제거하고 for 템플릿 관련 정보를 파싱함
     *
     * @param userObj         USERS JSONObject
     * @param forTemplateLine for문의 정보를 담고있는 템플릿
     *
     * @return templateMap
     * exchangeStr: ID
     * templateStr: USERS.membership.id
     * forTemplateObj: 12345
     * forTemplateCount: 1
     *
     */
    public Map<String, Object> getTemplateInfo(Object userObj, String forTemplateLine) {

        HashMap<String, Object> templateMap = new HashMap<>();
        Integer forTemplateCount = null;

        String[] forTemplateArr = forTemplateLine.split(" in ");
        String exchangeStr = removeSpecialTag(forTemplateArr[0]).replaceAll("for", "");
        templateMap.put("exchangeStr", exchangeStr);

        String templateStr = removeSpecialTag(forTemplateArr[1]).replaceAll("\\.\\*", "");
        templateMap.put("templateStr", templateStr);

        String[] parsingInfo = removeSpecialTag(templateStr).split("\\.");
        Queue<String> parsingInfoQueue = new LinkedList<String>(Arrays.asList(parsingInfo));

        Object forTemplateObj = templateHandler.findObjType(userObj, parsingInfoQueue);
        templateMap.put("forTemplateObj", forTemplateObj);

        if (forTemplateObj instanceof JSONArray) {
            forTemplateCount = ((JSONArray) forTemplateObj).size();
        } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
            forTemplateCount = new Integer(1);
        }

        templateMap.put("forTemplateCount", forTemplateCount);

        return templateMap;
    }


    /**
     * 템플릿 관련 정보들을 가지고 템플릿 정보를 변환하고 파싱해옴
     *
     * @param templateInfo  템플릿 관련 정보들
     * @param forTemplateLine Membership Id : <?= ID ?>\n
     * @param i 반복횟수(=forTemplateCount)
     *
     * @return exchangedTemplate
     * Membership Id : <?= USERS.membership.id ?>\n
     * Membership Id : <?= USERS.membership.id.0 ?>\n
     */
    public String exchangeTemplate(Map<String, Object> templateInfo, String forTemplateLine, int i) {

        String exchangedTemplate = null;
        Object forTemplateObj = templateInfo.get("forTemplateObj");
        String exchangeStr = (String) templateInfo.get("exchangeStr");
        String templateStr = (String) templateInfo.get("templateStr");

        if (forTemplateObj instanceof JSONArray) {
            exchangedTemplate = forTemplateLine.replaceAll(exchangeStr, templateStr + "." + i);
        } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
            exchangedTemplate = forTemplateLine.replaceAll(exchangeStr, templateStr);
        }

        return exchangedTemplate;
    }


}
