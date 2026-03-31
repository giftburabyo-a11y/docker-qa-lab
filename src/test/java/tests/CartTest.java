package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeEach
    public void login() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(BASE_URL);
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.urlContains("/inventory.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".inventory_item button")));
    }

    @Test
    @DisplayName("Adding an item updates the cart badge count")
    public void testAddItemUpdatesCartBadge() {
        driver.findElement(By.cssSelector(".inventory_item button")).click();

        WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")));
        assertEquals("1", badge.getText(), "Cart badge should show 1 after adding one item");
    }

    @Test
    @DisplayName("Added item appears in the cart page")
    public void testItemAppearsInCart() {
        String productName = driver.findElement(
                By.cssSelector(".inventory_item_name")).getText();

        driver.findElement(By.cssSelector(".inventory_item button")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.urlContains("/cart.html"));
        assertTrue(driver.getCurrentUrl().contains("/cart.html"),
                "Should be on the cart page");

        WebElement cartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("inventory_item_name")));
        assertEquals(productName, cartItem.getText(),
                "Cart should contain the product that was added");
    }

    @Test
    @DisplayName("Remove item from cart clears the badge")
    public void testRemoveItemFromCart() {
        driver.findElement(By.cssSelector(".inventory_item button")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.urlContains("/cart.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".cart_item button"))).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.className("shopping_cart_badge")));

        assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear after removing the only item");
    }
}