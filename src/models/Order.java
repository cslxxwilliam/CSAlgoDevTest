package models;


public class Order {
    private Status status;
    private String orderId;
    private String symbol;
    private double price;
    private BuySell side;
    private int qty;

//                "Order1,0700.HK,610,Sell,20000"

    public Order(Status status, String orderId, String symbol, double price, BuySell side, int qty) {
        this.status = status;
        this.orderId = orderId;
        this.side = side;
        this.symbol = symbol;
        this.price = price;
        this.qty = qty;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void fill(Order matchedSellOrder) {
        //print sout
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BuySell getSide() {
        return side;
    }

    public void setSide(BuySell side) {
        this.side = side;
    }

    @Override
    public String toString() {
        return
                status +
                "," + orderId  +
                "," + symbol +
                "," + price +
                "," + side +
                "," + qty
                ;
    }
}
