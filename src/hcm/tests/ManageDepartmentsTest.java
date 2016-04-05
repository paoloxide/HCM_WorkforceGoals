package hcm.tests;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.util.ArrayList;
import java.util.List;

import static common.ExcelUtilities.getCellType;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;
import org.testng.annotations.Test;

import common.BaseTest;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;
import hcm.pageobjects.WorkforceStructureTasksPage;
import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

public class ManageDepartmentsTest extends BaseTest{
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		createDepartment();
	  
	  	}
	
        catch (AssertionError ae)
        {
            //takeScreenshot();
            logFailure(ae.getMessage());

            throw ae;
        }
        catch (Exception e)
        {
            //takeScreenshot();
            logFailure(e.getMessage());

            throw e;
        }
    }

	public void createDepartment() throws Exception{
			
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Workforce Structures");
			
		WorkforceStructureTasksPage task = new WorkforceStructureTasksPage(driver);
		takeScreenshot();
		
		task.waitForElementToBeClickable(60, "//li/a[text()='Manage Departments']");
		task.clickTask("Manage Departments");
		Thread.sleep(5000);
		//takeScreenshot();
		
		task.clickCreate();
		Thread.sleep(5000);
		//task.waitForElementToBeClickable(10, "//td/label[text()='Effective Start Date']/../../td/input");

		//Enable task
		int inputLabel = 7;
		int inputs = 8;
		int rowNum = 8;
		long startTime;
		final int TIMEOUT_IN_MILLIS = 60000;
		String type, inputValue, dataLocator, dataPath, nextDataLocator = "", nextDataPath;
		boolean isScrollingDown = true;
		

		task.waitForElementToBeClickable(10, "//td/label[text()='"+getExcelData(inputLabel, 8, "text")+"']/../../td/input");
		inputloop:
		for(int colNum = 8;getExcelData(inputLabel, colNum, "text").length()>0; colNum++){
			dataLocator = getExcelData(inputLabel, colNum, "text");
			System.out.println("Filtering text....."+dataLocator);
			dataLocator = task.filterDataLocator(dataLocator);
			
			/*if(getCellType(inputLabel, colNum) == 1){
						type = "text";

				}*/if(dataLocator.contains("Date")){
						type = "date";
					}else if(dataLocator.contains("Time")){
						type = "time";
					}else{
						type = "text";
					}
				
			inputValue = getExcelData(inputs, colNum, type);
			System.out.println("Input value....."+inputValue);
			
			//Proceed on Iterating all the inputs....
			dataPath =  TaskUtilities.retryingSearchInput(dataLocator);
			
			if(dataPath.indexOf("select") != -1){
					TaskUtilities.jsFindThenClick(dataPath);
					if(!inputValue.isEmpty() || !inputValue.contentEquals("")){
							TaskUtilities.jsFindThenClick(dataPath+"/option[text()='"+inputValue+"']");
							
						} else{
							//Do nothing here...
						}
				} else{

					TaskUtilities.jsFindThenClick(dataPath);
					driver.findElement(By.xpath(dataPath)).clear();
					task.enterTextByXpath(dataPath, inputValue);
					
				}
			
			//Triggering Next Button.....
			System.out.println("Checking next data locator.....");
			nextDataLocator = getExcelData(inputLabel, colNum+1, "text");
			nextDataLocator = task.filterDataLocator(nextDataLocator);
			nextDataPath = TaskUtilities.retryingSearchInput(nextDataLocator);
			
			if(nextDataLocator.isEmpty() || nextDataLocator.contentEquals("")){
					break inputloop;
				
				}else if(nextDataPath == null){
						Thread.sleep(500); task.clickNext();
						
						//Stop the loop after a set amount of time.....
						startTime = System.currentTimeMillis(); 
						while(!is_element_visible("//td/label[text()='"+nextDataLocator+"']", "xpath")){
							TaskUtilities.jsCheckMessageContainer();
							TaskUtilities.jsCheckMissedInput();
							
							if(System.currentTimeMillis()-startTime > TIMEOUT_IN_MILLIS){
								throw new TimeoutException(TIMEOUT_IN_MILLIS/1000+" seconds has passed after waiting for the element...");
							}
						}
					}
		}
	
		
		Thread.sleep(3000);
		task.clickSubmitButton();
		task.waitForElementToBeClickable(10, "//button[text()='es']"); Thread.sleep(250);
		task.clickYesButton();
		task.waitForElementToBeClickable(10, "//button[text()='O']"); Thread.sleep(250);
		task.clickOKButton();
		//task.clickSaveandCloseButton();
		Thread.sleep(5000);
		
		//Verifying if the department has been added.....
		task.clickTask("Manage Departments");
		Thread.sleep(10000);
		//task.enterSearchData("Name", getExcelData(inputs, 10, "text"));
		String searchData = getExcelData(inputs, 10, "text");
		searchData = task.filterDataLocator(searchData);
		String dataSearchPath = TaskUtilities.retryingSearchInput("Name");
		TaskUtilities.jsFindThenClick(dataSearchPath);
		driver.findElement(By.xpath(dataSearchPath)).clear();
		task.enterTextByXpath(dataSearchPath, searchData);
		
		
		startTime = System.currentTimeMillis();
		while(!task.jsGetInputValue(dataSearchPath).contentEquals(searchData)){
			if(System.currentTimeMillis()-startTime > TIMEOUT_IN_MILLIS){
				throw new TimeoutException(TIMEOUT_IN_MILLIS/1000+" seconds has passed after waiting for the element...");
			}
		}
		
		//Test Case Verification process...
		task.clickSearchButton();
		Thread.sleep(2000);
		task.clickSearchButton();
		Thread.sleep(10000);
		takeScreenshot();
		
		TaskUtilities.jsFindThenClick("//a[text()='"+searchData+"']");
		Thread.sleep(7500);
		takeScreenshot();
		
		TaskUtilities.jsScrollIntoView("//h2[contains(text(),'Organization Information EFF: Department Details')]");
		Thread.sleep(2500);
		takeScreenshot();
		
		isScrollingDown = task.jsScrollDown(isScrollingDown);
		takeScreenshot();
		
		//Thread.sleep(3000);
		//task.clickSaveandCloseButton();
		//Thread.sleep(10000);
		
		//Ending message::
				Thread.sleep(5000);
				//takeScreenshot();
				
				System.out.println("Department Creation Completed\n***************\n");
				log("Department Creation Completed");
				
				Thread.sleep(1500);
				//takeScreenshot();
	}
}