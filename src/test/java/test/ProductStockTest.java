package test;

import main.ProductStock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductStockTest {

    private ProductStock stock;

    @BeforeEach
    void setup() {
        stock = new ProductStock("P100", "WH-1-A3", 20, 10, 50);
    }

    @AfterEach
    void teardown() {
        stock = null;
    }

    // ---------------- CONSTRUCTOR TESTS ----------------
    @Test
    @DisplayName("Valid constructor creates object correctly")
    void testValidConstructor() {
        assertAll(
                () -> assertEquals("P100", stock.getProductId()),
                () -> assertEquals("WH-1-A3", stock.getLocation()),
                () -> assertEquals(20, stock.getOnHand()),
                () -> assertEquals(10, stock.getReorderThreshold()),
                () -> assertEquals(50, stock.getMaxCapacity())
        );
    }

    @Test
    @DisplayName("Invalid constructor parameters should throw exceptions")
    void testInvalidConstructor() {
        assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("", "L", 10, 5, 50)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("X", null, 10, 5, 50)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("X", "L", -5, 5, 50)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("X", "L", 10, -3, 50)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new ProductStock("X", "L", 60, 5, 50)
        );
    }

    // ---------------- CHANGE LOCATION TEST ----------------
    @Test
    void testChangeLocation() {
        stock.changeLocation("WH-7-B3");
        assertEquals("WH-7-B3", stock.getLocation());
    }

    @Test
    void testChangeLocationInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.changeLocation(""));
    }

    // ---------------- ADD STOCK TESTS ----------------
    @Test
    void testAddStockNormal() {
        stock.addStock(10);
        assertEquals(30, stock.getOnHand());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void testAddStockParameterized(int amount) {
        stock.addStock(amount);
        assertEquals(20 + amount, stock.getOnHand());
    }

    @Test
    void testAddStockInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.addStock(0));
        assertThrows(IllegalArgumentException.class, () -> stock.addStock(-5));
    }

    @Test
    void testAddStockBeyondCapacity() {
        assertThrows(IllegalStateException.class, () -> stock.addStock(1000));
    }

    // ---------------- REMOVE DAMAGED TESTS ----------------
    @Test
    void testRemoveDamagedNormal() {
        stock.removeDamaged(5);
        assertEquals(15, stock.getOnHand());
    }

    @Test
    void testRemoveDamagedInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.removeDamaged(0));
        assertThrows(IllegalArgumentException.class, () -> stock.removeDamaged(-3));
    }

    @Test
    void testRemoveDamagedTooMuch() {
        assertThrows(IllegalStateException.class, () -> stock.removeDamaged(500));
    }

    // ---------------- RESERVATION TESTS ----------------
    @Test
    void testReserveNormal() {
        stock.reserve(5);
        assertEquals(5, stock.getReserved());
        assertEquals(15, stock.getAvailable());
    }

    @Test
    void testReserveTooMuch() {
        assertThrows(IllegalStateException.class, () -> stock.reserve(999));
    }

    @Test
    void testReleaseReservationNormal() {
        stock.reserve(10);
        stock.releaseReservation(5);
        assertEquals(5, stock.getReserved());
    }

    @Test
    void testReleaseReservationInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.releaseReservation(0));
        assertThrows(IllegalStateException.class, () -> stock.releaseReservation(50));
    }

    // ---------------- SHIP RESERVED TESTS ----------------
    @Test
    void testShipReservedNormal() {
        stock.reserve(10);
        stock.shipReserved(10);
        assertEquals(10, stock.getOnHand());
        assertEquals(0, stock.getReserved());
    }

    @Test
    void testShipReservedInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.shipReserved(0));
        assertThrows(IllegalStateException.class, () -> stock.shipReserved(5));
    }

    // ---------------- REORDER / CAPACITY TESTS ----------------
    @Test
    void testReorderNeeded() {
        stock.reserve(15); // available = 5
        assertTrue(stock.isReorderNeeded());
    }

    @Test
    void testUpdateThreshold() {
        stock.updateReorderThreshold(20);
        assertEquals(20, stock.getReorderThreshold());
    }

    @Test
    void testUpdateThresholdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(-1));
        assertThrows(IllegalArgumentException.class, () -> stock.updateReorderThreshold(999));
    }

    @Test
    void testUpdateMaxCapacity() {
        stock.updateMaxCapacity(100);
        assertEquals(100, stock.getMaxCapacity());
    }

    @Test
    void testUpdateMaxCapacityInvalid() {
        assertThrows(IllegalArgumentException.class, () -> stock.updateMaxCapacity(0));
        assertThrows(IllegalStateException.class, () -> stock.updateMaxCapacity(5));
    }

    // ---------------- DISABLED TEST FOR FUTURE FEATURE ----------------
    @Disabled("Future implementation coming soon")
    @Test
    void futureFeatureTest() {}
}
