package com.pccw.bpss.qa.automation.data;

import java.io.File;

/**
 * Created by FaustineP on 22/02/2017.
 */
public class Constant {

    /**
     * Location of available drivers FireFox,Internet Explorer and Chrome
     */
    public static final String FILE_PATH_GECKO_DRIVER_EXE = System.getProperty("user.dir").concat(File.separator).
            concat("conf").concat(File.separator).concat("drivers").concat(File.separator).concat("geckodriver.exe");

    public static final String DRIVER_IE_X64 = System.getProperty("user.dir").concat(File.separator).
            concat("conf").concat(File.separator).concat("drivers").concat(File.separator).concat("iedriver64.exe");

    public static final String FILE_PATH_DRIVER_IE_X32_EXE = System.getProperty("user.dir").concat(File.separator).
            concat("conf").concat(File.separator).concat("drivers").concat(File.separator).concat("iedriver32.exe");

    public static final String FILE_PATH_DRIVER_IE_X64_EXE = System.getProperty("user.dir").concat(File.separator).
            concat("conf").concat(File.separator).concat("drivers").concat(File.separator).concat("iedriver64.exe");

    public static final String FILE_PATH_CHROME_DRIVER_EXE = System.getProperty("user.dir").concat(File.separator).
            concat("conf").concat(File.separator).concat("drivers").concat(File.separator).concat("chromedriver.exe");

    public static final String PROFILE_PROP_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("profiles");

    public static final String TEST_CASE_PREFIX_NAME = "TC-";
    public static final String TEST_SCRIPT_PREFIX_NAME = "TS-";
    public static final String TEST_FILE_EXTENSION = ".xlsx";
    /**
     * Base path of input test case for every service
     */
    public static final String INPUT_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("input");

    /**
     * Base path of output report
     */
    public static final String OUTPUT_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("output").concat(File.separator).concat("qa").concat(File.separator).concat("report");


    public static final String CONFIG_BASE_PATH_FOLDER =System.getProperty("user.dir").concat(File.separator)
            .concat("conf");


    /**
     * Key properties for drivers
     */
    public static final String KEY_GECKO_DRIVER = "webdriver.gecko.driver";
    public static final String KEY_IE_DRIVE = "webdriver.ie.driver";
    public static final String KEY_CHROME_DRIVER = "webdriver.chrome.driver";

    public static final String LOCAL_URL = "http://10.37.131.64:9081/appExt/loginExternal.do";

    /**
     * Column index for Test script.xlsx
     */
    public static final int COL_TEST_CASE_ID = 0;

    public static final int COL_TEST_DESCRIPTION = 1;

    public static final int COL_TEST_PAGE_OBJECT = 2;

    public static final int COL_TEST_ACTION_KEYWORD = 3;

    public static final int COL_TEST_INPUT_DATA = 4;
}
