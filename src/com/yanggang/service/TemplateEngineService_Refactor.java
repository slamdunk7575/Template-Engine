package com.yanggang.service;

import com.yanggang.handler.ForTemplateHandler;
import com.yanggang.handler.LineHandler;
import com.yanggang.io.FileIoObj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;


public class TemplateEngineService_Refactor implements TemplateEngine {

    private FileIoObj fileIo;
    private LineHandler lineHandler;
    private ForTemplateHandler forTemplateHandler;

    public TemplateEngineService_Refactor(FileIoObj fileIo, LineHandler lineHandler, ForTemplateHandler forTemplateHandler) {
        this.fileIo = fileIo;
        this.lineHandler = lineHandler;
        this.forTemplateHandler = forTemplateHandler;
    }

    // 템플릿 문자를 USER 데이터로 변환
    public String convert(List<String> templateList, String inputData) {

        // 템필릿 문자를
        String result = null;

        try {
            JSONParser jsonParse = new JSONParser();
            JSONArray userArray = (JSONArray) jsonParse.parse(inputData);

            result = lineHandler.parsingLine(userArray, templateList);




        } catch (Exception e) {
            e.printStackTrace();
        }


        /*try {
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

                        String forParsingResult = forTemplateHandler.parsingFor(userObj, forTemplateList);
                        result.append(forParsingResult);
                        // result.append(forParsingResult).append(System.getProperty("line.separator"));

                    } else if (forTemplateCheck) {
                        forTemplateList.add(templateLine);

                    } else {
                        String lineParsingResult = lineHandler.parsingLine(userObj, templateLine);
                        // result.append(lineParsingResult);
                        result.append(lineParsingResult).append(System.getProperty("line.separator"));
                    }

                }

                result.append(System.getProperty("line.separator"));
            }

            System.out.println(result.toString());

        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }*/

        return result;
    }







    @Override
    public void run() {

        List<String> templateList = fileIo.readTemplate();
        String inputData = fileIo.readData();

        String result = convert(templateList, inputData);
        fileIo.writeData(result);
    }

}

