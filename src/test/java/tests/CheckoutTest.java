package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CheckoutTest covers the full checkout flow on Swag Labs:
 * add item → cart → checkout info → overview → order complete.
 */
public class CheckoutTest extends BaseTest {

    @BeforeEach
    public void loginAndAddItem() {
        // 1. Login
        driver.get(BASE_URL);
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();

        // 2. Add first item to cart
        driver.findElement(By.cssSelector(".inventory_item button")).click();

        // 3. Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();
    }

    @Test
    @DisplayName("Complete checkout flow ends on order confirmation page")
    public void testFullCheckoutFlow() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click Checkout and wait for navigation
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        assertTrue(driver.getCurrentUrl().contains("/checkout-step-one.html"),
                "Should be on checkout step one");

        // Fill in customer information
        driver.findElement(By.id("first-name")).sendKeys("Test");
        driver.findElement(By.id("last-name")).sendKeys("User");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        // Wait for overview page
        wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));
        assertTrue(driver.getCurrentUrl().contains("/checkout-step-two.html"),
                "Should be on the order overview page");

        // Finish the order
        driver.findElement(By.id("finish")).click();

        // Wait for confirmation page
        wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));
        assertTrue(driver.getCurrentUrl().contains("/checkout-complete.html"),
                "Should land on the order complete page");

        String confirmation = driver.findElement(
                By.className("complete-header")).getText();
        assertEquals("Thank you for your order!", confirmation,
                "Order confirmation message mismatch");
    }

    @Test
    @DisplayName("Checkout with empty first name shows validation error")
    public void testCheckoutMissingFirstName() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click checkout and wait for the form to load
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));

        // Leave first-name blank, fill the rest
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("last-name")));
        driver.findElement(By.id("last-name")).sendKeys("User");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        // Wait for error message and assert
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='error']")));
        String error = driver.findElement(
                By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("First Name is required"),
                "Expected validation error for missing first name, got: " + error);
    }
}