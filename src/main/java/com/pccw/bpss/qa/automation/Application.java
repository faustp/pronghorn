package com.pccw.bpss.qa.automation;


import com.pccw.ad.pronghorn.engine.IEngine;
import com.pccw.ad.pronghorn.engine.PronghornEngine;
import com.pccw.ad.pronghorn.engine.action.ActionKeywords;
import com.pccw.ad.pronghorn.engine.data.FileType;
import com.pccw.ad.pronghorn.model.exception.ProfileException;
import com.pccw.ad.pronghorn.model.exception.TestCaseException;
import com.pccw.ad.pronghorn.model.profile.Profile;
import com.pccw.ad.pronghorn.model.profile.Selector;
import com.pccw.ad.pronghorn.model.profile.Service;
import com.pccw.ad.pronghorn.model.tc.Script;
import com.pccw.ad.pronghorn.model.tc.TestCase;
import com.pccw.bpss.qa.automation.exception.InputFileException;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import com.pccw.bpss.qa.automation.utility.XLSUtility;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pccw.bpss.qa.automation.data.Konstante.*;


/**
 * Created by FaustineP on 06/03/2017.
 */
public class Application extends ApplicationAbstract {

    final static Logger logger = Logger.getLogger(Application.class);

    public Application() throws IOException {
        setUpLogger();
    }

    public static void main(String args[]) throws IllegalAccessException, InvocationTargetException, ProfileException {
        logger.info("Starting application....");
        Profile profile = loadProfileData(args);
        IEngine engine = new PronghornEngine(profile);
        engine.execute();
    }

    public static Profile loadProfileData(String args[]) {
        logger.info("Starting application....");
        List<File> serviceFolders = null;
        Selector selector;
        Profile profile = new Profile();
        LinkedHashSet<Service> services = new LinkedHashSet<>();

        try {
            selector = loadPropertiesData(args);

            serviceFolders = XLSUtility.getFiles(INPUT_BASE_PATH, FileType.FOLDER);
        } catch (IOException | XLSUtilityException e) {
            logger.error(e);
            return null;
        }

        for (File serviceFolder : serviceFolders) { //project folder
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
            } catch (TestCaseException e) {
                e.printStackTrace();
            }
            if (testCaseData == null)// continue;
                return null;
            else {
                Service service = new Service();
                service.setName(args[1]);
                service.setTestCases(testCaseData);
                services.add(service);
            }
        }
        profile.setSelector(selector);
        profile.setServices(services);
        profile.setName(args[0].trim());

        return profile;
    }
}
