package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.yanggang.utils.Utils.isStringUpperCase;
import static com.yanggang.utils.Utils.removeSpecialTag;


public class ForTemplateHandler extends LineTemplateHandler {

    public String convertForTemplate(JSONObject userObj, List<String> forTemplateList) {

        int forTemplateCount = 0;
        String templateStr = null;
        String exchangeStr = null;
        Object forTemplateObj = null;
        StringBuilder forTemplateResult = new StringBuilder();

        for (String forTemplateLine : forTemplateList) {

            if (forTemplateLine.startsWith("<? for")) {
                String[] forTemplateArr = forTemplateLine.split(" in ");

                exchangeStr = removeSpecialTag(forTemplateArr[0]).replaceAll("for", "");
                templateStr = removeSpecialTag(forTemplateArr[1]).replaceAll("\\.\\*", "");

                forTemplateObj = getForTemplateObj(userObj, templateStr);

                if (forTemplateObj instanceof JSONArray) {
                    forTemplateCount = ((JSONArray) forTemplateObj).size();
                } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
                    forTemplateCount = new Integer(1);
                }

            } else if (!forTemplateLine.equals("<? endfor ?>")) {

                for (int i = 0; i < forTemplateCount; i++) {
                    String exchangedTemplate = null;
                    if (forTemplateObj instanceof JSONArray) {
                        exchangedTemplate = forTemplateLine.replaceAll(exchangeStr, templateStr + "." + i);
                    } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
                        exchangedTemplate = forTemplateLine.replaceAll(exchangeStr, templateStr);
                    }
                    String parsingResult = convertTemplateToData(userObj, exchangedTemplate);
                    forTemplateResult.append(parsingResult);
                }
            }
        }

        return forTemplateResult.toString();
    }




    public Object getForTemplateObj(JSONObject userObj, String forTemplateStr) {

        Object curObj = userObj;
        String parsingStr = removeSpecialTag(forTemplateStr);
        String[] parsingInfo = parsingStr.split("\\.");

        Queue<String> parsingInfoQueue = new LinkedList<String>(Arrays.asList(parsingInfo));

        while (!parsingInfoQueue.isEmpty()) {
            String curStr = parsingInfoQueue.poll();

            if (isStringUpperCase(curStr)) continue;

            if (curObj instanceof JSONObject) {
                curObj = ((JSONObject) curObj).get(curStr);
            } else if (curObj instanceof JSONArray) {
                if (((JSONArray) curObj).isEmpty()) {
                    curObj = "?";
                } else {
                    curObj = ((JSONArray) curObj).get(Integer.parseInt(curStr));
                }
            } else if (parsingInfoQueue.size() == 1) {
                curObj = ((JSONObject) curObj).get(curStr);
            }
        }

        return curObj;
    }

}
