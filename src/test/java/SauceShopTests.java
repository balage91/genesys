import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class SauceShopTests {

    private WebDriver driver; // WebDriver instance
    private static final String SAUCE_URL = "https://www.saucedemo.com/inventory.html"; // Target login page
    private static final String LOGIN_DATA_FILE = "src/test/resources/credential.json"; // Path to JSON file
    private static final Logger log = LoggerFactory.getLogger(SauceShopTests.class);
    private static int numberOfItemsInCart = 0;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        log.info("Test setup complete: WebDriver initialized.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.manage().deleteAllCookies();
            driver.quit();
            log.info("Test teardown complete: WebDriver closed.");
        }
    }

    @Test
    public void testAutomatedPurchase() throws IOException, InterruptedException {
        log.info("Starting test...");
        driver.get(SAUCE_URL);
        Map<String, String> credentials = loadCredentials(LOGIN_DATA_FILE);

        performLogin(credentials.get("username"), credentials.get("password"));

        addToCart("add-to-cart-sauce-labs-backpack");
        addToCart("add-to-cart-sauce-labs-fleece-jacket");
        checkoutCart();
    }

    @Test
    public void testErrorMessages() throws IOException, InterruptedException {
        driver.get(SAUCE_URL);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.name("login-button")).click();
        WebElement errorMessageContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message-container")));
        String actualErrorMessage = errorMessageContainer.getText();
        String expectedErrorMessage = "Epic sadface: Username is required";
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message validation failed!");

        performLogin("standard_user", "secret_sauce");

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement footerElement = driver.findElement(By.cssSelector("footer"));

        String footerText = footerElement.getText();
        Assert.assertTrue(footerText.contains("2025"), "Footer does not contain '2025'");
        Assert.assertTrue(footerText.contains("Terms of Service"), "Footer does not contain 'Terms of Service'");

    }

    private Map<String, String> loadCredentials(String filePath) throws IOException {
        log.info("Loading login data from: {}", filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), Map.class);
    }

    private void performLogin(String username, String password) {
        log.info("Performing login...");
        WebElement usernameField = driver.findElement(By.name("user-name"));
        WebElement passwordField = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.name("login-button"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        log.info("Login form submitted.");

        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean loginSuccess = wait.until(ExpectedConditions.stalenessOf(loginButton));

        Assert.assertTrue(loginSuccess, "Login validation failed");
        log.info("Login successful.");
    }

    private void addToCart(String addItemButtonId) {
        log.info("Adding item to cart...");
        WebElement addItemButton = driver.findElement(By.id(addItemButtonId));
        addItemButton.click();
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Assert.assertTrue(wait.until(driver ->
                Integer.parseInt(driver.findElements(By.className("shopping_cart_badge")).getFirst().getText()) == numberOfItemsInCart + 1
        ));
        numberOfItemsInCart++;
        log.info("Item added to cart.");
    }

    private void checkoutCart() {
        log.info("Checking out cart...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.id("shopping_cart_container")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();
        WebElement firstNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("first-name")));
        firstNameInput.sendKeys("Bob");
        WebElement lastNameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("last-name")));
        lastNameInput.sendKeys("Rob");
        WebElement postalCodeInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("postal-code")));
        postalCodeInput.sendKeys("12345");
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("continue")));
        continueButton.click();
        WebElement finishButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
        finishButton.click();

        Assert.assertTrue(driver.getPageSource().contains("Thank you for your order!"),
                "Thank you message is not found on the page!");
        log.info("Cart checked out.");
    }
}
