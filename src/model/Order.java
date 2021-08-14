package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static model.BuySell.Buy;
import static model.BuySell.Sell;

public class Order implements Comparable<Order> {
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
        this.filledQty = 0;
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
        return new FillExecutionReport(this, fill, this.toString() + "," + fill.toString());
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
                        "," + printPrice() +
                        "," + side +
                        "," + qty
                ;
    }

    private String printPrice() {
        if (orderType.equals(OrderType.LMT)) {
            return formatNum(price);
        }

        return orderType.toString();
    }


    private String formatNum(double price) {
        if (price == (long) price)
            return String.format("%d", (long) price);
        else
            return String.format("%s", price);
    }

    public int getSeq() {
        return seq;
    }

    public int getUnFilledQty() {
        return unFilledQty;
    }

    public boolean isFullyFilled() {
        return filledQty >= qty;
    }

    public static Comparator<Order> buyOrderComparator = getOrderComparator(Buy);

    public static Comparator<Order> sellOrderComparator = getOrderComparator(Sell);

    private static Comparator<Order> getOrderComparator(BuySell side) {
        return (o1, o2) -> {
            if (o1.getOrderType().compareTo(o2.getOrderType()) == 0) {
                if (side.equals(Buy)){
                    return (int) -(o1.getPrice() - o2.getPrice());
                }else{
                    return (int) (o1.getPrice() - o2.getPrice());
                }
            } else {
                return o1.getOrderType().compareTo(o2.getOrderType());
            }
        };
    }

    @Override
    public int compareTo(Order order) {
        return this.seq - order.getSeq();
    }
}
