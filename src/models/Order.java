package models;

public class Order {
    private boolean isBuy;
    private String symbol;
    private double price;
    private int qty;

    public boolean isBuy() {
        return isBuy;
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
}
