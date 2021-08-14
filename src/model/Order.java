package model;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Order implements Comparable<Order>{
    private final OrderType orderType;
    private Status status;
    private final String orderId;
    private final String symbol;
    private final double price;
    private final BuySell side;
    private final int qty;
    List<Fill> fillList = new ArrayList<>();
    private final int seq;
    private int filledQty;
    private int unFilledQty;

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

    public OrderType getOrderType() {
        return orderType;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public FillExecutionReport fill(int fillQty, double fillPrice, int fillSeq) {
        this.status = Status.Fill;
        this.filledQty = filledQty + fillQty;
        this.unFilledQty = qty - filledQty;

        Fill fill = new Fill(fillPrice, fillQty, fillSeq);

        this.fillList.add(fill);

        //ToDo refactor to diff reports type
        return new FillExecutionReport(this, fill, this.toString()+","+fill.toString());
    }

    public Status getStatus() {
        return status;
    }

    public BuySell getSide() {
        return side;
    }

    @Override
    public String toString() {
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

    public int getUnFilledQty() {
        return unFilledQty;
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

    @Override
    public int compareTo(Order order) {
        return this.seq - order.getSeq();
    }
}
