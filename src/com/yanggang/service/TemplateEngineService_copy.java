package com.yanggang.service;

import com.yanggang.io.FileIoObj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class TemplateEngineService_copy implements TemplateEngine {

    private FileIoObj fileIo;

    public TemplateEngineService_copy(FileIoObj fileIo) {
        this.fileIo = fileIo;
    }

    // 템플릿 문자를 보고 JSON 데이터에서 해당 문자를 파싱해옴
    public String combine(List<String> templateList, String inputData) {

        StringBuilder result = new StringBuilder();

        try {
            JSONParser jsonParse = new JSONParser();
            JSONArray userArray = (JSONArray) jsonParse.parse(inputData);

            boolean onPrintTemplateCheck = false;
            int onePrintTemplateLine = 0;

            for (int i = 0; i < userArray.size(); i++) {

                JSONObject userObj = (JSONObject) userArray.get(i);
                List<String> forTemplateList = new ArrayList<String>();
                boolean forTemplateCheck = false;


                // for (String templateLine : templateList) {
                for (int j = 0; j < templateList.size(); j++) {
                    String templateLine = templateList.get(j);

                    if (templateLine.equals("") || (onPrintTemplateCheck && onePrintTemplateLine == j)) continue;

                    if (templateLine.startsWith("<? for")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = true;

                        if (templateLine.contains("USERS")) {
                            if (j > 0) onePrintTemplateLine = j - 1;
                            onPrintTemplateCheck = true;
                        }

                    } else if (templateLine.equals("<? endfor ?>")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = false;

                        String forParsingResult = forTemplateHandler(userObj, forTemplateList);
                        result.append(forParsingResult);
                        // result.append(forParsingResult).append(System.getProperty("line.separator"));

                    } else if (forTemplateCheck) {
                        forTemplateList.add(templateLine);

                    } else {
                        String lineParsingResult = parsingData(userObj, templateLine);
                        // result.append(lineParsingResult);
                        result.append(lineParsingResult).append(System.getProperty("line.separator"));
                    }

                }

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


    public String parsingData(JSONObject userObj, String templateLine) {

        String[] templateArr = templateLine.split("<\\?=");
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
        }

        return result.toString();
    }


    public String forTemplateHandler(JSONObject userObj, List<String> forTemplateList) {

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
                    String parsingResult = parsingData(userObj, exchangedTemplate);
                    // forTemplateResult.append(parsingResult).append(System.getProperty("line.separator"));
                    forTemplateResult.append(parsingResult);
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


    public String removeSpecialTag(String templateStr) {
        String removedSpecialTag = templateStr.replaceAll("\\<\\?", "")
                .replaceAll("\\?\\>", "")
                .replaceAll("\\p{Z}", "")
                .replaceAll("\\\\n", "");
        return removedSpecialTag;
    }


    @Override
    public void run() {

        List<String> templateList = fileIo.readTemplate();
        String inputData = fileIo.readData();

        String result = combine(templateList, inputData);
        fileIo.writeData(result);
    }

}

