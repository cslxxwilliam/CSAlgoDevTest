package model;


import java.util.ArrayList;
import java.util.Comparator;
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

    public Order(int seq, Status status, String orderId, String symbol, OrderType orderType, double price, BuySell side, int qty) {
        this.seq = seq;
        this.status = status;
        this.orderId = orderId;
        this.side = side;
        this.symbol = symbol;
        this.price = price;
        this.qty = qty;
        this.orderType = orderType;
        this.filledQty =0;
        this.unFilledQty = qty;
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

    public ExecutionReport fill(int fillQty, double fillPrice, int fillSeq) {
        this.status = Status.Fill;
        this.filledQty = filledQty + fillQty;
        this.unFilledQty = qty - filledQty;

        Fill fill = new Fill(fillPrice, fillQty, fillSeq);

        this.fillList.add(fill);

        //ToDo refactor to diff reports type
        return new ExecutionReport(ReportType.Fill, this, fill, this.toString()+","+fill.toString()+"\n");
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

    public boolean isFullyFilled() {
        return filledQty>=qty;
    }

    public static Comparator<Order> buyOrderComparator = (c1, c2) -> {
        //price larger, the less
        if (c1.getOrderType().compareTo(c2.getOrderType()) == 0) {
            return (int) -(c1.getPrice() - c2.getPrice());
        }
        //market order less than limit
        else {
            return c1.getOrderType().compareTo(c2.getOrderType());
        }
    };
    public static Comparator<Order> sellOrderComparator = (c1, c2) -> {
        //price lower, the less
        if (c1.getOrderType().compareTo(c2.getOrderType()) == 0) {
            return (int) (c1.getPrice() - c2.getPrice());
        }
        //market order less than limit
        else {
            return c1.getOrderType().compareTo(c2.getOrderType());
        }
    };
}
