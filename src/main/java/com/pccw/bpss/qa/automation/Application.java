package com.pccw.bpss.qa.automation;

import com.pccw.bpss.qa.automation.action.ActionKeywords;
import com.pccw.bpss.qa.automation.data.FileType;
import com.pccw.bpss.qa.automation.exception.InputFileException;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import com.pccw.bpss.qa.automation.utility.XLSUtility;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.pccw.bpss.qa.automation.data.Constant.*;

/**
 * Created by FaustineP on 06/03/2017.
 */
public class Application extends ApplicationAbstract {

    final static Logger logger = Logger.getLogger(Application.class);

    public Application() throws IOException {
        setUpLogger();
        initializeRepository();
        actionKeywords = new ActionKeywords();
        methods = actionKeywords.getClass().getMethods();
    }

    public static void main(String args[]) {
        logger.info("Starting application....");
        List<File> serviceFolders = null;
        Application application = null;
        try {
            loadProfile(args);
            application = new Application();
            serviceFolders = XLSUtility.getFiles(INPUT_BASE_PATH, FileType.FOLDER);
        } catch (IOException | XLSUtilityException e) {
            logger.error(e);
            return;
        }
        for (File serviceFolder : serviceFolders) {
            String reportFileName = OUTPUT_BASE_PATH.concat(File.separator).
                    concat(serviceFolder.getName().concat(".html"));
            report = new ExtentReports(reportFileName, false, DisplayOrder.OLDEST_FIRST);
            try {
                testCaseData = loadTestCaseData(serviceFolder);
            } catch (IOException | InvalidFormatException | InputFileException e) {
                logger.error(e);
                continue;
            } catch (XLSUtilityException e) {
                logger.error(e);
                continue;
            }
            if (testCaseData == null) continue;
            for (Map.Entry<String, Map<String, String>> testCaseContent : testCaseData.getContent().entrySet()) {
                String testScript = null;
                try {
                    testScript = serviceFolder.getCanonicalPath().concat(File.separator).
                            concat(TEST_SCRIPT_PREFIX_NAME.concat(testCaseContent.getKey()).concat(TEST_FILE_EXTENSION));
                    XLSUtility.setExcelFile(testScript);
                } catch (IOException e) {
                    logger.error(e);
                    continue;
                } catch (XLSUtilityException e) {
                    logger.error(e);
                    continue;
                }
                for (Map.Entry<String, String> testCaseInfoIterator : testCaseContent.getValue().entrySet()) {
                    testCaseId = testCaseInfoIterator.getKey();
                    String testCaseObjective = testCaseInfoIterator.getValue();
                    testCaseId = testCaseId.replaceFirst("Fct\\/Sys\\/", "");
                    test = report.startTest(testCaseId, testCaseObjective);
                    try {
                        application.executeTestScript();
                    } catch (Exception ex) {
                        logger.error(ex);
                        ActionKeywords.webDriver.quit();
                    } finally {
                        report.endTest(test);
                        report.flush();
                        REPOSITORY.clear();
                    }
                }
            }
        }
    }
}
