package com.yanggang.handler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.yanggang.utils.Utils.removeSpecialTag;

public class LineHandler {

    public String parsingLine(JSONArray userArray, List<String> templateList) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < userArray.size(); i++) {

            JSONObject userObj = (JSONObject) userArray.get(i);

            for (String templateLine : templateList) {

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

                            if (curStr.equals("USER") || curStr.equals("USERS")) continue;

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
            }
        }





        /*String[] templateArr = templateLine.split("<\\?=");
        StringBuilder result = new StringBuilder();

        if (!templateLine.contains("<?=")) {
            result.append(removeSpecialTag(templateLine)).append("\n");
        }

        // for (String templateStr : templateArr) {
        for (int i = 0; i < templateArr.length; i++) {
            String templateStr = templateArr[i];

            if (templateStr.contains(":")) {
                result.append(templateStr);
            } else if (templateStr.equals("\n")) {
                // result.append("\n");
                continue;
            } else if (templateStr.contains("?>")) {

                //(중요) Template에서 JSON 관련 정보는 어떻게 파싱해야 하는지에 대한 정보를 나타냄.
                String removedParsingStr = templateStr.replaceAll("\\?\\>", "");
                String parsingStr = removedParsingStr.replaceAll("\\\\n", "");
                String[] parsingInfo = parsingStr.split("\\.");

                Queue<String> parsingInfoQueue = new LinkedList<String>(Arrays.asList(parsingInfo));

                Object curObj = userObj;
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

                if (i < templateArr.length - 1) {
                    result.append(curObj).append(" ");
                } else {
                    result.append(curObj).append("\n");
                }
            }
        }*/

        return result.toString();
    }


}
