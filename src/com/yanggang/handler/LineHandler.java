package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static com.yanggang.utils.Utils.removeSpecialTag;
import static com.yanggang.utils.Utils.isStringUpperCase;

public class LineHandler {

    public String parsingLine(JSONObject userObj, String templateLine) {

        StringBuilder result = new StringBuilder();

        String[] templateArr = templateLine.split("<\\?=");

        for (int j = 0; j < templateArr.length; j++) {

            String templateStr = templateArr[j];

            if (templateStr.contains(":")) {
                result.append(templateStr);

            } else if (templateStr.contains("?>")) {

                String parsingStr = removeSpecialTag(templateStr);
                String[] parsingArr = parsingStr.split("\\.");

                Queue<String> parsingStrQueue = new LinkedList<String>(Arrays.asList(parsingArr));
                Object curObj = userObj;

                while (!parsingStrQueue.isEmpty()) {
                    String curStr = parsingStrQueue.poll();

                    if (isStringUpperCase(curStr)) continue;

                    if (curObj instanceof JSONObject) {
                        curObj = ((JSONObject) curObj).get(curStr);
                    } else if (curObj instanceof JSONArray) {
                        if (((JSONArray) curObj).isEmpty()) {
                            curObj = "?";
                        } else {
                            curObj = ((JSONArray) curObj).get(Integer.parseInt(curStr));
                        }
                    } else if (parsingStrQueue.size() == 1) {
                        curObj = ((JSONObject) curObj).get(curStr);
                    }
                }

                if (j < templateArr.length - 1) {
                    result.append(curObj).append(" ");
                } else {
                    result.append(curObj).append(System.getProperty("line.separator"));
                }
            }
        }

        result.append(System.getProperty("line.separator"));
        return result.toString();
    }


}
