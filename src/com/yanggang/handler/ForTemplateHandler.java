package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.yanggang.utils.Utils.removeSpecialTag;

public class ForTemplateHandler extends LineHandler {

    public String parsingFor(JSONObject userObj, List<String> forTemplateList) {

        int forTemplateCount = 0;
        String templateStr = null;
        String exchangeStr = null;
        Object forTemplateObj = null;
        StringBuilder forTemplateResult = new StringBuilder();

        for (String forTemplate : forTemplateList) {

            if (forTemplate.startsWith("<? for")) {
                String[] forTemplateArr = forTemplate.split(" in ");

                // exchangeStr = forTemplateArr[0].replaceAll("\\<\\? for", "").replaceAll("\\p{Z}", "");
                // forTemplateStr = forTemplateArr[1].replaceAll("\\?\\>", "").replaceAll("\\p{Z}", "");

                exchangeStr = removeSpecialTag(forTemplateArr[0]).replaceAll("for", "");
                templateStr = removeSpecialTag(forTemplateArr[1]).replaceAll("\\.\\*", "");

                forTemplateObj = getForTemplateObj(userObj, templateStr);

                if (forTemplateObj instanceof JSONArray) {
                    forTemplateCount = ((JSONArray) forTemplateObj).size();
                } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
                    forTemplateCount = 1;
                }

            } else if (!forTemplate.equals("<? endfor ?>")) {

                for (int i = 0; i < forTemplateCount; i++) {
                    // Address : <?= ADDR.addr1?> <?= ADDR.addr2?>\n
                    String exchangedTemplate = null;
                    if (forTemplateObj instanceof JSONArray) {
                        exchangedTemplate = forTemplate.replaceAll(exchangeStr, templateStr + "." + i);
                    } else if (forTemplateObj instanceof JSONObject || forTemplateObj instanceof String) {
                        exchangedTemplate = forTemplate.replaceAll(exchangeStr, templateStr);
                    }
                    // String parsingResult = parsingLine(userObj, exchangedTemplate);

                    // forTemplateResult.append(parsingResult).append(System.getProperty("line.separator"));
                    // forTemplateResult.append(parsingResult);
                }
            }
        }

        return forTemplateResult.toString();
    }


    public Object getForTemplateObj(JSONObject userObj, String forTemplateStr) {

        Object curObj = userObj;

        String removedParsingStr = forTemplateStr.replaceAll("\\?\\>", "");
        String parsingStr = removedParsingStr.replaceAll("\\\\n", "");
        String[] parsingInfo = parsingStr.split("\\.");

        Queue<String> parsingInfoQueue = new LinkedList<String>(Arrays.asList(parsingInfo));

        while (!parsingInfoQueue.isEmpty()) {
            String curStr = parsingInfoQueue.poll().replaceAll("\\p{Z}", "");

            if (curStr.equals("USER") || curStr.equals("USERS")) continue;

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
