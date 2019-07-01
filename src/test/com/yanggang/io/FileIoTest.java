package com.yanggang.io;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FileIoTest {

    FileIoObj fileIoObj = null;

    @Before
    public void 파일_IO_객체_생성() {
        fileIoObj = new FileIoObj();
    }

    @Test
    public void 템플릿_파일_읽기() {
        // Queue<String> templateResult = templateObj.readTemplate();
        List<String> templateResult = fileIoObj.readTemplate();
        assertNotEquals(0, templateResult.size());
    }


    @Test
    public void 데이터_파일_읽기() {
        String dataResult = fileIoObj.readData();
        System.out.println(dataResult);
        assertNotNull(dataResult);
    }
}