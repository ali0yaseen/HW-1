package main;

public class ProductStock {

    private final String productId;
    private String location;
    private int onHand;
    private int reserved;
    private int reorderThreshold;
    private int maxCapacity;

    public ProductStock(String productId,
                        String location,
                        int initialOnHand,
                        int reorderThreshold,
                        int maxCapacity) {

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("location must not be null or blank");
        }
        if (initialOnHand < 0) {
            throw new IllegalArgumentException("initialOnHand must be >= 0");
        }
        if (reorderThreshold < 0) {
            throw new IllegalArgumentException("reorderThreshold must be >= 0");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("maxCapacity must be > 0");
        }
        if (initialOnHand > maxCapacity) {
            throw new IllegalArgumentException("initialOnHand exceeds maxCapacity");
        }

        this.productId = productId;
        this.location = location;
        this.onHand = initialOnHand;
        this.reserved = 0;
        this.reorderThreshold = reorderThreshold;
        this.maxCapacity = maxCapacity;
    }

    public String getProductId() { return productId; }
    public String getLocation() { return location; }
    public int getOnHand() { return onHand; }
    public int getReserved() { return reserved; }
    public int getAvailable() { return onHand - reserved; }
    public int getReorderThreshold() { return reorderThreshold; }
    public int getMaxCapacity() { return maxCapacity; }

    public void changeLocation(String newLocation) {
        if (newLocation == null || newLocation.isBlank()) {
            throw new IllegalArgumentException("newLocation must not be null or blank");
        }
        this.location = newLocation;
    }

    public void addStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to add must be positive");
        if (onHand + amount > maxCapacity) throw new IllegalStateException("Cannot add stock beyond maxCapacity");
        onHand += amount;
    }

    public void removeDamaged(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to remove must be positive");
        if (amount > onHand) throw new IllegalStateException("Cannot remove more than on-hand quantity");
        onHand -= amount;
        if (reserved > onHand) reserved = onHand;
    }

    public void reserve(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to reserve must be positive");
        if (amount > getAvailable()) throw new IllegalStateException("Insufficient available stock to reserve");
        reserved += amount;
    }

    public void releaseReservation(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to release must be positive");
        if (amount > reserved) throw new IllegalStateException("Cannot release more than reserved");
        reserved -= amount;
    }

    public void shipReserved(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to ship must be positive");
        if (amount > reserved) throw new IllegalStateException("Cannot ship more than reserved");
        if (amount > onHand) throw new IllegalStateException("On-hand quantity is not enough to ship");
        reserved -= amount;
        onHand -= amount;
    }

    public boolean isReorderNeeded() {
        return getAvailable() < reorderThreshold;
    }

    public void updateReorderThreshold(int newThreshold) {
        if (newThreshold < 0) throw new IllegalArgumentException("reorderThreshold must be >= 0");
        if (newThreshold > maxCapacity) throw new IllegalArgumentException("reorderThreshold cannot exceed maxCapacity");
        this.reorderThreshold = newThreshold;
    }

    public void updateMaxCapacity(int newMaxCapacity) {
        if (newMaxCapacity <= 0) throw new IllegalArgumentException("maxCapacity must be > 0");
        if (newMaxCapacity < onHand) throw new IllegalStateException("New maxCapacity is less than current onHand");
        this.maxCapacity = newMaxCapacity;
        if (reorderThreshold > maxCapacity) reorderThreshold = maxCapacity;
    }

    @Override
    public String toString() {
        return "ProductStock{" +
               "productId='" + productId + '\'' +
               ", location='" + location + '\'' +
               ", onHand=" + onHand +
               ", reserved=" + reserved +
               ", available=" + getAvailable() +
               ", reorderThreshold=" + reorderThreshold +
               ", maxCapacity=" + maxCapacity +
               '}';
    }
}
