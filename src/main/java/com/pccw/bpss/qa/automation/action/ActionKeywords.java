package com.pccw.bpss.qa.automation.action;

import com.pccw.bpss.qa.automation.data.Constant;
import com.pccw.bpss.qa.automation.exception.ActionKeywordException;
import com.relevantcodes.extentreports.IExtentTestClass;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.App;
import org.sikuli.script.Key;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.pccw.bpss.qa.automation.Application.REPOSITORY;
import static com.pccw.bpss.qa.automation.Application.SELECTOR;
import static com.pccw.bpss.qa.automation.data.Constant.*;
import static com.pccw.bpss.qa.automation.data.Constant.PROFILE_PROP_IMG_PATH;

/**
 * Created by FaustineP on 07/03/2017.
 */
public class ActionKeywords {

    final static Logger logger = Logger.getLogger(ActionKeywords.class);


    public static WebDriver webDriver = null;
    public static App appDriver = null;
    public static Screen screen = null;

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void openApplication(IExtentTestClass report, String description, String testCaseId,
                                   String object, String data) {
        try {
            switch (data) {
                case "IE":
                case "ie":
                case "Internet Explorer":
                case "InternetExplorer":
                    System.setProperty(KEY_IE_DRIVE, FILE_PATH_DRIVER_IE_X32_EXE);
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                    webDriver = new InternetExplorerDriver(capabilities);
                    webDriver.manage().window().maximize();
                    report.log(LogStatus.INFO, description, data.toUpperCase());
                    break;
                case "FF":
                case "FireFox":
                    System.setProperty(KEY_GECKO_DRIVER, FILE_PATH_GECKO_DRIVER_EXE);
                    webDriver = new FirefoxDriver();
                    webDriver.manage().window().maximize();
                    report.log(LogStatus.INFO, description, data.toUpperCase());
                    break;
                case "Chrome":
                case "chrome":
                    System.setProperty(KEY_CHROME_DRIVER, FILE_PATH_CHROME_DRIVER_EXE);
                    webDriver = new ChromeDriver();
                    webDriver.manage().window().maximize();
                    report.log(LogStatus.INFO, description, data.toUpperCase());
                    break;
                default:
                    appDriver = App.open(data);
                    report.log(LogStatus.INFO, description, data.toUpperCase());
                    break;
            }
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            logger.info("openBrowser " + data);
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void navigate(IExtentTestClass report, String description, String testCaseId,
                                String object, String data) {
        try {
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            webDriver.get(data);
            report.log(LogStatus.INFO, description, data);
            logger.info("navigate " + data);
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void closeApplication(IExtentTestClass report, String description, String testCaseId,
                                    String object, String data) {
        try {
            if(appDriver != null){
                appDriver.close();
            }

            if(webDriver != null){
                webDriver.quit();
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("closing browser...");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void inputTxt(IExtentTestClass report, String description, String testCaseId,
                                String object, String data) {
        try {
            if(!isCSSSelector(SELECTOR.getProperty(object).toLowerCase())){
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).clear();
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).sendKeys(data);
            }else{
                screen = new Screen();
                Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
                screen.wait(image, 10);
                screen.click();
                screen.type(data);
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("inputTxt [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    private static boolean isCSSSelector(String filename){
        String pattern = ".*\\.(png|jpg|bmp|jpeg|tiff)";
        return filename.toLowerCase().matches(pattern);
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void click(IExtentTestClass report, String description, String testCaseId,
                             String object, String data) {
        try {
            if(!isCSSSelector(SELECTOR.getProperty(object).toLowerCase())){
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).click();
            }else{
                screen = new Screen();
                Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
                screen.wait(image, 10);
                screen.click();
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("click [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     *
     * @param report
     * @param description
     * @param testCaseId
     * @param object
     * @param data
     */
    public static void doubleClick(IExtentTestClass report, String description, String testCaseId,
                                   String object, String data) {
        try {
            screen = new Screen();
            Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
            screen.wait(image, 10);
            screen.doubleClick();
            report.log(LogStatus.INFO, description, data);
            logger.info("click [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     *
     * @param report
     * @param description
     * @param testCaseId
     * @param object
     * @param data
     */
    public static void rightClick(IExtentTestClass report, String description, String testCaseId,
                                  String object, String data) {
        try {
            screen = new Screen();
            Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
            screen.wait(image, 10);
            screen.rightClick();
            report.log(LogStatus.INFO, description, data);
            logger.info("click [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void inputVerificationCode(IExtentTestClass report, String description, String testCaseId,
                                             String object, String data) throws NoSuchElementException {
        try {
            String code = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getText();
            webDriver.findElement(By.cssSelector(SELECTOR.getProperty("loginVerificationTxt"))).sendKeys(code);
            report.log(LogStatus.INFO, description, data);
            logger.info("inputVerification [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }


    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void inputDate(IExtentTestClass report, String description, String testCaseId, String object,
                                 String data) throws NoSuchElementException {
        // TODO : provide date format validation
        try {
            if(!isCSSSelector(SELECTOR.getProperty(object).toLowerCase())){
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).clear();
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).sendKeys(data);
                webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).sendKeys(Keys.TAB);
            }else{
                screen = new Screen();
                Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
                screen.wait(image, 10);
                screen.click();
                screen.type(data);
                screen.type(Key.TAB);
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("inputDate [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.INFO, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void select(IExtentTestClass report, String description, String testCaseId, String object,
                                         String data) throws NoSuchElementException {
        try {
            if(!isCSSSelector(SELECTOR.getProperty(object).toLowerCase())){
                new Select(webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim())))).
                        selectByVisibleText(data);
            }else{
                //TODO
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("selectFromListBox [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void waitFor(IExtentTestClass report, String description, String testCaseId, String object,
                               String data) throws InterruptedException {
        try {
            Thread.sleep(Long.valueOf(data));
            logger.info("waitFor [" + data + "]");
        } catch (InterruptedException e) {
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(e.getStackTrace()), e.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void uploadFile(IExtentTestClass report, String description, String testCaseId, String object,
                                  String data) {
        try {
            webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).clear();
            webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object.trim()))).sendKeys(data);
            report.log(LogStatus.INFO, "Upload file[" + data + "]");
            logger.info("uploadFile [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void snapShot(IExtentTestClass report, String description, String testCaseId, String object,
                                String data) throws IOException {
        try {
            String FILE_PATH = Constant.OUTPUT_BASE_PATH.concat(File.separator).concat("snapshot").
                    concat(File.separator).concat(testCaseId.replaceAll("\\\\/", "\\")).
                    concat(File.separator).concat(data + ".png");
            File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(FILE_PATH));
            String relativePath = FILE_PATH.replace(new File(Constant.OUTPUT_BASE_PATH).getParent(), "..");
            report.log(LogStatus.INFO, data + "\t" + report.addScreenCapture(relativePath));
            logger.info("snapShot [" + object + "]" + "[" + data + "]");
        } catch (IOException ioe) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ioe.getStackTrace()), ioe.getCause());
            logger.error(ake);
            throw ake;
        }
    }


    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void isContentEqualsTo(IExtentTestClass report, String description, String testCaseId,
                                         String object, String data) {
        try {
            if(!isCSSSelector(SELECTOR.getProperty(object).toLowerCase())){
                String strContent = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getText();
                if (strContent.trim().equals(data)) {
                    report.log(LogStatus.PASS, description, data);
                } else {
                    report.log(LogStatus.FAIL, description, data);
                }
            }else{
                screen = new Screen();
                Pattern image = new Pattern(PROFILE_PROP_IMG_PATH.concat(File.separator).concat(SELECTOR.getProperty(object)));
                if (screen.exists(image) != null) {
                    report.log(LogStatus.PASS, description, data);
                } else {
                    report.log(LogStatus.FAIL, description, data);
                }
            }
            logger.info("isContentEqualsTo [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void isContentContains(IExtentTestClass report, String description, String testCaseId,
                                         String object, String data) {
        try {
            String strContent = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getText();
            if (strContent.trim().contains(data)) {
                report.log(LogStatus.PASS, description, data);
            } else {
                report.log(LogStatus.FAIL, description, data);
            }
            logger.info("isContentContains [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector where to get the info that will be stored
     * @param data        Key property reference for the info
     */
    public static void storeInfo(IExtentTestClass report, String description, String testCaseId,
                                 String object, String data) {
        try {
            String info;
            if (object.endsWith("Txt")) {
                info = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getAttribute("value");
            } else if (object.endsWith("ListBox")) {
                Select selectItem = new Select(webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))));
                info = selectItem.getFirstSelectedOption().getText();
            } else {
                info = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getText();
            }
            REPOSITORY.put(data, info);
            report.log(LogStatus.INFO, description, data);
            logger.info("storeInfo [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector where to put the retrieved info
     * @param data        Key property of info that will be retrieved from REPOSITORY
     */
    public static void retrieveInfo(IExtentTestClass report, String description, String testCaseId,
                                    String object, String data) {
        try {
            String info = REPOSITORY.get(data);
            webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).sendKeys(info);
            logger.info("retrieveInfo [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void validateAttributeValue(IExtentTestClass report, String description, String testCaseId,
                                              String object, String data) {
        try {
            String[] info = data.split(":");
            String attr = null;
            String expectedValue = null;
            if (info.length > 1) {
                attr = info[0].trim();
                expectedValue = info[1].trim();
                String attrActualValue = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getAttribute(attr);
                if (Objects.equals(expectedValue, attrActualValue)) {
                    report.log(LogStatus.PASS, description, expectedValue);
                } else {
                    report.log(LogStatus.FAIL, description, expectedValue);
                }
            } else {
                report.log(LogStatus.ERROR, description, expectedValue);
            }
            logger.info("validateAttributeValue [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, "");
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void isObjectPresent(IExtentTestClass report, String description, String testCaseId,
                                       String object, String data) {
        boolean isFound = false;
        try {
            WebElement element = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object)));
            if (element.isDisplayed()) {
                isFound = true;
            }
            logger.info("isObjectPresent [" + object + "]" + "[" + data + "]");
        } catch (NoSuchElementException ignored) {
        } finally {
            if (isFound == Boolean.valueOf(data)) {
                report.log(LogStatus.PASS, description, data);
            } else {
                report.log(LogStatus.FAIL, description, data);
            }
        }
    }


    /**
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void cancelRequest(IExtentTestClass report, String description, String testCaseId,
                                     String object, String data) {
        try {
            Thread.sleep(2000);
            WebElement tableElement = webDriver.findElement(By.cssSelector("#extProRequestTable"));
            List<WebElement> tableRows = tableElement.findElements(By.tagName("tr"));
            for (WebElement element : tableRows) {
                List<WebElement> tableColumns = element.findElements(By.tagName("td"));
                List<WebElement> elements = tableColumns.get(Integer.valueOf(data)).findElements(By.tagName("a"));
                for (WebElement element1 : elements) {
                    if (element1.getText().equalsIgnoreCase("cancel")) {
                        element1.click();
                        return;
                    }
                }
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("cancelRequest [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    /**
     * Note : View Details of subscription BP side
     *
     * @param report      IExtentTestClass interface for report logging
     * @param description Step description based on test script .xlsx
     * @param testCaseId  Test Case Identifier
     * @param object      Key property selector to identify the CSS Selector path
     * @param data        Input data
     */
    public static void viewDetails(IExtentTestClass report, String description, String testCaseId,
                                   String object, String data) {
        try {
            WebElement tableElement = webDriver.findElement(By.cssSelector("#extProRequestTable"));
            List<WebElement> tableRows = tableElement.findElements(By.tagName("tr"));
            for (WebElement element : tableRows) {
                List<WebElement> tableColumns = element.findElements(By.tagName("td"));
                List<WebElement> elements = tableColumns.get(Integer.valueOf(data)).findElements(By.tagName("a"));
                for (WebElement element1 : elements) {
                    if (element1.getAttribute("class").equalsIgnoreCase("pointCss")) {
                        element1.click();
                        return;
                    }
                }
            }
            report.log(LogStatus.INFO, description, "");
            logger.info("viewDetails [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void viewInternalDetails(IExtentTestClass report, String description, String testCaseId,
                                           String object, String data) {
        try {
            WebElement tableElement = webDriver.findElement(By.cssSelector("#srResultTable"));
            List<WebElement> tableRows = tableElement.findElements(By.tagName("tr"));
            for (WebElement element : tableRows) {
                List<WebElement> tableColumns = element.findElements(By.tagName("td"));
                List<WebElement> elements = tableColumns.get(1).findElements(By.tagName("a"));
                for (WebElement element1 : elements) {
                    element1.click();
                    return;
                }
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("viewInternalDetails [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }


    public static void findSubscription(IExtentTestClass report, String description, String testCaseId,
                                        String object, String data) {
        try {
            Thread.sleep(2000);
            WebElement tableElement = webDriver.findElement(By.cssSelector("#extProRequestTable"));
            List<WebElement> tableRows = tableElement.findElements(By.tagName("tr"));
            boolean isFound = false;
            String subscriptionRefSource = null;
            String subscriptionRefTarget = REPOSITORY.get(data);
            for (WebElement element : tableRows) {
                List<WebElement> tableColumns = element.findElements(By.tagName("td"));
                subscriptionRefSource = tableColumns.get(3).getText();
                if (subscriptionRefSource == null || subscriptionRefTarget == null) {
                } else {
                    if (subscriptionRefSource.trim().equalsIgnoreCase(subscriptionRefTarget.trim())) {
                        isFound = true;
                        break;
                    }
                }
            }
            if (isFound) {
                String details = "Source = ".concat(subscriptionRefSource).concat(" Target = ").concat(subscriptionRefTarget);
                report.log(LogStatus.PASS, description, details);

            } else {
                String details = "Source = ".concat(subscriptionRefSource).concat(" Target = ").concat(subscriptionRefTarget);
                report.log(LogStatus.FAIL, description, details);
            }
            logger.info("findSubscription [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void validateStoredInfo(IExtentTestClass report, String description, String testCaseId,
                                          String object, String data) {
        try {
            String info = REPOSITORY.get(data);
            String strContent = webDriver.findElement(By.cssSelector(SELECTOR.getProperty(object))).getText();
            if (strContent.trim().equals(info)) {
                report.log(LogStatus.PASS, description, strContent + " is equal to" + info);
            } else {
                report.log(LogStatus.FAIL, description, strContent + " not equal to" + info);
            }
            logger.info("validateStoredInfo [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void waitUntil(IExtentTestClass report, String description, String testCaseId,
                                 String object, String data) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, 90);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(SELECTOR.getProperty(object))));
            Thread.sleep(1000);
            logger.info("waitUntil [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    public static void updateSubscription(IExtentTestClass report, String description, String testCaseId,
                                          String object, String data) {
        try {
            WebElement tableElement = webDriver.findElement(By.cssSelector("#monthlyReportTable"));
            List<WebElement> tableRows = tableElement.findElements(By.tagName("tr"));
            for (WebElement element : tableRows) {
                List<WebElement> tableColumns = element.findElements(By.tagName("td"));
                List<WebElement> elements = tableColumns.get(10).findElements(By.tagName("a"));
                for (WebElement element1 : elements) {
                    element1.click();
                    return;
                }
            }
            report.log(LogStatus.INFO, description, data);
            logger.info("viewInternalDetails [" + object + "]" + "[" + data + "]");
        } catch (Exception ex) {
            report.log(LogStatus.ERROR, description, data);
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

    //TODO
    public static void closePrompt(){
        try {
            screen.closePrompt();
        } catch (Exception ex) {
            ActionKeywordException ake = new ActionKeywordException(Arrays.toString(ex.getStackTrace()), ex.getCause());
            logger.error(ake);
            throw ake;
        }
    }

}
