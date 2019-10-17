package com.signity.ziprecruiterproj;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SeleniumInstances {

	@Bean
	public WebDriver webDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		// options.addArguments("--headless");
		return new ChromeDriver(options);
	}

	@Bean
	public FluentWait<WebDriver> fluentWait(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		return wait.withTimeout(Duration.ofSeconds(60)).pollingEvery(Duration.ofSeconds(2));// .ignoring(Exception.class);
	}

	@Bean
	public Actions actions(WebDriver driver) {
		return new Actions(driver);
	}
}
