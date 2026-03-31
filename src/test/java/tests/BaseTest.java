package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * BaseTest sets up and tears down a headless Chrome WebDriver
 * for every test. All test classes extend this.
 */
public class BaseTest {

    protected WebDriver driver;

    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final String USERNAME  = "standard_user";
    protected static final String PASSWORD  = "secret_sauce";

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // --- Headless flags required inside Docker (no display available) ---
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // Selenium 4 Manager automatically downloads the correct ChromeDriver
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}