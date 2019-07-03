package com.yanggang.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FileIoObj {

    // 사용자 정보 읽기
    public String readData() {

        StringBuilder sb = new StringBuilder();

        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufReader = null;

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("data.txt");
            streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufReader = new BufferedReader(streamReader);

            String line = "";
            while ((line = bufReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            bufReader.close();

        } catch (FileNotFoundException e) {
            consumer.accept(e);
        } catch (IOException e) {
            consumer.accept(e);
        } catch (Exception e) {
            consumer.accept(e);
        } finally {
            try {
                bufReader.close();
                streamReader.close();
                inputStream.close();
            } catch (Exception e) {
                consumer.accept(e);
            }
        }

        return sb.toString();
    }


    // 템플릿 파일 읽기
    public List<String> readTemplate() {

        List<String> templateList = new ArrayList<String>();

        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufReader = null;

        try {

            inputStream = getClass().getClassLoader().getResourceAsStream("template.txt");
            streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufReader = new BufferedReader(streamReader);

            String line = "";
            while ((line = bufReader.readLine()) != null) {
                if (!line.equals("")) templateList.add(line);
            }

        } catch (FileNotFoundException e) {
            consumer.accept(e);
        } catch (IOException e) {
            consumer.accept(e);
        } catch (Exception e) {
            consumer.accept(e);

        } finally {
            try {
                bufReader.close();
                streamReader.close();
                inputStream.close();
            } catch (Exception e) {
                consumer.accept(e);
            }
        }

        return templateList;
    }


    // 결과 출력
    public void writeData(String outResult) {

        File file = new File("output.txt");

        FileWriter writer = null;
        BufferedWriter bufWriter = null;

        try {
            writer = new FileWriter(file);
            bufWriter = new BufferedWriter(writer);

            bufWriter.write(outResult);
            bufWriter.flush();

        } catch (Exception e) {
            consumer.accept(e);
        } finally {
            try {
                if (bufWriter != null) bufWriter.close();
                if (writer != null) writer.close();
            } catch (Exception e) {
                consumer.accept(e);
            }
        }
    }

    // PrintStackTrace 출력
    Consumer<Exception> consumer = e -> {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        System.out.println(errors.toString());
    };
}
