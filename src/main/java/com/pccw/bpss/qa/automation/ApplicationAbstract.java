package com.pccw.bpss.qa.automation;

import com.pccw.bpss.qa.automation.action.ActionKeywords;
import com.pccw.bpss.qa.automation.data.Constant;
import com.pccw.bpss.qa.automation.data.FileType;
import com.pccw.bpss.qa.automation.data.TestCaseData;
import com.pccw.bpss.qa.automation.exception.InputFileException;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import com.pccw.bpss.qa.automation.utility.XLSUtility;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.pccw.bpss.qa.automation.data.Constant.*;
import static com.pccw.bpss.qa.automation.data.Constant.PROFILE_PROP_BASE_PATH;

/**
 * Created by FaustineP on 24/03/2017.
 */
public abstract class ApplicationAbstract {

    final static Logger logger = Logger.getLogger(ApplicationAbstract.class);

    public static Properties SELECTOR;
    public static Map<String, String> REPOSITORY;
    protected static ActionKeywords actionKeywords;
    protected static String actionKeyword;
    protected static String objectKey;
    protected static String inputData;
    protected static String testCaseId;
    protected static Method methods[];
    protected static String testStepDescription;
    protected static ExtentReports report;
    protected static ExtentTest test;
    protected static XSSFWorkbook testCaseBook;
    protected static TestCaseData testCaseData;

    protected void executeTestScript() throws Exception {
        logger.info("Executing test script # " + testCaseId);
        for (int row = 1; row < XLSUtility.getRowCount(testCaseId); row++) {
            actionKeyword = XLSUtility.getCellData(row, COL_TEST_ACTION_KEYWORD, testCaseId);
            objectKey = XLSUtility.getCellData(row, COL_TEST_PAGE_OBJECT, testCaseId);
            inputData = XLSUtility.getCellData(row, COL_TEST_INPUT_DATA, testCaseId);
            testStepDescription = XLSUtility.getCellData(row, COL_TEST_DESCRIPTION, testCaseId);
            execute_Actions();
        }
    }

    protected static void execute_Actions() throws InvocationTargetException, IllegalAccessException {
        for (Method aMethod : methods) {
            if (aMethod.getName().equals(actionKeyword)) {
                aMethod.invoke(actionKeyword, test, testStepDescription, testCaseId, objectKey, inputData);
                break;
            }
        }
    }

    protected static void initializeRepository() {
        logger.info("Initializing repository...");
        REPOSITORY = new HashMap<>();
    }

    protected static TestCaseData loadTestCaseData(File serviceFolder) throws
            IOException, InvalidFormatException, InputFileException, XLSUtilityException {
        logger.info("Loading test cases...");
        Map<String, Map<String, String>> data = new LinkedHashMap<>();
        List<File> testCases = XLSUtility.getFiles(serviceFolder.getCanonicalPath(), FileType.TEST_CASE);
        if (testCases.size() < 1) return null;
        File testCase = testCases.get(0);
        testCaseBook = new XSSFWorkbook(testCase);
        int tcSheetCount = testCaseBook.getNumberOfSheets();
        for (int tcSheetIndex = 0; tcSheetIndex < tcSheetCount; tcSheetIndex++) {
            XSSFSheet tcSheet = testCaseBook.getSheetAt(tcSheetIndex);
            int tcSheetRowCount = tcSheet.getLastRowNum() + 1;
            Map<String, String> dataContent = new LinkedHashMap<>();
            for (int rowIndex = 1; rowIndex < tcSheetRowCount; rowIndex++) {
                if (rowIndex % 2 != 0) {
                    if (tcSheet.getRow(rowIndex).getCell(9) == null ||
                            tcSheet.getRow(rowIndex).getCell(9).getStringCellValue().equalsIgnoreCase("N"))
                        continue;
                    String testCaseId = tcSheet.getRow(rowIndex).getCell(0).getStringCellValue();
                    String testCaseObjective = tcSheet.getRow(rowIndex).getCell(1).getStringCellValue();
                    logger.info("Loading " + testCaseId);
                    dataContent.put(testCaseId, testCaseObjective);
                }
            }

            data.put(tcSheet.getSheetName(), dataContent);

        }
        testCaseBook.close();
        return new TestCaseData(data);
    }


    protected static void loadProfile(String args[]) throws IOException {
        if (args == null || args.length < 2)
            throw new MissingFormatArgumentException("Please provide [profile_name] [service_name] as argument");

        String profileName = args[0].trim();
        String serviceName = args[1].trim();

        String globalPropertyPath = PROFILE_PROP_BASE_PATH.concat(File.separator).concat(profileName).
                concat(File.separator).concat("global.properties");
        String servicePropertyPath = PROFILE_PROP_BASE_PATH.concat(File.separator).concat(profileName).
                concat(File.separator).concat(serviceName.concat(".properties"));

        if (!new File(globalPropertyPath).exists()) {
            IOException ioe = new IOException("File does not exists ".concat(globalPropertyPath));
            logger.info(ioe.getMessage());
            throw ioe;
        }
        if (!new File(servicePropertyPath).exists()) {
            IOException ioe = new IOException("File does not exists ".concat(servicePropertyPath));
            logger.info(ioe.getMessage());
            throw ioe;
        }
        FileInputStream globalProp = new FileInputStream(globalPropertyPath);
        FileInputStream serviceProp = new FileInputStream(servicePropertyPath);

        logger.info("Loading property files [" + globalPropertyPath + "," + servicePropertyPath + " ]");
        SELECTOR = new Properties(System.getProperties());
        //Load all the properties from Object Repository property file in to OR object
        SELECTOR.load(globalProp);
        SELECTOR.load(serviceProp);
    }


    protected static void setUpLogger() {
        String propFile = CONFIG_BASE_PATH_FOLDER.concat(File.separator).concat("log4j.properties");
        PropertyConfigurator.configure(propFile);
    }

}
