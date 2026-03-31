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

public class CartTest extends BaseTest {

    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeEach
    public void login() {
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(BASE_URL);
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        js.executeScript("arguments[0].click();", driver.findElement(By.id("login-button")));
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".inventory_item button")));
    }

    @Test
    @DisplayName("Adding an item updates the cart badge count")
    public void testAddItemUpdatesCartBadge() {
        js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(".inventory_item button")));
        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")));
        assertEquals("1", badge.getText(), "Cart badge should show 1 after adding one item");
    }

    @Test
    @DisplayName("Added item appears in the cart page")
    public void testItemAppearsInCart() {
        String productName = driver.findElement(
                By.cssSelector(".inventory_item_name")).getText();

        js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(".inventory_item button")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")));

        js.executeScript("arguments[0].click();",
                driver.findElement(By.className("shopping_cart_link")));
        wait.until(ExpectedConditions.urlContains("/cart.html"));

        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("inventory_item_name")));
        assertEquals(productName, cartItem.getText(),
                "Cart should contain the product that was added");
    }

    @Test
    @DisplayName("Remove item from cart clears the badge")
    public void testRemoveItemFromCart() {
        js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(".inventory_item button")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")));

        js.executeScript("arguments[0].click();",
                driver.findElement(By.className("shopping_cart_link")));
        wait.until(ExpectedConditions.urlContains("/cart.html"));

        js.executeScript("arguments[0].click();",
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".cart_item button"))));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.className("shopping_cart_badge")));
        assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear after removing the only item");
    }
}