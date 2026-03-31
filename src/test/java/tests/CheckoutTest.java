package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

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
        // Click Checkout
        driver.findElement(By.id("checkout")).click();
        assertTrue(driver.getCurrentUrl().contains("/checkout-step-one.html"),
                "Should be on checkout step one");

        // Fill in customer information
        driver.findElement(By.id("first-name")).sendKeys("Test");
        driver.findElement(By.id("last-name")).sendKeys("User");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        // Overview page
        assertTrue(driver.getCurrentUrl().contains("/checkout-step-two.html"),
                "Should be on the order overview page");

        // Finish the order
        driver.findElement(By.id("finish")).click();

        // Confirmation page
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
        driver.findElement(By.id("checkout")).click();

        // Leave first-name blank
        driver.findElement(By.id("last-name")).sendKeys("User");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        // Error message should appear
        String error = driver.findElement(
                By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("First Name is required"),
                "Expected validation error for missing first name, got: " + error);
    }
}