import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Set;
import java.util.logging.Logger;

public class IFrameTabHandlingTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final Logger logger = Logger.getLogger(IFrameTabHandlingTest.class.getName());

    @BeforeMethod
    public void setup() {
        logger.info("Initializing WebDriver...");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("http://demo.guru99.com/test/guru99home");

        logger.info("Setting up explicit wait...");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testIFrameAndTabHandling() {
        logger.info("Switching to iFrame and clicking the image...");
        switchToIFrameById("a077aa5e");

        WebElement iframeImage = waitUntilElementClickable(By.cssSelector("img[src*='Jmeter720.png']"));
        iframeImage.click();

        logger.info("Handling new tab...");
        handleNewTabAndClose();

        logger.info("Submitting email in 'Email Submission' form...");
        switchToDefaultContent();
        WebElement emailField = waitUntilElementPresent(By.id("philadelphia-field-email"));
        emailField.sendKeys("testautomation@example.com");
        driver.findElement(By.id("philadelphia-field-submit")).click();

        logger.info("Validating popup message...");
        validateAlertMessage("Successfully");

        logger.info("Navigating to Tooltip page...");
        navigateToTooltipPage();

        logger.info("Validating presence of 'Download now' button...");
        assertElementIsVisible(By.id("download_now"), "'Download now' button wasn't found on the tooltip page.");
    }

    @AfterMethod
    public void teardown() {
        logger.info("Closing browser session...");
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement waitUntilElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitUntilElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    private void switchToIFrameById(String frameId) {
        try {
            logger.info("Switching to iFrame with ID: " + frameId);
            driver.switchTo().frame(frameId);
        } catch (NoSuchFrameException e) {
            String errorMessage = "Unable to switch to iFrame with ID: " + frameId;
            logger.severe(errorMessage);
            Assert.fail(errorMessage);
        }
    }

    private void handleNewTabAndClose() {
        String mainWindow = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();

        if (windowHandles.size() <= 1) {
            String errorMessage = "New tab did not open as expected.";
            logger.severe(errorMessage);
            Assert.fail(errorMessage);
        }

        for (String handle : windowHandles) {
            if (!handle.equals(mainWindow)) {
                logger.info("Switching to new tab...");
                driver.switchTo().window(handle);
                driver.close();
                break;
            }
        }

        logger.info("Returning to the main window...");
        driver.switchTo().window(mainWindow);
    }

    private void validateAlertMessage(String expectedMessage) {
        try {
            logger.info("Switching to alert popup...");
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.info("Alert message: " + alertText);
            Assert.assertTrue(alertText.contains(expectedMessage), "Expected alert message to contain: " + expectedMessage);
            alert.accept();
        } catch (NoAlertPresentException e) {
            String errorMessage = "No alert was present when attempting to validate the popup message.";
            logger.severe(errorMessage);
            Assert.fail(errorMessage);
        }
    }

    private void navigateToTooltipPage() {
        WebElement seleniumMenu = waitUntilElementClickable(By.linkText("Selenium"));
        seleniumMenu.click();
        WebElement tooltipOption = waitUntilElementClickable(By.linkText("Tooltip"));
        tooltipOption.click();
        logger.info("Navigated to Tooltip page.");
    }

    private void assertElementIsVisible(By locator, String failMessage) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            Assert.assertTrue(element.isDisplayed(), failMessage);
        } catch (TimeoutException e) {
            logger.severe(failMessage);
            Assert.fail(failMessage);
        }
    }
}