package com.yanggang.utils;

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


    public static String removeSpecialTag(String templateStr) {
        String removedSpecialTag = templateStr
                .replaceAll("\\<\\?\\=", "")
                .replaceAll("\\<\\?", "")
                .replaceAll("\\?\\>", "")
                .replaceAll("\\p{Z}", "")
                .replaceAll("\\\\n", "");
        return removedSpecialTag;
    }


    public static boolean isStringUpperCase(String str){

        char[] charArray = str.toCharArray();

        for(int i=0; i < charArray.length; i++){
            if( !Character.isUpperCase( charArray[i] ))
                return false;
        }

        return true;
    }

}
