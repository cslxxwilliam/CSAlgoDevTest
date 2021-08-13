package models;


import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderType orderType;
    private Status status;
    private String orderId;
    private String symbol;
    private double price;
    private BuySell side;
    private int qty;

    List<Fill> fillList = new ArrayList<>();
    private int seq;
    private int filledQty;
    private int unFilledQty;

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Order(Status status, String orderId, String symbol, double price, BuySell side, int qty) {
        this.status = status;
        this.orderId = orderId;
        this.side = side;
        this.symbol = symbol;
        this.price = price;
        this.qty = qty;
        this.orderType = OrderType.LMT;
    }

    public Order(Status status, String orderId, String symbol, double price, BuySell side, int qty, int filledQty) {

    }

    public Order(Status status, String orderId, String symbol, OrderType orderType, BuySell side, int qty) {
        this.status = status;
        this.orderId = orderId;
        this.symbol = symbol;
        this.orderType = orderType;
        this.side = side;
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

    public String fill(Order matchedOrder) {
            int newMatchedQty;

        if(this.qty<=matchedOrder.qty){
            newMatchedQty = this.qty;
        }else {
            newMatchedQty = matchedOrder.qty;
        }

        this.status = Status.Fill;
        this.filledQty = filledQty + newMatchedQty;
        this.unFilledQty = qty - filledQty;

        double filledPrice = matchedOrder.getOrderType().equals(OrderType.MKT) ? this.price : matchedOrder.getPrice();
        Fill fill = new Fill(filledPrice, newMatchedQty);

        this.fillList.add(fill);
        return this.toString()+fill.toString()+"\n";
    }

    public String fill(Order toFill, double fillPrice) {
        int newMatchedQty;

        if((this.qty-filledQty)<=toFill.getUnFilledQty()){
            newMatchedQty = this.qty;
        }else {
            newMatchedQty = toFill.qty;
        }

        this.status = Status.Fill;
        this.filledQty = filledQty + newMatchedQty;
        this.unFilledQty = qty - filledQty;

        Fill fill = new Fill(fillPrice, newMatchedQty);

        this.fillList.add(fill);
        return this.toString()+","+fill.toString()+"\n";
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
        //ToDo better model the print message
        if(status==Status.Ack||status==Status.Reject) {
            return
                    status +
                            "," + orderId +
                            "," + symbol +
                            "," + printPrice()
                             +
                            "," + side +
                            "," + qty
                    ;
        }

        return
                status +
                        "," + orderId +
                        "," + symbol +
                        "," + printPrice()+
                        "," + side +
                        "," + qty
                ;
    }

    private String printPrice() {
        if(orderType.equals(OrderType.LMT)){
            return formatNum(price);
        }

        return orderType.toString();
    }


    private String formatNum(double price) {
        if(price == (long) price)
            return String.format("%d",(long)price);
        else
            return String.format("%s",price);
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getUnFilledQty() {
        return unFilledQty;
    }

    public void setUnFilledQty(int unFilledQty) {
        this.unFilledQty = unFilledQty;
    }
}
