package com.yanggang.service;

import com.yanggang.io.FileIoObj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TemplateEngineService implements TemplateEngine {

    private FileIoObj fileIo;

    public TemplateEngineService(FileIoObj fileIo) {
        this.fileIo = fileIo;
    }

    // 템플릿 문자를 보고 JSON 데이터에서 해당 문자를 파싱해옴
    @Override
    public String combine(List<String> templateList, String inputData) {

        StringBuilder result = new StringBuilder();

        try {
            JSONParser jsonParse = new JSONParser();
            JSONArray userArray = (JSONArray) jsonParse.parse(inputData);

            for (int i = 0; i < userArray.size(); i++) {

                JSONObject userObj = (JSONObject) userArray.get(i);

                // while (!templateQueue.isEmpty()) {
                for (String templateLine : templateList) {

                    // 템플릿 데이터를 라인별로 담은 Queue에서 한줄 가져옴
                    // String templateLine = templateQueue.poll();
                    String[] templateArr = templateLine.split("<\\?=");

                    String lineParsingResult = parsingData(userObj, templateArr);
                    result.append(lineParsingResult).append(System.getProperty("line.separator"));
                }

                // result.append("\n").append(System.getProperty("line.separator"));
                result.append(System.getProperty("line.separator"));
            }

            System.out.println(result.toString());


            /*for (int i = 0; i < userArray.size(); i++) {

                // USER.info.name.family
                for (String temStr : templateStrArr) {
                    JSONObject userObj = (JSONObject) userArray.get(i);
                }

                System.out.println("======== user : " + i + " ========");
                JSONObject userObj = (JSONObject) userArray.get(i);
                JSONObject infoObj = (JSONObject) userObj.get("info");
                System.out.println(userObj.get("info"));

                JSONObject nameObj = (JSONObject) infoObj.get("name");
                System.out.println(nameObj.get("family"));
                System.out.println(nameObj.get("given"));

                JSONArray addrsArr = (JSONArray) infoObj.get("addrs");
                System.out.println(((JSONObject) addrsArr.get(0)).get("addr1"));

                JSONObject membershipObj = (JSONObject) userObj.get("membership");
                System.out.println(userObj.get("membership"));
            }*/


        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return result.toString();
    }


    public String parsingData(JSONObject userObj, String[] templateArr) {

        StringBuilder result = new StringBuilder();

        // for (String templateStr : templateArr) {
        for (int i = 0; i < templateArr.length; i++) {
            String templateStr = templateArr[i];

            if (templateStr.contains(":")) {
                result.append(templateStr);
            } else if (templateStr.equals("\n")) {
                result.append("\n");
            } else if (templateStr.contains("?>")) {

                //(중요) Template에서 JSON 관련 정보는 어떻게 파싱해야 하는지에 대한 정보를 나타냄.
                String removedParsingStr = templateStr.replaceAll("\\?\\>", "");
                String parsingStr = removedParsingStr.replaceAll("\\\\n", "");
                String[] parsingInfo = parsingStr.split("\\.");

                Queue<String> parsingInfoQueue = new LinkedList<String>(Arrays.asList(parsingInfo));

                Object curObj = userObj;
                while (!parsingInfoQueue.isEmpty()) {
                    // String curStr = parsingInfoQueue.poll().trim();
                    String curStr = parsingInfoQueue.poll().replaceAll("\\p{Z}", "");

                    if (curStr.equals("USER")) continue;

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
        }

        return result.toString();
    }


    // 다음 문자열이 숫자인자 확인하는 함수
    public boolean isInteger(String str) {
        int size = str.length();
        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return size > 0;
    }


    @Override
    public void run() {

        List<String> templateList = fileIo.readTemplate();
        String inputData = fileIo.readData();

        String result = combine(templateList, inputData);
        fileIo.writeData(result);

    }


}

