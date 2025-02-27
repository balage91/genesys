import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class RichTextEditorTest {

    // Initialize SLF4J logger
    private static final Logger logger = LoggerFactory.getLogger(RichTextEditorTest.class);

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        logger.info("Initializing WebDriver and opening the application...");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://onlinehtmleditor.dev");
        logger.info("Browser launched and navigated to https://onlinehtmleditor.dev");
    }

    @Test
    public void testRichTextEditor() throws InterruptedException {
        logger.info("Starting the rich text editor test...");
        try {
            // Locate editor body
            WebElement editorBody = driver.findElement(By.className("ck-editor__editable"));
            logger.debug("Located the rich text editor element.");

            // Perform actions
            logger.debug("Performing bold action and entering 'Automation'.");
            editorBody.sendKeys(Keys.chord(Keys.CONTROL, "b"), "Automation", Keys.CONTROL, "b");

            logger.debug("Inserting space and underlining 'Test'.");
            editorBody.sendKeys(Keys.SPACE, Keys.chord(Keys.CONTROL, "u"), "Test", Keys.CONTROL, "u");

            logger.debug("Entering 'Example'.");
            editorBody.sendKeys(Keys.SPACE, "Example");

            // Validate the inner HTML content
            String innerHTML = editorBody.getAttribute("innerHTML");
            logger.debug("Inner HTML content retrieved: {}", innerHTML);

            // Assertions
            Assert.assertTrue(innerHTML.contains("<strong>Automation</strong>"), "Bold 'Automation' was not found!");
            Assert.assertTrue(innerHTML.contains("<u>Test</u>"), "Underlined 'Test' was not found!");
            Assert.assertTrue(innerHTML.contains("Example"), "'Example' was not found!");

            logger.info("All assertions passed successfully.");
        } catch (Exception e) {
            logger.error("An error occurred during the test: ", e);
            throw e; // Re-throw the exception for test reporting
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing the browser and quitting WebDriver.");
            driver.quit();
        }
    }
}