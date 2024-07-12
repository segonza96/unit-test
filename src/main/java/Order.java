
public class Order {

    private final int id;
    private final double amount;
    public Order(int id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public double getAmount() {
        return this.amount;
    }
    public int getId() {
        return this.id;
    }
}
