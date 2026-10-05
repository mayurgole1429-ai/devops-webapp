package com.devops;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WebAppSeleniumIT {

    private static final String APP_URL =
            System.getProperty("app.url", "http://localhost:8080/");

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void greetingShowsEnteredName() {
        // 1. Open the application
        driver.get(APP_URL);
        assertTrue("Home page title should mention DevOps",
                driver.getTitle().contains("DevOps"));

        // 2. Enter data into the form
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        nameInput.clear();
        nameInput.sendKeys("DevOps Engineer");

        // 3. Click the button
        driver.findElement(By.id("greetBtn")).click();

        // 4. Verify the result
        WebElement greeting = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("greeting")));
        assertTrue("Greeting should contain the entered name",
                greeting.getText().contains("Hello, DevOps Engineer!"));
    }

    @Test
    public void emptyNameDefaultsToGuest() {
        driver.get(APP_URL);

        // Submit the form without entering a name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("greetBtn")))
                .click();

        WebElement greeting = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("greeting")));
        assertTrue("Empty name should greet Guest",
                greeting.getText().contains("Hello, Guest!"));
    }
}
