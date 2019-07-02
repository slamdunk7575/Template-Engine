package com.yanggang.service;

import com.yanggang.handler.ForTemplateHandler;
import com.yanggang.handler.LineHandler;
import com.yanggang.io.FileIoObj;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

import static com.yanggang.utils.Utils.removeSpecialTag;


public class TemplateEngineService implements TemplateEngine {

    private FileIoObj fileIo;
    private LineHandler lineHandler;
    private ForTemplateHandler forTemplateHandler;

    public TemplateEngineService(FileIoObj fileIo, LineHandler lineHandler, ForTemplateHandler forTemplateHandler) {
        this.fileIo = fileIo;
        this.lineHandler = lineHandler;
        this.forTemplateHandler = forTemplateHandler;
    }

    // 템플릿 문자를 USER 데이터로 변환
    public String convert(List<String> templateList, String inputData) {

        StringBuilder result = new StringBuilder();

        try {
            JSONParser jsonParse = new JSONParser();
            JSONArray userArray = (JSONArray) jsonParse.parse(inputData);

            boolean notTemplateCheck = false;
            for (int i = 0; i <userArray.size(); i++) {

                JSONObject userObj = (JSONObject) userArray.get(i);

                List<String> forTemplateList = new ArrayList<String>();
                boolean forTemplateCheck = false;

                for (int j = 0; j < templateList.size(); j++) {

                    String templateLine = templateList.get(j);

                    if(templateLine.equals("\\n")) result.append(System.getProperty("line.separator"));

                    if(!templateLine.contains("<?") || !templateLine.contains("?>")){
                        if(!notTemplateCheck && !templateLine.equals("\\n")) {
                            result.append(removeSpecialTag(templateLine))
                                    .append(System.getProperty("line.separator"));
                            notTemplateCheck = true;
                        }
                        continue;
                    }

                    if (templateLine.equals("")) continue;

                    if (templateLine.startsWith("<? for")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = true;

                    } else if (templateLine.equals("<? endfor ?>")) {
                        forTemplateList.add(templateLine);
                        forTemplateCheck = false;

                        String forParsingResult = forTemplateHandler.parsingFor(userObj, forTemplateList);
                        result.append(forParsingResult);

                    } else if (forTemplateCheck) {
                        forTemplateList.add(templateLine);

                    } else {
                        String lineParsingResult = lineHandler.parsingLine(userObj, templateLine);
                        result.append(lineParsingResult);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

}

