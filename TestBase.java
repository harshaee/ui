package com.sprint.qa.base;
import java.awt.*;
//import java.awt.datatransfer.Clipboard;
//import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import com.sprint.qa.helper.Helper;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.sprint.qa.helper.LoggerHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.sprint.qa.util.POST_Method_2FAUtil;
import com.sprint.qa.util.TestUtil;
import org.openqa.selenium.support.ui.*;

import static com.sprint.qa.util.APIPage.ReadExcelData;


public class TestBase {

    public static WebDriver driver;
    public static Properties prop;
    public static WebElement element;
    public static JavascriptExecutor js;
    public static Wait<WebDriver> flu_wait;
    public static Helper h;
    public static WebDriverWait wait;
    public static Actions action;
    public static String parentwindow;
    public static Robot robot;
    public static HashMap<String, Object> chromepreferences = new HashMap<>();
    public static List<WebElement> options_list;

    public static List<WebElement> total_list;
    public Set<String> allwindows;
    public static SimpleDateFormat simdate;

    public static Date date;
    public static String download_path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    public static Logger log = Logger.getLogger(TestBase.class);
    static String ENV = "qa3";
    public static String date1;


    public TestBase() {
        try {
            prop = new Properties();
            if (ENV.equals("qa")) {
                FileInputStream fis = new FileInputStream("src/main/java/com/sprint/qa/config/qaconfig.properties");
                prop.load(fis);
            } else if (ENV.equals("UAT")) {
                FileInputStream fis = new FileInputStream("src/main/java/com/sprint/qa/config/uatconfig.properties");
                prop.load(fis);

            }
            else if (ENV.equals("qa3")) {
                FileInputStream fis = new FileInputStream("src/main/java/com/sprint/qa/config/qa3config.properties");
                prop.load(fis);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void initialization() throws Exception {
        WebDriverManager.chromedriver().setup();
        //System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
		//driver.manage().window().maximize();
        chromepreferences.put("profile.default_content_settings.popups", 0);
        chromepreferences.put("download.default_directory", download_path);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--proxy-server='direct://'");
        chromeOptions.addArguments("--proxy-bypass-list=*");
        chromeOptions.addArguments("--start-maximized");
       // chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.setExperimentalOption("prefs", chromepreferences);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(TestUtil.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(TestUtil.IMPLICIT_WAIT, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 90);
        action = new Actions(driver);
        date = new Date();
        simdate = new SimpleDateFormat("MM/dd/yyyy");
        parentwindow = driver.getWindowHandle();
        h = new Helper();
        js = (JavascriptExecutor) driver;
        options_list = new ArrayList<>();
        flu_wait = new FluentWait<WebDriver>(TestBase.driver)
                .withTimeout(Duration.ofSeconds(120))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class)
                .ignoring(ElementNotInteractableException.class);
        //call the post method
        //POST_Method_2FAUtil.sendPost1();
        //POST api method to get access token and refresh token
        if(ENV=="UAT"){
            RestAssured.baseURI = prop.getProperty("apiurl");
            RequestSpecification request = RestAssured.given();
            String payload = ReadExcelData("UatLoginExcelFile");
            System.out.println(payload);
            request.header("Content-Type", "application/json");
            Response responsefromspecification = request.body(payload).post("/v1.0/authentication/token");
            responsefromspecification.prettyPrint();
            String accesstype = responsefromspecification.getBody().jsonPath().get("response.token_type");
            String accesstoken = responsefromspecification.getBody().jsonPath().get("response.access_token");
            String refreshtoken = responsefromspecification.getBody().jsonPath().get("response.refresh_token");
            driver.get("https://phoenix-uat.presidio.com/#/redirect?token_type=" + accesstype + "&access_token=" + accesstoken + "&refresh_token=" + refreshtoken + "&url=/");
            Thread.sleep(50000);
        }
        else if(ENV=="qa"){ RestAssured.baseURI = prop.getProperty("apiurl");
            RequestSpecification request = RestAssured.given();
            String payload = ReadExcelData("LoginExcelFile");
            System.out.println(payload);
            request.header("Content-Type", "application/json");
            Response responsefromspecification = request.body(payload).post("/v1.0/authentication/token");
            responsefromspecification.prettyPrint();
            String accesstype = responsefromspecification.getBody().jsonPath().get("response.token_type");
            String accesstoken = responsefromspecification.getBody().jsonPath().get("response.access_token");
            String refreshtoken = responsefromspecification.getBody().jsonPath().get("response.refresh_token");
            driver.get("https://phoenix-qa2.presidio.com/#/redirect?token_type=" + accesstype + "&access_token=" + accesstoken + "&refresh_token=" + refreshtoken + "&url=/");
            Thread.sleep(50000);
        }
        else{RestAssured.baseURI = prop.getProperty("apiurl");
            RequestSpecification request = RestAssured.given();
            String payload = ReadExcelData("Qa3LoginExcelFile");
            System.out.println(payload);
            request.header("Content-Type", "application/json");
            Response responsefromspecification = request.body(payload).post("/v1.0/authentication/token");
            responsefromspecification.prettyPrint();
            String accesstype = responsefromspecification.getBody().jsonPath().get("response.token_type");
            String accesstoken = responsefromspecification.getBody().jsonPath().get("response.access_token");
            String refreshtoken = responsefromspecification.getBody().jsonPath().get("response.refresh_token");
            driver.get("https://phoenix-qa3.presidio.com/#/redirect?token_type=" + accesstype + "&access_token=" + accesstoken + "&refresh_token=" + refreshtoken + "&url=/");
            Thread.sleep(50000);

        }
    }


    public static void initialization1() throws Exception {

        String a = "window.open('about:blank','_blank');";

        ((JavascriptExecutor) driver).executeScript(a);

        driver.switchTo().activeElement();

        Set<String> Allhandles = driver.getWindowHandles();

        for (String childWindow1 : Allhandles) {

            driver.switchTo().window(childWindow1);

            Thread.sleep(2000);

        }

        driver.get(prop.getProperty("url3"));

    }

    public static void scrollintoview(String locator) {
        js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath(locator)));
    }

    public static void scrollintoview(WebElement ele) {
        js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", ele);
    }


    public static void ExplicitWaits(String locator) {
        WebDriverWait wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.and(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(locator))));
        WebElement status_cmemo4 = driver.findElement(By.xpath(locator));
        wait.until(ExpectedConditions.visibilityOf(status_cmemo4));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
    }

    public void Pagination(int i) {
        driver.findElement(By.xpath("//li[contains(@class,'page-item')]//a[text()='" + i + "']")).click();
    }

    public boolean verifyPagination(int i) {
        return driver.findElement(By.xpath("//li[contains(@class,'page-item')]//a[text()='" + i + "']")).isDisplayed();
    }

    public static Object scrollElementIntoView(WebDriver driver, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static WebElement xpath_by_js(String xpath) {
        element = (WebElement) js.executeScript("document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue");
        return element;
    }

    public static Object textarea_by_js(String parent, String tagname) {
        log.info("document.querySelector('#" + parent + " " + tagname + "').value");
        return js.executeScript("document.querySelector('#" + parent + " " + tagname + "').value");
    }

    //scrolling
    public void Scroll() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");

    }

    public void enterTextBox(String Control, String Value) {
        Actions action = new Actions(driver);
        switch (Control) {
            case "Enter Subscription":
            case "Enter Change Request":
                h.fluent_wait_no_such_element("//div[contains(@class,'right-section-input')]//input[contains(@placeholder,'" + Control + "')]");
                WebElement element = driver.findElement(By.xpath("//div[contains(@class,'right-section-input')]//input[contains(@placeholder,'" + Control + "')]"));
                action.moveToElement(element).build().perform();
                element.clear();
                h.fluent_wait_sendkeys_not_interactable(element, Value);
                //element.sendKeys(Value);
                break;
        }
    }

    public void selectDropDown(int i, String Value) throws IOException, InterruptedException {
        Actions act = new Actions(driver);
        switch (Value) {
            case "Created Date":
            case "Subs #":
            case "Status":
            case "Change Order #":
            case "Change Type":
            case "Order #":
            case "Document #":
            case "Billing Frequency":
            case "Vendor":
            case "Has Usage":
            case "Document Type":
                ExplicitWaits("//div[@class='row']//div[" + i + "]//select[contains(@class,'form-control')]");
                Select select = new Select(driver.findElement(By.xpath("//div[@class='row']//div[" + i + "]//select[contains(@class,'form-control')]")));
                List<WebElement> options = select.getOptions();
                System.out.println(options);
                for (WebElement option : options) {
                    if (option.getText().equals(Value)) {
                        select.selectByVisibleText(Value);
                    }
                }
                break;

        }
    }

    public void selectDropDown(String Value, String value) {
        switch (Value) {
            case "Change Type Value":
                ExplicitWaits("//div[@class='row']//div[contains(@class,'right-section-input')]//select[contains(@class,'form-control')]");
                Select select = new Select(driver.findElement(By.xpath("//div[@class='row']//div[contains(@class,'right-section-input')]//select[contains(@class,'form-control')]")));
                List<WebElement> options = select.getOptions();
                System.out.println(options);
                for (WebElement option : options) {
                    if (option.getText().equals(value)) {
                        select.selectByVisibleText(value);
                    }
                }
                break;
        }
    }


    public void EnterTextBox(int i, String value) {
        try {
            Actions act = new Actions(driver);
            WebElement element = driver.findElement(By.xpath("//div[@class='row']//div[contains(@class,'form-group col')][" + i + "]//div[contains(@class,'right-section-input mr')]//div[@class='autocomplete']//input"));
            act.moveToElement(element).click().build().perform();
            act.moveToElement(element).sendKeys(value);
            act.moveToElement(element).sendKeys(Keys.ENTER);
        } catch (ElementNotInteractableException e) {
            e.printStackTrace();
        }
    }

    public void multiSelectDropDown(String Value) throws IOException, InterruptedException {
        Actions act = new Actions(driver);
        switch (Value) {
            case "Unsubmitted":
            case "Change Requested":
            case "Submitted":
            case "Approved By Customer":
            case "Rejected By Customer":
            case "Expired":
            case "Verification":
            case "Credit Hold":
            case "Sent back to inside sales":
            case "InFulfillment":
            case "Submitted To vendor":
            case "Acknowledged By vendor":
            case "Confirmed By Vendor":
            case "Rejected By Vendor":
            case "Booked":
            case "Pending Provisioning":
            case "Received":
            case "Manually Received":
            case "Submitted To ERP":
            case "Complete":
            case "Canceled":
            case "New":
            case "Active":
            case "Overdue":
                try {
                    ExplicitWaits("//div[@class='multiselect__select']");
                    driver.findElement(By.xpath("//div[@class='multiselect__select']")).click();
                    List<WebElement> statuslist = driver.findElements(By.xpath("//div[@class='multiselect__content-wrapper']//ul//li//span"));
                    System.out.println(statuslist);
                    for (WebElement option : statuslist) {
                        if (option.getText().equals(Value)) {
                            option.click();
                            option.sendKeys(Keys.ENTER);
                        }
                    }
                } catch (ElementNotInteractableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public boolean verifyselectedDropDown(int i, String Value) throws IOException, InterruptedException {
        switch (Value) {
            case "Status":
                ExplicitWaits("//div[@class='row']//div[" + i + "]//select[contains(@class,'form-control')]");
                Select select = new Select(driver.findElement(By.xpath("//div[@class='row']//div[" + i + "]//select[contains(@class,'form-control')]")));
                WebElement option = select.getFirstSelectedOption();
                assert option.isDisplayed();
        }
        return true;
    }


    public void addDropdown() throws InterruptedException {
        try {
            Actions act = new Actions(driver);
            act.moveToElement(driver.findElement(By.xpath("//div[@class='d-flex buttons']//i[2]"))).build().perform();
            act.moveToElement(driver.findElement(By.xpath("//div[@class='d-flex buttons']//i[2]"))).click().build().perform();
            Thread.sleep(3000);
        } catch (ElementClickInterceptedException e) {
            e.printStackTrace();
        }
    }

    public void removeDropdown(int i) throws InterruptedException {
        try {
            Actions act = new Actions(driver);
            act.moveToElement(driver.findElement(By.xpath("//div[@class='row']//div[contains(@class,'form-group col')][" + i + "]//div[@class='d-flex buttons']//i[1]"))).build().perform();
            act.moveToElement(driver.findElement(By.xpath("//div[@class='row']//div[contains(@class,'form-group col')][" + i + "]//div[@class='d-flex buttons']//i[1]"))).click().build().perform();
            Thread.sleep(3000);
        } catch (ElementClickInterceptedException e) {
            e.printStackTrace();
        }
    }


    public void switch_to_child_window() {
        allwindows = driver.getWindowHandles();
        for (String childwindow : allwindows
        ) {
            driver.switchTo().window(childwindow);
        }
    }

    public void close_child_windows() {
        allwindows = driver.getWindowHandles();
        for (String win : allwindows
        ) {
            if (!(win.equalsIgnoreCase(parentwindow))) {
                driver.close();
            }
        }
    }

    public void wait_element_tobe_displayed(String xpath) {
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
    }

    public void wait_element_tobe_displayed(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void wait_element_tobe_present(String xpath) {
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
    }

    public void wait_element_tobe_present(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void wait_element_tobe_clickable(String xpath) {
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(xpath))));
    }

    public void wait_element_tobe_clickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void click_element(String xpath) {
        driver.findElement(By.xpath(xpath)).click();
    }

    public void click_element(WebElement element) {
        element.click();
    }

    public String get_element_text(String xpath) {
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public void select_drop_down(String xpath, int index) {
        Select select = new Select(driver.findElement(By.xpath(xpath)));
        select.selectByIndex(index);
    }

    public void select_drop_down(String xpath, String value) {
        Select select = new Select(driver.findElement(By.xpath(xpath)));
        List<WebElement> items = select.getOptions();
        for (WebElement ele : items
        ) {
            if (ele.getText().equalsIgnoreCase(value)) {
                ele.click();
            }
        }
    }

    public List select_drop_down_list(String xpath) {
        Select select = new Select(driver.findElement(By.xpath(xpath)));
        return select.getOptions();
    }

    public static List<WebElement> select_unordered_list(WebElement ele, String tag) {
        return ele.findElements(By.tagName(tag));
    }

    public static List<WebElement> select_unordered_list(String xpath, String tag) {
        return driver.findElement(By.xpath(xpath)).findElements(By.tagName(tag));
    }

    public static void click_toggle_btn(String xpath) {
        element = driver.findElement(By.xpath(xpath));
        action.moveToElement(element).click().build().perform();
    }

    public void move_and_click(String xpath) {
        element = driver.findElement(By.xpath(xpath));
        action.moveToElement(element).click().build().perform();
    }

    public void upload_file(String fileName)  {

        String path = System.getProperty("user.dir");
        log.info("path: " + path + File.separator + fileName);
        String pathfile = path + File.separator + fileName;

        //todo: find another way
        //Create instance of Clipboard class
        //Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //StringSelection stringSelection = new StringSelection(pathfile);
        //clipboard.setContents(stringSelection, null);

        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.CONTROL);
        actions.sendKeys(Keys.getKeyFromUnicode('v'));
        actions.perform();
        actions.sendKeys(Keys.ENTER);
        actions.perform();
    }

    public void move_and_click(WebElement ele) {
        action.moveToElement(ele).click().build().perform();
    }

    public void PaginationNext() throws InterruptedException {
        try {
            WebElement next_page1 = driver.findElement(By.xpath("//a[contains(text(),'Next')]"));
            next_page1.click();
        } catch (StaleElementReferenceException ex) {
            ex.printStackTrace();
        }
        Thread.sleep(3000);
    }

    public void PaginationPrevious() throws InterruptedException {
        try {
            WebElement next_page1 = driver.findElement(By.xpath("//a[contains(text(),'Previous')]"));
            next_page1.click();
        } catch (StaleElementReferenceException ex) {
            ex.printStackTrace();
        }
        Thread.sleep(3000);
    }

    public void scrollhorizontal() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(300, 0)", "");
    }

    public boolean verifyvisibleText(String locator) {
        log.info("verify visible Text");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
        return true;
    }

    public String verifyField(String locator) {
        log.info("verify field ");
        scrollElementIntoView(driver, driver.findElement(By.xpath(locator)));
        return driver.findElement(By.xpath(locator)).getText();
    }

    public void clickLink(String link) throws InterruptedException {
        Actions act = new Actions(driver);
        //  scrollElementIntoView(driver,driver.findElement(By.xpath(link)));
        act.moveToElement(driver.findElement(By.xpath(link))).build().perform();
        Thread.sleep(1000);
        act.moveToElement(driver.findElement(By.xpath(link))).click().build().perform();
    }

    public void verifyTitle(String name) throws InterruptedException {
        String title = driver.getTitle();
        log.info("Title is matches to correct page");
        Assert.assertEquals(name, title);
        Thread.sleep(3000);
    }

    public void selectDrpdwnMoreActions(String Value) throws InterruptedException {
        switch (Value) {
            case "Edit QC":
            case "Verification":
            case "Credit Hold":
            case "InFulfillment":
            case "Submitted To vendor":
            case "Acknowledged By vendor":
            case "Confirmed by Vendor":
            case "Rejected By Vendor":
            case "Booked":
            case "Pending Provisioning":
            case "Mark Complete":
            case "Send for Verification":
            case "Release Credit Hold":
            case "Mark Submitted to Vendor":
            case "Acknowledged":
                try {
                    List<WebElement> statuslist = driver.findElements(By.xpath("//div[@class='btn-group show']/div[@id='divToButtonGroup']/a"));
                    System.out.println(statuslist);
                    for (WebElement option : statuslist) {
                        if (option.getText().equals(Value)) {
                            action.moveToElement(option).click().perform();
                            Thread.sleep(6000);
                        }
                    }
                } catch (ElementNotInteractableException | StaleElementReferenceException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void modifyEndDate() throws InterruptedException {
        log.info("user enters end date as near to start date upto 90 days");
        Thread.sleep(1000);
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        cal.add(Calendar.DATE, +5);
        cal.add(Calendar.YEAR,+1);
//        help.get_element("//div[2]/div[3]/div[2]/div/div[1]/div/input").sendKeys(Keys.DELETE);
//        help.get_element("//div[2]/div[3]/div[2]/div/div[1]/div/input").clear();
        String datenew = dateFormat.format(cal.getTime());
        date1 = datenew;
        WebElement end_date = driver.findElement(By.xpath("//div[2]/div[3]/div[2]/div/div[1]/div/input"));
        end_date.sendKeys(Keys.DELETE);
        end_date.clear();
        end_date.sendKeys(date1);
        Thread.sleep(3000);
    }
}

