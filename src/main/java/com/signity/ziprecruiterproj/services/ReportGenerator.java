package com.signity.ziprecruiterproj.services;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.signity.ziprecruiterproj.configs.CommandArguments;
import com.signity.ziprecruiterproj.models.LoginCredential;
import com.signity.ziprecruiterproj.models.ParsedDataModal;
import com.signity.ziprecruiterproj.models.PropertyFile;
import com.signity.ziprecruiterproj.utils.AppUtils;

@Component
public class ReportGenerator {

	@Autowired
	private WebDriver driver;
	@Autowired
	private FluentWait<WebDriver> fluentWait;
	@Autowired
	private AppUtils appUtils;
	@Autowired
	private Gson gson;
	private Map<String, PropertyFile> propertyFileMap;
	private LoginCredential loginCredential;

	@PostConstruct
	private void init() {
		this.propertyFileMap = appUtils.getSourceDataMap();
		this.loginCredential = appUtils.getLoginCredentials();
	}

	/**
	 * Generate Report
	 * 
	 * @param
	 */
	public void generateReport() throws Exception {
		System.out.println(propertyFileMap);
		System.out.println(CommandArguments.printData());
		System.out.println(loginCredential);
		loginPortal();
		openPlacementMenu();
		Set<Map.Entry<String, PropertyFile>> entrySet = propertyFileMap.entrySet();
		for (Map.Entry<String, PropertyFile> entry : entrySet) {
			String publisherName = entry.getKey();
			if (CommandArguments.jobSourceName.equals("all")) {
				openPlacementPublisherByName(publisherName);
			} else {
				if (!publisherName.equals(CommandArguments.jobSourceName)) {
					continue;
				}
				openPlacementPublisherByName(publisherName);
			}
		}
	}

	/**
	 * Login to The Portal
	 *
	 */
	private void loginPortal() throws Exception {
		driver.get("https://www.ziprecruiter.com/marketplace/login");
		// Fill Login Credentials
		fluentWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name=email]")));
		WebElement inputEmail = driver.findElement(By.cssSelector("input[type=email]"));
		inputEmail.clear();
		inputEmail.sendKeys(appUtils.decrypt(loginCredential.getLoginEmail()));

		fluentWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name=password]")));
		WebElement inputPass = driver.findElement(By.cssSelector("input[type=password]"));
		inputPass.clear();
		inputPass.sendKeys(appUtils.decrypt(loginCredential.getLoginPass()));
		inputPass.sendKeys(Keys.ENTER);
	}

	/**
	 * Open Placement Menu via Click Event
	 */
	private void openPlacementMenu() {
		fluentWait.until(ExpectedConditions.elementToBeClickable(By.id("partnerPlacementsTab")));
		WebElement propertyMenu = driver.findElement(By.id("partnerPlacementsTab")).findElement(By.tagName("a"));
		propertyMenu.click();
	}

	/**
	 * Open Publisher Page By @Param: publisherName and Generate Report
	 * 
	 * @param publisherName
	 *            name of publisher for report
	 */
	private void openPlacementPublisherByName(String publisherName) {

		tableLoaderCheckPlacementPublisher();

		int rowCount = driver.findElement(By.cssSelector("div[id='tablebuilder-ui-summary']"))
				.findElement(By.tagName("table")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
				.size();
		System.out.println("rowCount " + rowCount);
		for (int i = 0; i < rowCount; i++) {
			tableLoaderCheckPlacementPublisher();
			WebElement anchorTag = driver.findElement(By.cssSelector("div[id='tablebuilder-ui-summary']"))
					.findElement(By.tagName("table")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
					.get(i).findElement(By.cssSelector("td[class='non-admin']")).findElement(By.tagName("a"));

			String rowPublisherName = anchorTag.getText();
			System.out.println(rowPublisherName);
			if (!publisherName.equals("all")) {
				if (!rowPublisherName.equals(publisherName)) {
					continue;
				}

				anchorTag.click();
				openReport();
				selectMonthDropDown(publisherName);
				openPlacementMenu();

			} else {
				anchorTag.click();
				openReport();
				selectMonthDropDown(publisherName);
				openPlacementMenu();
			}
		}
	}

	/**
	 * Placement partner Loader Wait
	 *
	 */
	private void tableLoaderCheckPlacementPublisher() {
		WebElement loader = driver.findElement(By.cssSelector("div[id='tablebuilder-ui-summary']"))
				.findElement(By.cssSelector("div[class='data-table-overlay']"));
		fluentWait.until(ExpectedConditions.invisibilityOf(loader));
	}

	/**
	 * partner-revenue-detail Loader Wait
	 *
	 */
	private void tableLoaderCheckRevenueDetailReport() {
		WebElement loader = driver.findElement(By.cssSelector("div[id='chair_spinner']"));
		fluentWait.until(ExpectedConditions.invisibilityOf(loader));
	}

	/**
	 * Open Reports Tab Inside PlacementMenu
	 *
	 */
	private void openReport() {
		WebElement reportMenuElement = driver.findElement(By.cssSelector("ul[id='board_links_wrapper']"))
				.findElement(By.cssSelector("li[data-reports]")).findElement(By.tagName("a"));
		fluentWait.until(ExpectedConditions.elementToBeClickable(reportMenuElement));
		reportMenuElement.click();
	}

	/**
	 * Select Month Dropdown
	 * 
	 * @param publisherName
	 *            name of publisher for report
	 */
	private void selectMonthDropDown(String publisherName) {
		String monthToSelect = appUtils.getMonthToSelect();
		System.out.println("monthToSelect>> " + monthToSelect);

		WebElement selectElement = driver.findElement(By.cssSelector("form[id='month_select']"))
				.findElement(By.cssSelector("select[id='report_month']"));
		fluentWait.until(ExpectedConditions.elementToBeClickable(selectElement));
		Select monthSelect = new Select(selectElement);
		monthSelect.selectByVisibleText(monthToSelect);
		tableLoaderCheckRevenueDetailReport();

		parsingPartnerRevenueTableData(publisherName);

		System.out.println();
	}

	/**
	 * Parsing and Generate Json File of Partner Revenue Detail Table
	 * 
	 * @param publisherName
	 *            name of publisher for report
	 */
	private void parsingPartnerRevenueTableData(String publisherName) {

		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		List<WebElement> tableRowsElements = driver.findElement(By.cssSelector("div[id='report_container']"))
				.findElement(By.cssSelector("table[id='table1']")).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));

		try {
			fluentWait.until(ExpectedConditions.visibilityOfAllElements(tableRowsElements));
		} catch (Exception e) {
			e.printStackTrace();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			tableRowsElements = driver.findElement(By.cssSelector("div[id='report_container']"))
					.findElement(By.cssSelector("table[id='table1']")).findElement(By.tagName("tbody"))
					.findElements(By.tagName("tr"));
			fluentWait.until(ExpectedConditions.visibilityOfAllElements(tableRowsElements));
		}

		List<ParsedDataModal> parsedDataModals = new ArrayList<>();

		Date startDate=appUtils.getStartDate();
		Date endDate=appUtils.getEndDate();
		for (int i = 1; i < tableRowsElements.size(); i++) {


			List<WebElement> columnList = tableRowsElements.get(i).findElements(By.tagName("td"));
			Date eventSourceDate=appUtils.getEventDateByString(columnList.get(0).getText().split(" ")[0]);
			if((eventSourceDate.compareTo(startDate)==0 || eventSourceDate.compareTo(startDate)>0) &&
					(eventSourceDate.compareTo(endDate)==0 || eventSourceDate.compareTo(endDate)<0)){
				ParsedDataModal modal = new ParsedDataModal();
				modal.setEventDate(eventSourceDate);
				modal.setJobSource(propertyFileMap.get(publisherName).getDataBaseName());
				// modal.setJobSource(publisherName);
				modal.setPaidClicks(appUtils.removeNonDigitChars(columnList.get(1).getText()));
				modal.setUnpaidClicks(appUtils.removeNonDigitChars(columnList.get(2).getText()));
				modal.setPaidRevenue(appUtils.removeNonDigitChars(columnList.get(4).getText()));
				parsedDataModals.add(modal);
			}
		}

		System.out.println("Start Extracting Data For " + publisherName + "=================");
		System.out.println(gson.toJson(parsedDataModals));

		// Saving Data in File...
		// appUtils.saveJsonData(parsedDataModals,
		// propertyFileMap.get(publisherName).getFilePath() +
		// propertyFileMap.get(publisherName).getFileName());
		appUtils.deleteFileIfExist(propertyFileMap.get(publisherName).getFilePath() + propertyFileMap.get(publisherName).getFileName());
		appUtils.saveRedshiftData(parsedDataModals,
				propertyFileMap.get(publisherName).getFilePath() + propertyFileMap.get(publisherName).getFileName());
		System.out.println("Finished Extracting Data For " + publisherName + "=================");
	}

}
