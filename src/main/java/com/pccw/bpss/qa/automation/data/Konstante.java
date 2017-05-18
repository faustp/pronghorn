package com.pccw.bpss.qa.automation.data;

import java.io.File;

/**
 * Created by FaustineP on 17/05/2017.
 */
public class Konstante {
    //filename prefixes
    public static final String TEST_CASE_PREFIX_NAME = "TC-";
    public static final String TEST_SCRIPT_PREFIX_NAME = "TS-";
    public static final String TEST_FILE_EXTENSION = ".xlsx";

    //test script columns
    public static final int COL_TEST_CASE_ID = 0;
    public static final int COL_TEST_DESCRIPTION = 1;
    public static final int COL_TEST_PAGE_OBJECT = 2;
    public static final int COL_TEST_ACTION_KEYWORD = 3;
    public static final int COL_TEST_INPUT_DATA = 4;

    //file paths
    public static final String PROFILE_PROP_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("profiles");
    public static final String INPUT_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("input");
    public static final String OUTPUT_BASE_PATH = System.getProperty("user.dir").concat(File.separator)
            .concat("output").concat(File.separator).concat("qa").concat(File.separator).concat("report");
    public static final String CONFIG_BASE_PATH_FOLDER =System.getProperty("user.dir").concat(File.separator)
            .concat("conf");


}
