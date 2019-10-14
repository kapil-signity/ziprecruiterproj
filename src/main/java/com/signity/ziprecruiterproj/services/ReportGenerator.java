package com.signity.ziprecruiterproj.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.ziprecruiterproj.models.LoginCredential;
import com.signity.ziprecruiterproj.models.PropertyFile;
import com.signity.ziprecruiterproj.utils.AppUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

@Component
public class ReportGenerator {

    @Autowired
    private WebDriver driver;
    @Autowired
    private FluentWait<WebDriver> fluentWait;
    @Autowired
    private Actions actions;
    @Autowired
    private AppUtils appUtils;
    private Map<String,PropertyFile> propertyFileMap;
    private LoginCredential loginCredential;

    @PostConstruct
    private void init() {
        this.propertyFileMap=appUtils.getSourceDataMap();
        this.loginCredential=appUtils.getLoginCredentials();
    }


    public void generateReport(){
        System.out.println(propertyFileMap);
        System.out.println(loginCredential);
        loginPortal();
    }

    private void loginPortal() {
        //Fill Login Credentials
        fluentWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name=email]")));
        WebElement inputEmail=driver.findElement(By.cssSelector("input[type=email]"));
        inputEmail.clear();
        inputEmail.sendKeys(loginCredential.getLoginEmail());

        fluentWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name=password]")));
        WebElement inputPass=driver.findElement(By.cssSelector("input[type=password]"));
        inputPass.clear();
        inputPass.sendKeys(loginCredential.getLoginPass());
        inputPass.sendKeys(Keys.ENTER);

        fluentWait.until(ExpectedConditions.elementToBeClickable(By.id("partnerPlacementsTab")));
        WebElement propertyMenu=driver.findElement(By.id("partnerPlacementsTab")).findElement(By.tagName("a"));
        propertyMenu.click();
    }


}
