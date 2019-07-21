package com.yanggang.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

public class Utils {

    // 다음 문자열이 숫자인지 확인하는 함수
    public static boolean isInteger(String str) {
        int size = str.length();
        for (int i = 0; i < size; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return size > 0;
    }

    
    // 특수문자 제거하는 함수
    public static String removeSpecialTag(String templateStr) {
        String removedSpecialTag = templateStr
                .replaceAll("\\<\\?\\=", "")
                .replaceAll("\\<\\?", "")
                .replaceAll("\\?\\>", "")
                .replaceAll("\\p{Z}", "")
                .replaceAll("\\\\n", "");
        return removedSpecialTag;
    }


    // 문자열이 대문자인지 판별하는 함수
    public static boolean isStringUpperCase(String str){
        char[] charArray = str.toCharArray();
        for(int i=0; i < charArray.length; i++){
            if( !Character.isUpperCase( charArray[i] ))
                return false;
        }
        return true;
    }


    // Exception 출력 함수
    public static void printException(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        System.out.println(errors.toString());
    }

}
