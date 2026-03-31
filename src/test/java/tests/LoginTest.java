package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest covers the Swag Labs (saucedemo.com) login flow.
 * Tests: successful login, failed login with wrong password.
 */
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Successful login with valid credentials")
    public void testValidLogin() {
        driver.get(BASE_URL);

        // Fill in credentials
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();

        // After login, the URL should contain /inventory.html
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/inventory.html"),
                "Expected to land on inventory page but got: " + currentUrl);

        // The product list title should be visible
        WebElement title = driver.findElement(By.className("title"));
        assertEquals("Products", title.getText(), "Page title should be 'Products'");
    }

    @Test
    @DisplayName("Failed login with wrong password shows error message")
    public void testInvalidLogin() {
        driver.get(BASE_URL);

        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        // Error message container should appear
        WebElement error = driver.findElement(
                By.cssSelector("[data-test='error']"));
        assertTrue(error.isDisplayed(), "Error message should be visible");
        assertTrue(error.getText().contains("Username and password do not match"),
                "Error text mismatch: " + error.getText());
    }
}