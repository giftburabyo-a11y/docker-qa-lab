package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CartTest covers adding items to the cart on Swag Labs.
 * Tests: add one item, cart badge updates, item appears in cart.
 */
public class CartTest extends BaseTest {

    // Log in before each cart test so we start on the inventory page
    @BeforeEach
    public void login() {
        driver.get(BASE_URL);
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();
    }

    @Test
    @DisplayName("Adding an item updates the cart badge count")
    public void testAddItemUpdatesCartBadge() {
        // Click "Add to cart" for the first product
        driver.findElement(By.cssSelector(".inventory_item button")).click();

        // Cart badge should now show "1"
        WebElement badge = driver.findElement(By.className("shopping_cart_badge"));
        assertEquals("1", badge.getText(), "Cart badge should show 1 after adding one item");
    }

    @Test
    @DisplayName("Added item appears in the cart page")
    public void testItemAppearsInCart() {
        // Get the name of the first product on the inventory page
        String productName = driver.findElement(
                By.cssSelector(".inventory_item_name")).getText();

        // Add it to cart
        driver.findElement(By.cssSelector(".inventory_item button")).click();

        // Navigate to cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Cart page URL check
        assertTrue(driver.getCurrentUrl().contains("/cart.html"),
                "Should be on the cart page");

        // The same product name should appear in the cart
        WebElement cartItem = driver.findElement(By.className("inventory_item_name"));
        assertEquals(productName, cartItem.getText(),
                "Cart should contain the product that was added");
    }

    @Test
    @DisplayName("Remove item from cart clears the badge")
    public void testRemoveItemFromCart() {
        // Add item first
        driver.findElement(By.cssSelector(".inventory_item button")).click();

        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();

        // Remove the item
        driver.findElement(By.cssSelector(".cart_item button")).click();

        // Badge should be gone (no element present)
        assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear after removing the only item");
    }
}