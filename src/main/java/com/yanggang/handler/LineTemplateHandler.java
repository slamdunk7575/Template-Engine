package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

import static com.yanggang.utils.Utils.removeSpecialTag;
import static com.yanggang.utils.Utils.isStringUpperCase;

public class LineTemplateHandler implements TemplateHandler {

    @Override
    public String convertTemplateToData(JSONObject userObj, String templateLine) {

        StringBuilder result = new StringBuilder();
        String[] templateArr = templateLine.split("<\\?=");


        IntStream.rangeClosed(0, templateArr.length - 1).forEach(i -> {

            String templateStr = templateArr[i];

            if (templateArr[i].contains(":")) {
                result.append(templateArr[i]);
                return;

            } else if (templateArr[i].contains("?>")) {

                String parsingStr = removeSpecialTag(templateStr);
                String[] parsingArr = parsingStr.split("\\.");

            /*String[] parsingArr = Arrays.stream(templateArr)
                    .filter(str -> str.contains("?>"))
                    .map(str -> removeSpecialTag(str).split("\\."))
                    .flatMap(x -> Arrays.stream(x))
                    .toArray(String[]::new);*/

                Queue<String> parsingStrQueue = new LinkedList<>(Arrays.asList(parsingArr));
                Object curObj = findObjType(userObj, parsingStrQueue);

                if (i < templateArr.length - 1) {
                    result.append(curObj).append(" ");
                } else {
                    result.append(curObj).append(System.getProperty("line.separator"));
                }
            }
        });



        /*for (int j = 0; j < templateArr.length; j++) {

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
        }*/


        // result.append(System.getProperty("line.separator"));
        return result.toString();
    }


    @Override
    public Object findObjType(Object curObj, Queue<String> parsingStrQueue) {

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
        return curObj;
    }

}
