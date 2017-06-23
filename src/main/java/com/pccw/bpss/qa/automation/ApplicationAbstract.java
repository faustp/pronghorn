package com.pccw.bpss.qa.automation;


import com.pccw.ad.pronghorn.model.exception.TestCaseException;
import com.pccw.ad.pronghorn.model.profile.Selector;
import com.pccw.ad.pronghorn.model.tc.Script;
import com.pccw.ad.pronghorn.model.tc.TestCase;
import com.pccw.bpss.qa.automation.exception.InputFileException;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import com.pccw.bpss.qa.automation.utility.XLSUtility;
import com.relevantcodes.extentreports.ExtentReports;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.pccw.ad.pronghorn.engine.data.FileType.TEST_CASE;
import static com.pccw.bpss.qa.automation.data.Konstante.*;


/**
 * Created by FaustineP on 24/03/2017.
 */
public abstract class ApplicationAbstract {

    final static Logger logger = Logger.getLogger(ApplicationAbstract.class);

    public static Properties SELECTOR;
    protected static String testCaseId;
    protected static ExtentReports report;
    protected static XSSFWorkbook testCaseBook;
    protected static HashSet<TestCase> testCaseData;


    protected static HashSet<TestCase> loadTestCaseData(File serviceFolder) throws
            IOException, InvalidFormatException, InputFileException, XLSUtilityException, TestCaseException {
        logger.info("Loading test cases...");

        HashSet<TestCase> testCases = new HashSet<>();
        List<File> testCasesPath = XLSUtility.getFiles(serviceFolder.getCanonicalPath(), TEST_CASE);

        for (File file : testCasesPath) {
            testCaseBook = new XSSFWorkbook(file);
            int tcSheetCount = testCaseBook.getNumberOfSheets();

            for (int tcSheetIndex = 0; tcSheetIndex < tcSheetCount; tcSheetIndex++) { //sheets ng test case
                XSSFSheet tcSheet = testCaseBook.getSheetAt(tcSheetIndex);
                int tcSheetRowCount = tcSheet.getLastRowNum() + 1;

                for (int rowIndex = 1; rowIndex < tcSheetRowCount; rowIndex++) {
                    if (rowIndex % 2 != 0) {
                        if (tcSheet.getRow(rowIndex).getCell(9) != null && tcSheet.getRow(rowIndex).getCell(9).getStringCellValue().equalsIgnoreCase("Y")) {
                            testCaseId = tcSheet.getRow(rowIndex).getCell(0).getStringCellValue().replaceAll("^Fct/Sys/", "");
                            // XLSUtility.setExcelFile();
                            String testCaseObjective = tcSheet.getRow(rowIndex).getCell(1).getStringCellValue();
                            String testScriptFile = file.getParent().concat(File.separator).concat(TEST_SCRIPT_PREFIX_NAME).
                                    concat(tcSheet.getSheetName()).concat(TEST_FILE_EXTENSION);
                            TestCase testCase = new TestCase.Builder().
                                    addIdentifier(testCaseId).
                                    addObjective(testCaseObjective).
                                    addIsActive(true).
                                    addAuthor(tcSheet.getRow(rowIndex).getCell(6).getStringCellValue()).
                                    addScripts(generateScript(testScriptFile, testCaseId)).
                                    build();
                            testCases.add(testCase);
                        }
                    }
                }
            }

            testCaseBook.close();

        }
        return testCases;
    }

    protected static void setUpLogger() {
        String propFile = CONFIG_BASE_PATH_FOLDER.concat(File.separator).concat("log4j.properties");
        PropertyConfigurator.configure(propFile);
    }

    protected static Selector loadPropertiesData(String args[]) throws IOException {
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

        Selector selector = new Selector();
        HashMap<String, HashMap<String, String>> selectors = new HashMap<>();
        HashMap<String, String> serviceSelector = new HashMap<>();

        Properties p = new Properties();
        for (String key : SELECTOR.stringPropertyNames()) {
            String value = SELECTOR.getProperty(key);
            serviceSelector.put(key, value);
            selectors.put(serviceName, serviceSelector);
        }
        return selector;
    }

    private static List<Script> generateScript(String testScripFile, String sheetName) throws IOException {
        List<Script> scripts = new ArrayList<>();
        XSSFWorkbook testScript = null;
        try {
            testScript = new XSSFWorkbook(new File(testScripFile));

            XSSFSheet tcSheet = testScript.getSheet(sheetName);
            int tcSheetRowCount = tcSheet.getLastRowNum() + 1;
            for (int rowIndex = 1; rowIndex < tcSheetRowCount; rowIndex++) {
                Script script = new Script();
                Selector selector = new Selector();
                selector.setKey(tcSheet.getRow(rowIndex).getCell(COL_TEST_PAGE_OBJECT) == null ? null : tcSheet.getRow(rowIndex).getCell(COL_TEST_PAGE_OBJECT).getStringCellValue());
                selector.setValue((selector.getKey() == null || selector.getKey().isEmpty()) ?
                        "" : SELECTOR.getProperty(selector.getKey()));
                script.setDescription(tcSheet.getRow(rowIndex).getCell(COL_TEST_DESCRIPTION) == null ? null : tcSheet.getRow(rowIndex).getCell(COL_TEST_DESCRIPTION).getStringCellValue());
                script.setSelector(selector);
                script.setAction(tcSheet.getRow(rowIndex).getCell(COL_TEST_ACTION_KEYWORD) == null ? null : tcSheet.getRow(rowIndex).getCell(COL_TEST_ACTION_KEYWORD).getStringCellValue());
                script.setInputData(tcSheet.getRow(rowIndex).getCell(COL_TEST_INPUT_DATA) == null ? null : tcSheet.getRow(rowIndex).getCell(COL_TEST_INPUT_DATA).getStringCellValue());
                scripts.add(script);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (testScript != null) {
                testScript.close();
            }
        }


        return scripts;
    }
}
