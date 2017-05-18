package com.pccw.bpss.qa.automation.utility;

import com.pccw.ad.pronghorn.engine.data.FileType;
import com.pccw.bpss.qa.automation.data.Konstante;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.pccw.bpss.qa.automation.data.Konstante.*;

/**
 * Created by FaustineP on 07/03/2017.
 */
public class XLSUtility {

    private static XSSFSheet excelSheet;
    private static XSSFWorkbook excelBook;

    public static void setExcelFile(String filePath) throws XLSUtilityException {
        FileInputStream xlsFis = null;
        try {
            if (!new File(filePath).exists()) throw new XLSUtilityException("");
            xlsFis = new FileInputStream(filePath);
            excelBook = new XSSFWorkbook(xlsFis);
        } catch (XLSUtilityException | IOException e) {
            throw new XLSUtilityException(e.getMessage());
        }
    }

    public static String getCellData(int row, int col, String sheetName) {
        excelSheet = excelBook.getSheet(sheetName);
        XSSFCell cell = excelSheet.getRow(row).getCell(col);
        if (cell == null) return null;
        return cell.getStringCellValue();
    }

    public static int getRowCount(String sheetName) {
        excelSheet = excelBook.getSheet(sheetName);
        return excelSheet.getLastRowNum() + 1;
    }

    public static int getSheetCount() {
        return excelBook.getNumberOfSheets();
    }


    public static List<File> getFiles(String path, FileType type) throws XLSUtilityException {
        File directory = new File(path);
        File[] arrayFiles = new File[0];
        switch (type) {
            case FOLDER:
                arrayFiles = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
                break;
            case TEST_CASE:
                WildcardFileFilter tcWildCardFilter = new WildcardFileFilter(TEST_CASE_PREFIX_NAME.concat("*").
                        concat(TEST_FILE_EXTENSION));
                arrayFiles = directory.listFiles((FileFilter) tcWildCardFilter);
                break;
            case TEST_SCRIPT:
                WildcardFileFilter tsWildCardFilter = new WildcardFileFilter(TEST_SCRIPT_PREFIX_NAME.concat("*").
                        concat(TEST_FILE_EXTENSION));
                arrayFiles = directory.listFiles((FileFilter) tsWildCardFilter);
                break;
        }
        if (arrayFiles == null) throw new XLSUtilityException("No input file found.");
        return Arrays.asList(arrayFiles);
    }

    //This methods is to get the count of the test steps of test case
    //This methods takes three arguments (Sheet name, Test Case Id & Test case row number)
    public static int getTestStepsCount(String sheetName, String testCaseId, int iTestCaseStart) throws Exception {
        for (int i = iTestCaseStart; i <= XLSUtility.getRowCount(sheetName); i++) {
            if (!testCaseId.equals(XLSUtility.getCellData(i, Konstante.COL_TEST_CASE_ID, sheetName))) {
                return i;
            }
        }
        excelSheet = excelBook.getSheet(sheetName);
        return excelSheet.getLastRowNum() + 1;
    }
}
