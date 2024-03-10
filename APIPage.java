package com.sprint.qa.util;

import com.microsoft.sqlserver.jdbc.StringUtils;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.sprint.qa.base.TestBase;
import com.sprint.qa.helper.LoggerHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.python.antlr.ast.Str;
import org.testng.Assert;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class APIPage extends TestBase {
    public Map<String,String> api_urls;
    public APIPage() {
        api_urls=new TreeMap<>();
        api_urls.put("search","/v1.0/subscriptionChangeRequest/search");
        api_urls.put("save","/v1.0/user/saveSearchSetting");
        api_urls.put("getsearchsetting","/v1.0/user/GetSearchSetting?screenType=2");
        api_urls.put("deletesetting","/v1.0/user/deleteSearchSetting?screenType=2");
        api_urls.put("exportexcel","/v1.0/subscriptionChangeRequest/exportToExcel");
        api_urls.put("SubscriptionChangeRequestStatus","/v1.0/enumeration/listForDropDown?typeName=SubscriptionChangeRequestStatus");
        api_urls.put("SubscriptionChangeType","/v1.0/enumeration/listForDropDown?typeName=SubscriptionChangeType");
        api_urls.put("CustomerBillingModel","/v1.0/enumeration/listForDropDown?typeName=CustomerBillingModel");
        api_urls.put("RenewalStatus","/v1.0/enumeration/listForDropDown?typeName=RenewalStatus");
        api_urls.put("SubscriptionInvoiceStatus","/v1.0/enumeration/listForDropDown?typeName=SubscriptionInvoiceStatus");
        api_urls.put("SubscriptionInvoicePaymentStatus","v1.0/enumeration/listForDropDown?typeName=SubscriptionInvoicePaymentStatus");
        api_urls.put("getsearchsetting_type4","/v1.0/user/GetSearchSetting?screenType=4");
        api_urls.put("subinvme","/v1.0/user/me");
        api_urls.put("CRD_hist_items","/v1.0/subscription/getItemsHistory?subscriptionID=a769ae1b-3869-414c-aede-d0c3a9e8c7d2");
        api_urls.put("CRD_doc_type","/v1.0/enumeration/listForDropDown?typeName=DocumentAttachmentType");
        api_urls.put("CRD_change_order_dropdown","/v1.0/subscription/listForDropdownChangeOrder?subscriptionID=a769ae1b-3869-414c-aede-d0c3a9e8c7d2");
        api_urls.put("CRD_group_by","/v1.0/document/listAttachmentsGroupedByType?id=a769ae1b-3869-414c-aede-d0c3a9e8c7d2&type=200&internal=-1");
        api_urls.put("CRD_list_drop_down","/v1.0/contact/listForDropDown?id=7546d343-58d5-4e77-8ee8-46f06242c6d6");
        api_urls.put("CRD_get","/v1.0/subscription/get?id=a769ae1b-3869-414c-aede-d0c3a9e8c7d2&scope=items");
        api_urls.put("CRD_list_notes","/v1.0/document/listNotes?id=a769ae1b-3869-414c-aede-d0c3a9e8c7d2&type=200");
        api_urls.put("CRD_sort","/v1.0/subscriptionChangeRequest/getVersions?start=1&records=10&sort=0");
        api_urls.put("CRD_status","/v1.0/enumeration/listForDropDown?typeName=SubscriptionChangeRequestStatus");
        api_urls.put("CRD_contactsrc","/v1.0/enumeration/listForDropDown?typeName=QuoteInternalContactSource");
        api_urls.put("CRD_version","/v1.0/subscriptionChangeRequest/getVersions?start=1&records=10&sort=0");
        api_urls.put("CRD_initiateCR","/v1.0/subscriptionChangeRequest/initiatechangerequest?subscriptionID=976fb0fe-c863-4a63-8f6f-e394c1795b56&subscriptionChangeType=1&scope=header");
        api_urls.put("CRD_doc_con_roletype","/v1.0/enumeration/listForDropDown?typeName=DocumentContributorRoleType");
    }

    static Logger log = LoggerHelper.getLogger(LoggerHelper.class);

    public static String ReadExcelData(String filename) throws IOException {
        String payloaddata = null;
        String path = System.getProperty("user.dir");
        FileInputStream fs = new FileInputStream(path + "/testdata/ExcelReadData/" + filename + ".xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(fs);
        XSSFSheet sh = wb.getSheetAt(0);
        int rowCount = sh.getLastRowNum();
        int colCount = sh.getRow(0).getLastCellNum();
        for (int i = 0; i < rowCount + 1; i++) {
            Row row = sh.getRow(i);
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                switch (cell.getCellType()) {
                    case STRING:
                        payloaddata = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        double payload = cell.getNumericCellValue();
                        break;
                }
            }
        }
        return payloaddata;
    }

    public static boolean WriteExcelData(String filename, int sheetIndex, int rowNum, int colNum, String data) {
        try {
            String path = System.getProperty("user.dir");
            FileInputStream fs = new FileInputStream(path + "/testdata/ExcelWriteData/" + filename + ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            if (rowNum <= 0)
                return false;
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            Row row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(filename))
                    colNum = i;
            }
            if (colNum == -1)
                return false;
            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum - 1);
            if (row == null)
                row = sheet.createRow(rowNum - 1);
            Cell cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);
            cell.setCellValue(data);
            FileOutputStream fileOut = new FileOutputStream(path + "/testdata/ExcelWriteData/" + filename + ".xlsx");
            workbook.write(fileOut);
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getoptionsWithToken() throws IOException {
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        String payload = ReadExcelData("LoginExcelFile");
        request.header("Content-Type", "application/json");
        Response responsefromspecification = request.body(payload).post(prop.getProperty("apiurl") + "/v1.0/authentication/token");
        responsefromspecification.prettyPrint();
        String result = responsefromspecification.getBody().jsonPath().get("response.access_token");
        return result;
    }

    public static Response postsubscriptionVendorInvoiceWithAccessToken(String url, String token, String vendor_invoice) throws URISyntaxException, IOException {
        String[] subscription_invoice_api_body = {
                "id : " + vendor_invoice,
                "vendorErpCode : CISC01",
                "status : Received"
        };
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header(new Header("Authorization", "Bearer" + token));
        Map<String, String> payload = build_api_body(subscription_invoice_api_body);
        Response response = request.contentType(ContentType.JSON).body(payload).post(url);
        int statuscode = response.getStatusCode();
        Assert.assertEquals(statuscode, 200, "Correct status code returned");
        return response;
    }

    public static Map<String, String> build_api_body(String[] body) {
        HashMap<String, String> api_payload = new HashMap<>();
        int payload_size = body.length;
        for (String dict : body) {
            log.info("dict key: " + dict.split(":")[0].trim() + " dict value: " + dict.split(":")[1].trim());
            api_payload.put(dict.split(":")[0].trim(), dict.split(":")[1].trim());
        }
        return api_payload;
    }

    public static Response getresponse(String url, String token){
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        request.header(new Header("Authorization", " Bearer " + token));
        return request.when().get(url);
    }

    public static Response GetStatuscode(String url, String token) throws URISyntaxException {
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        request.header(new Header("Authorization", " Bearer " + token));
        Response response = request.when().get(url);
        int statuscode = response.getStatusCode();
        Assert.assertEquals(statuscode, 200, "Correct status code returned");
        log.info("Correct status code returned :" + statuscode);
        return response;
    }

    public static long GetResponseTimeWithAccessToken(String url, String token) throws URISyntaxException {
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        request.header(new Header("Authorization", " Bearer " + token));
        long timeresponse = request.when().get(url).then().extract().time();
        System.out.println("Response time is in mili sec :" + timeresponse);
        log.info("Response time is in mili sec :" + timeresponse);
        return timeresponse;
    }

    public static void set_request_specification(){

    }

    public static long GetResponseTimeWithAccessTokenPostRequest(String url, String token)  {
        RestAssured.baseURI = prop.getProperty("apiurl");
        RequestSpecification request = RestAssured.given();
        request.header(new Header("Authorization", " Bearer " + token));
        long timeresponse = request.when().post(url).then().extract().time();
        System.out.println("Response time is in mili sec :" + timeresponse);
        log.info("Response time is in mili sec :" + timeresponse);
        String responsetime = String.valueOf(timeresponse);
        return timeresponse;
    }

    public int post_xml_document(Document doc,String op_file,String api_url) throws TransformerException, IOException, InterruptedException {
        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        DOMSource xmlsrc = new DOMSource(doc);
        StreamResult output_wrap;
        try (FileWriter tempfile = new FileWriter(download_path + File.separator + op_file)) {
            output_wrap = new StreamResult(tempfile);
            TransformerFactory.newInstance().newTransformer().transform(xmlsrc, output_wrap);
        }
        Thread.sleep(6000);
        File file1=new File(download_path + File.separator + op_file);
        log.info("url: "+prop.getProperty(api_url));
        RestAssured.baseURI = prop.getProperty(api_url);
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/xml");
        request.header("Accept","*/*");
        request.header("x-api-key", "b2d5ee84-c6f6-471b-9097-e4ecf83becd7");
        request.header("Connection","keep-alive");
        Response response = request.contentType(ContentType.XML).accept(ContentType.XML).body(file1)
                .post(prop.getProperty(api_url));
        log.info("Status code: " + response.statusCode());
        Thread.sleep(5000);
        try {
            File xmlfile = new File(download_path+File.separator+"temp.xml");
            if(xmlfile.exists()){
                xmlfile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.statusCode();
    }

    public static int getVICreatedAPISubIDandPOUnique() throws IOException {
        RestAssured.baseURI = prop.getProperty("apiurl_vendorInvoices");
        RequestSpecification request = RestAssured.given();
        String payload = ReadExcelData("subID&POUnique");
        //request.header("Content-Type", "application/json");
        request.header(new Header("Authorization", " Bearer " + getoptionsWithToken()));
        request.header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON);
        request.header("x-api-key", "b2d5ee84-c6f6-471b-9097-e4ecf83becd7");
        Response responsefromspecification = request.body(payload).post(prop.getProperty("apiurl_vendorInvoices") + ":443/dev3/rest/SubscriptionTest/XMLInjection/?postToMO=yes");
        int status_code=responsefromspecification.getStatusCode();
        return status_code;
//        RequestSpecification request = RestAssured.given();
//        request.header(new Header("Authorization", " Bearer " + getoptionsWithToken()));
//        String payload = ReadExcelData("subID&POUnique");
//        request.header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON);
//        Response responsefromspecification = request.body(payload).post(prop.getProperty("apiurl_vendorInvoices") + ":443/dev3/rest/SubscriptionTest/XMLInjection/?postToMO=yes");
////        responsefromspecification.prettyPrint();
//        log.info("status code: " + responsefromspecification.getStatusCode());
//        return responsefromspecification.prettyPrint();
    }

    public static int getVICreatedAPISubCancelledSubIdandPOIsUnique() throws IOException {
        RestAssured.baseURI = prop.getProperty("apiurl_vendorInvoices");
        RequestSpecification request = RestAssured.given();
        String payload = ReadExcelData("subCancelledSubID&POIsUnique");
        request.header(new Header("Authorization", " Bearer " + getoptionsWithToken()));
        request.header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON);
        request.header("x-api-key", "b2d5ee84-c6f6-471b-9097-e4ecf83becd7");
        Response responsefromspecification = request.body(payload).post(prop.getProperty("apiurl_vendorInvoices") + ":443/dev3/rest/SubscriptionTest/XMLInjection/?postToMO=yes");
        int status_code = responsefromspecification.getStatusCode();
        return status_code;
    }

    public static int subIDUniquePOIsNot() throws IOException {
        RestAssured.baseURI = prop.getProperty("apiurl_vendorInvoices");
        RequestSpecification request = RestAssured.given();
        String payload = ReadExcelData("subIDUniquePOIsNot");
        request.header(new Header("Authorization", " Bearer " + getoptionsWithToken()));
        request.header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON);
        request.header("x-api-key", "b2d5ee84-c6f6-471b-9097-e4ecf83becd7");
        Response responsefromspecification = request.body(payload).post(prop.getProperty("apiurl_vendorInvoices") + ":443/dev3/rest/SubscriptionTest/XMLInjection/?postToMO=yes");
        int status_code = responsefromspecification.getStatusCode();
        return status_code;
    }
    public void setCookie(String name, String value, String domain) {
        Cookie cookie = new Cookie.Builder(name, value).domain(domain).build();
        driver.manage().addCookie(cookie);
    }

    public void myOrdersLogin() throws Throwable
    {
        JSONObject requestParams = new JSONObject();
        requestParams.put("fqdn", prop.getProperty("myorderurl"));
        requestParams.put("user", "myordersqatesting");
        requestParams.put("password", "Pres8900ab!!!!");   requestParams.put("domain", "PresidioAD");
        RestAssured.useRelaxedHTTPSValidation();
        Response response = RestAssured.given().header("Content-type", "application/json").and().body(requestParams.toJSONString()).when().post("https://aspx-forms-service.presidio.com").then().extract().response();
        String token = response.asString();
        final String loginUrl = "https://myorders-qa2.presidio.com/";
        driver.get(loginUrl);
        setCookie(".ASPXAUTH",token,"presidio.com");
        driver.get(loginUrl);
    }
}