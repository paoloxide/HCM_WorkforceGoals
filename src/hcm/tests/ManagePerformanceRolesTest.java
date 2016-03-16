package hcm.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.net.NetworkUtils;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;
import static util.ReportLogger.logFailure;

import java.util.ArrayList;

public class ManagePerformanceRolesTest extends BaseTest{
	private final static int MAX_TIMEOUT = 60;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		managePerf();
	  
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

	public void managePerf() throws Exception{
			
		LoginPage login = new LoginPage(driver);
		//takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
		
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		task.clickTask("Manage Implementation Projects");
		Thread.sleep(3000);
		//takeScreenshot();
		
		task.clickCreateIcon();
		Thread.sleep(5000);
		//takeScreenshot();
		String searchName = "Name";
		String searchData = getExcelData(2, 7, "text");
		String dataSearchPath = TaskUtilities.retryingSearchInput(searchName);
		TaskUtilities.jsFindThenClick(dataSearchPath);
		driver.findElement(By.xpath(dataSearchPath)).clear();
		task.enterTextByXpath(dataSearchPath, searchData);
		
		
		task.waitForElementToBeClickable(MAX_TIMEOUT, "//button[text()='Ne']");
		task.clickNextButton();
		Thread.sleep(250);
		task.clickNextButton();
		
		Thread.sleep(1000);
		
		////label[text()='Implementation Project']/../../td[text()='Implementation Project-193a']
		////h1[contains(text(),'Create Implementation')]
		String locRefPath = "//label[contains(text(),'Implementation Project')]/../.."+"/td[text()='"+searchData+"']";
		System.out.println("locRefPath is "+locRefPath);
		TaskUtilities.customWaitForElementVisiblity(locRefPath, MAX_TIMEOUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		
		int expectedTaskValue = defaultmainTaskValue;
		int subTaskExpectedValue = defaultsubTaskValue;
		String mainTaskCommonPath, subTaskCommonPath, refID;
		String expandableSubTaskPath = null;
		String thirdLevelSubTaskPath = null;
		ArrayList<String> currSubTaskAttr = new ArrayList<String>();
		ArrayList<String> prevSubTaskAttr = new ArrayList<String>();
		ArrayList<String> subTaskL3Attr   = new ArrayList<String>();
		boolean isScrollingDown = true;
		String mainRef = mainTaskReference;
		String subRef = subTaskReference;
		
		//Initialize values of the ArrayList
		currSubTaskAttr.add("0"); currSubTaskAttr.add("0");
		prevSubTaskAttr.add("0"); prevSubTaskAttr.add("0");
		subTaskL3Attr.add("0");   subTaskL3Attr.add("0");
		
		for(int rowNum = 2; getExcelData(rowNum, expectedTaskValue).length() > 0; rowNum++){

			//START OF MAIN TASK
			task.waitForElementToBeInvisible("//div[text()='Fetching Data...']", "Fetching Data...", MAX_TIMEOUT);
			refID = task.findMainTaskUniqueID(rowNum, expectedTaskValue);
			mainTaskCommonPath = "//tr[@_"+mainRef+"='"+refID+"']/td/div/table/tbody/tr/td/div[text()='"+getExcelData(rowNum, expectedTaskValue)+"']";
			TaskUtilities.retryingFindClick(By.xpath(mainTaskCommonPath));
			
			System.out.println("Main ref is now... *****" );
			
			
			while(!is_element_visible(mainTaskCommonPath, "xpath")){
				isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "normal");
			}
			
			if(is_element_visible(mainTaskCommonPath+"/span/a[contains(@title,'Expand')]","xpath")){
						
					TaskUtilities.retryingFindClick(By.xpath(mainTaskCommonPath));
					task.clickMainTaskCheckbox(rowNum, expectedTaskValue, mainTaskCommonPath);
						
					task.clickExpandFolder(mainTaskCommonPath);
					TaskUtilities.customWaitForElementVisiblity(mainTaskCommonPath+"/span/a[contains(@title,'Collapse')]", MAX_TIMEOUT);
					
				}else {
	
					System.out.println("Visible Element is Not Expandable: "+mainTaskCommonPath );
	
					TaskUtilities.retryingFindClick(By.xpath(mainTaskCommonPath));
					Thread.sleep(150);
					task.clickMainTaskCheckbox(rowNum, expectedTaskValue, mainTaskCommonPath);
				}
			
			Thread.sleep(250);
			//END OF MAIN TASK
			
			//START OF SUB TASK
				task.waitForElementToBeInvisible("//div[text()='Fetching Data...']", "Fetching Data...", MAX_TIMEOUT);
				refID = task.findMainTaskUniqueID(rowNum, expectedTaskValue);
				
				while (getExcelData(rowNum, subTaskExpectedValue).length() > 0){
					
					subTaskCommonPath  = "//tr[@_"+subRef+"='"+refID+"' or contains(@_"+subRef+",'"+refID+"_')]/td/div/table/tbody/tr/td/div[text() = '"+getExcelData(rowNum, subTaskExpectedValue)+"']";

					System.out.println("\n**********\nNext Task: "+getExcelData(rowNum, subTaskExpectedValue));
					System.out.println("Sub ref is now... *****" );

					//Adjust view to make element visible in the DOM...
					try{
						while(!is_element_visible(subTaskCommonPath, "xpath")){
							System.out.println("Element Path is not yet visible: "+subTaskCommonPath );
							isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "normal");
							TaskUtilities.fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", MAX_TIMEOUT);
						}

						task.scrollElementIntoView(subTaskCommonPath);
					}catch(StaleElementReferenceException e){
						//Skips staleElement....
					}catch(org.openqa.selenium.NoSuchElementException e3){
						isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "normal");
					}catch(Exception e2){
						//Skips visibility checks
					}
					
					//Reworking All Elements before processing.....
					try{
							TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
							//setting values after element refresh...
							currSubTaskAttr.set(0, (String)task.findByXpath(subTaskCommonPath+"/../../../../../../..").getAttribute("_afrrk"));
							currSubTaskAttr.set(1, (String)task.findByXpath(subTaskCommonPath+"/../../../../../../..").getAttribute("_afrap"));
							if((prevSubTaskAttr.get(1)+"_"+prevSubTaskAttr.get(0)).equals(currSubTaskAttr.get(0))){
								System.out.println("Third Level folder found.. adjustment in progress...");
								thirdLevelSubTaskPath = expandableSubTaskPath;
								subTaskL3Attr.set(0,(String)task.findByXpath(thirdLevelSubTaskPath).getAttribute("_afrrk"));
								subTaskL3Attr.set(1,(String)task.findByXpath(thirdLevelSubTaskPath).getAttribute("_afrap"));
								expandableSubTaskPath = null;
							}
							
							//Realign SubTaskCommonPath
							System.out.println("Reworking subTask Path...");
							subTaskCommonPath ="//tr[@_afrrk = '"+currSubTaskAttr.get(0)+"' and @_afrap = '"+currSubTaskAttr.get(1)+"']/td/div/table/tbody/tr/td/div[text() = '"+getExcelData(rowNum, subTaskExpectedValue)+"']";
							System.out.println("New subTask Path... *****");
						}catch(Exception e){
							//Nothing to do here..
						}
					
					//Refresh Element reference in DOM after detecting it..
					TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
					
					if(is_element_visible(subTaskCommonPath, "xpath")){
						System.out.println("Visibility of Element path CONFIRMED...");
						
						TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
						task.scrollElementIntoView(subTaskCommonPath);
					}
					
					if(is_element_visible(subTaskCommonPath+"/span/a[contains(@title,'Expand')]","xpath")){
						System.out.println("Visible Element is Expandable: *****" );
						if(expandableSubTaskPath != null && !currSubTaskAttr.get(1).contains(prevSubTaskAttr.get(0))){
							if(!is_element_visible(expandableSubTaskPath,"xpath")){
								isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "normal");
							}
							TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
							task.clickCollapseFolder(expandableSubTaskPath);
							Thread.sleep(2000);
							
							//Collapses expandable sub task that has a sub task...
							if(prevSubTaskAttr.get(1).equals( subTaskL3Attr.get(1)+"_"+subTaskL3Attr.get(0) )
									&& !currSubTaskAttr.get(1).contains( subTaskL3Attr.get(0) ) ){
								
								TaskUtilities.retryingFindClick(By.xpath(thirdLevelSubTaskPath));
								task.clickCollapseFolder(thirdLevelSubTaskPath);
								Thread.sleep(1000);
							}
							
						}
						
						expandableSubTaskPath = subTaskCommonPath;
						while(!is_element_visible(subTaskCommonPath, "xpath")){
							isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "normal");
							TaskUtilities.fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", MAX_TIMEOUT);
						}
						
						//Finally expands the target subtask folder...
						if(is_element_visible(subTaskCommonPath+"/span/a[contains(@title,'Expand')]", "xpath")){
							TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
							task.clickExpandFolder(subTaskCommonPath);
							Thread.sleep(1000);
							
							while(!is_element_visible(subTaskCommonPath+"/span/a[contains(@title,'Collapse')]", "xpath")){}
							isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "small");
							TaskUtilities.fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", MAX_TIMEOUT);
						}
					}
					 
					//Scroll down to the subtaskExpectedValue;
					System.out.println("Ticking Target: "+getExcelData(rowNum, subTaskExpectedValue));
					task.clickElementByXPath(subTaskCommonPath);

					//Ticking the expected Tasks;
					System.out.println("ELEMENT FOUND: Proceed to ticking...");
						
						try{
							TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
							task.clickSubTaskCheckbox(rowNum, subTaskExpectedValue, subTaskCommonPath);
						} catch(Exception e){
							isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "small");
							
							TaskUtilities.retryingFindClick(By.xpath(subTaskCommonPath));
							task.clickSubTaskCheckbox(rowNum, subTaskExpectedValue, subTaskCommonPath);
						}
						
					
					//Save prevSubTaskAttr
					prevSubTaskAttr.set(0, currSubTaskAttr.get(0));
					prevSubTaskAttr.set(1, currSubTaskAttr.get(1));
					Thread.sleep(1000);
					subTaskExpectedValue += 2;
				}
			
			//Collapsing Collapsible SubTask Folders
			//Ensuring Subtask to collapse is fresh...
			if(expandableSubTaskPath != null) TaskUtilities.retryingFindClick(By.xpath(expandableSubTaskPath)); 
			if(is_element_visible(expandableSubTaskPath+"/span/a[contains(@title,'Collapse')]","xpath")){
				System.out.println("Visible Element is Collapsible: *****" );
				task.clickCollapseFolder(expandableSubTaskPath);
				Thread.sleep(2000);
			}
			
			TaskUtilities.scrollDownToElement(false, "");
			Thread.sleep(1000);
			
			//Collapsing MainTask Folder
			if(!is_element_visible(mainTaskCommonPath, "xpath")){
				TaskUtilities.scrollDownToElement(true, "");
				while(!is_element_visible(mainTaskCommonPath, "xpath")){
					isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "");
				}
			}
			TaskUtilities.retryingFindClick(By.xpath(mainTaskCommonPath)); //Ensuring Main Task to collapse is fresh...
			if(is_element_visible(mainTaskCommonPath+"/span/a[contains(@title,'Collapse')]","xpath")){
				task.clickElementByXPath(mainTaskCommonPath);
				task.clickCollapseFolder(mainTaskCommonPath);
				Thread.sleep(3000);
				
			}
			
			System.out.println("Moving on to next row...");
			expandableSubTaskPath = null;
			thirdLevelSubTaskPath = null;
			subTaskExpectedValue = defaultsubTaskValue;
		}
		
		task.clickSaveandOpenProject();
		task.waitForElementToBeClickable(3600, "//button[text()='D']");
		task.clickDoneButton();
		
	} 	  
}