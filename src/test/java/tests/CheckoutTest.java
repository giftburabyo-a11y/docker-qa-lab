package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutTest extends BaseTest {

    private JavascriptExecutor js;

    private void fillField(String id, String value) {
        WebElement field = driver.findElement(By.id(id));
        js.executeScript(
                "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "setter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                field, value
        );
    }

    @BeforeEach
    public void loginAndAddItem() {
        js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(BASE_URL);
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        js.executeScript("arguments[0].click();", driver.findElement(By.id("login-button")));

        wait.until(ExpectedConditions.urlContains("/inventory.html"));
        js.executeScript("arguments[0].click();",
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".inventory_item button"))));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")));
        js.executeScript("arguments[0].click();",
                driver.findElement(By.className("shopping_cart_link")));
        wait.until(ExpectedConditions.urlContains("/cart.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkout")));
    }

    @Test
    @DisplayName("Complete checkout flow ends on order confirmation page")
    public void testFullCheckoutFlow() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        js.executeScript("arguments[0].click();", driver.findElement(By.id("checkout")));
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));

        fillField("first-name", "Test");
        fillField("last-name", "User");
        fillField("postal-code", "10001");
        Thread.sleep(500);

        js.executeScript("arguments[0].click();", driver.findElement(By.id("continue")));
        wait.until(ExpectedConditions.urlContains("/checkout-step-two.html"));

        js.executeScript("arguments[0].click();",
                wait.until(ExpectedConditions.elementToBeClickable(By.id("finish"))));
        wait.until(ExpectedConditions.urlContains("/checkout-complete.html"));

        String confirmation = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("complete-header"))
        ).getText();
        assertEquals("Thank you for your order!", confirmation,
                "Order confirmation message mismatch");
    }

    @Test
    @DisplayName("Checkout with empty first name shows validation error")
    public void testCheckoutMissingFirstName() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        js.executeScript("arguments[0].click();", driver.findElement(By.id("checkout")));
        wait.until(ExpectedConditions.urlContains("/checkout-step-one.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("last-name")));

        fillField("last-name", "User");
        fillField("postal-code", "10001");
        Thread.sleep(500);

        js.executeScript("arguments[0].click();", driver.findElement(By.id("continue")));

        String error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']"))
        ).getText();
        assertTrue(error.contains("First Name is required"),
                "Expected validation error for missing first name, got: " + error);
    }
}