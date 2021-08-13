package models;

public class Fill {
    private double price;
    private int qty;

    public Fill(double filledPrice, int newMatchedQty) {

        this.price = filledPrice;
        this.qty = newMatchedQty;
    }

    @Override
    public String toString() {
        return
               formatNum(price) +
                "," + qty;
    }

    private String formatNum(double price) {
        if(price == (long) price)
            return String.format("%d",(long)price);
        else
            return String.format("%s",price);
    }
}
