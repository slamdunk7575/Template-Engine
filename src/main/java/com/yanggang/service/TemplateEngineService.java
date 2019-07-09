package com.yanggang.service;

import com.yanggang.handler.ForTemplateHandler;
import com.yanggang.handler.LineTemplateHandler;
import com.yanggang.io.FileIoObj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.yanggang.utils.Utils.removeSpecialTag;


public class TemplateEngineService implements TemplateEngine {

    private FileIoObj fileIo;
    private LineTemplateHandler lineTemplateHandler;
    private ForTemplateHandler forTemplateHandler;

    public TemplateEngineService(FileIoObj fileIo, LineTemplateHandler lineTemplateHandler, ForTemplateHandler forTemplateHandler) {
        this.fileIo = fileIo;
        this.lineTemplateHandler = lineTemplateHandler;
        this.forTemplateHandler = forTemplateHandler;
    }

    // 템플릿 문자를 USER 데이터로 변환
    public String convert(List<String> templateList, String inputData) {

        StringBuilder result = new StringBuilder();

        try {
            JSONParser jsonParse = new JSONParser();
            JSONArray userArray = (JSONArray) jsonParse.parse(inputData);

            for (int i = 0; i <userArray.size(); i++) {

                JSONObject userObj = (JSONObject) userArray.get(i);

                List<String> forTemplateList = new ArrayList<String>();
                boolean forTemplateCheck = false;

                for (int j = 0; j < templateList.size(); j++) {

                    String templateLine = templateList.get(j);

                    if(checkTemplate(result, templateLine)) continue;

                    if (findForTemplate(templateLine, line -> line.startsWith("<? for"))) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = true;

                    } else if (findForTemplate(templateLine, line -> line.equals("<? endfor ?>"))) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = false;

                        String forParsingResult = forTemplateHandler.convertForTemplateToData(userObj, forTemplateList);
                        result.append(forParsingResult);

                    } else if (forTemplateCheck) {
                        forTemplateList.add(templateLine);

                    } else {
                        String lineParsingResult = lineTemplateHandler.convertTemplateToData(userObj, templateLine);
                        result.append(lineParsingResult);
                    }
                }
            }


            /*for (int i = 0; i <userArray.size(); i++) {

                JSONObject userObj = (JSONObject) userArray.get(i);

                List<String> forTemplateList = new ArrayList<String>();
                boolean forTemplateCheck = false;

                for (int j = 0; j < templateList.size(); j++) {

                    String templateLine = templateList.get(j);

                    if(checkTemplate(result, templateLine)) continue;

                    if (templateLine.startsWith("<? for")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = true;

                    } else if (templateLine.equals("<? endfor ?>")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = false;

                        String forParsingResult = forTemplateHandler.convertForTemplate(userObj, forTemplateList);
                        result.append(forParsingResult);

                    } else if (forTemplateCheck) {
                        forTemplateList.add(templateLine);

                    } else {
                        String lineParsingResult = lineTemplateHandler.convertTemplateToData(userObj, templateLine);
                        result.append(lineParsingResult);
                    }
                }
            }*/


        } catch (Exception e) {
            consumer.accept(e);
        }

        return result.toString();
    }


    @Override
    public void run() {

        List<String> templateList = fileIo.readTemplate();
        String inputData = fileIo.readData();

        String result = convert(templateList, inputData);
        fileIo.writeData(result);
    }


    public boolean checkTemplate(StringBuilder result, String templateLine) {

        boolean templateCheckResult = false;

        if (!templateLine.contains("<?") || !templateLine.contains("?>") || templateLine.equals("")) {
            if (!result.toString().contains(removeSpecialTag(templateLine)) && !templateLine.equals("\\n")) {
                result.append(removeSpecialTag(templateLine))
                        .append(System.getProperty("line.separator"))
                        .append(System.getProperty("line.separator"));

            } else if (templateLine.equals("\\n")) {
                result.append(System.getProperty("line.separator"));
            }
            templateCheckResult = true;
        }

        return templateCheckResult;
    }



    public boolean findForTemplate(String templateLine, Predicate<String> predicate) {

        if (predicate.test(templateLine)) {
            return true;
        }
        return false;
    }



    // PrintStackTrace 출력
    Consumer<Exception> consumer = e -> {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        System.out.println(errors.toString());
    };

}

