package com.pccw.bpss.qa.automation;


import com.pccw.ad.pronghorn.engine.IEngine;
import com.pccw.ad.pronghorn.engine.PronghornEngine;
import com.pccw.ad.pronghorn.engine.data.Constant;
import com.pccw.ad.pronghorn.engine.data.FileType;
import com.pccw.ad.pronghorn.model.exception.ProfileException;
import com.pccw.ad.pronghorn.model.exception.TestCaseException;
import com.pccw.ad.pronghorn.model.profile.Profile;
import com.pccw.ad.pronghorn.model.profile.Service;
import com.pccw.bpss.qa.automation.exception.InputFileException;
import com.pccw.bpss.qa.automation.exception.XLSUtilityException;
import com.pccw.bpss.qa.automation.utility.XLSUtility;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.sikuli.script.FindFailed;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pccw.bpss.qa.automation.data.Konstante.INPUT_BASE_PATH;


/**
 * Created by FaustineP on 06/03/2017.
 */
public class Application extends ApplicationAbstract {

    final static Logger logger = Logger.getLogger(Application.class);

    public static void main(String args[]) throws IllegalAccessException, InvocationTargetException, ProfileException, IOException, FindFailed {
        setUpLogger();
        logger.info("Starting application....");
        Profile profile = loadProfileData(args);
        IEngine engine = new PronghornEngine(profile);
        engine.execute();
    }

    public static Profile loadProfileData(String args[]) {
        logger.info("Starting application....");
        List<File> serviceFolders = null;
        Profile profile = new Profile();
        LinkedHashSet<Service> services = new LinkedHashSet<>();

        try {
            loadPropertiesData(args);
            serviceFolders = XLSUtility.getFiles(INPUT_BASE_PATH, FileType.FOLDER);
        } catch (IOException | XLSUtilityException e) {
            logger.error(e);
            return null;
        }

        for (File serviceFolder : serviceFolders) { //project folder
            String reportFileName = Constant.OUTPUT_BASE_PATH.concat(File.separator).
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
                logger.error(e);
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
        profile.setServices(services);
        profile.setName(args[0].trim());

        return profile;
    }
}
