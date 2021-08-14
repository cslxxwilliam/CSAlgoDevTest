package controller;

import model.BuySell;
import model.Order;
import model.OrderType;
import model.Status;

import static model.OrderType.LMT;

public class OrderFactory {
    public static final int QTY_LIMIT = 10000000;
    public static final String MKT = "MKT";
    public static int orderSeq = 0;

    public static Order create(String orderId, String symbol, String rawPrice, String rawSide, String rawQty) {
        Order order;
        Status status = Status.Ack;
        BuySell side = BuySell.valueOf(rawSide);
        int qty = Integer.parseInt(rawQty);
        OrderType orderType;
        double price=0;

        if (qty >= QTY_LIMIT) {
            status = Status.Reject;
        }

        if (rawPrice.equals(MKT)) {
            orderType = OrderType.MKT;
        }else{
            orderType = LMT;
            price=Double.parseDouble(rawPrice);
        }

        order = new Order(orderSeq++, status, orderId, symbol, orderType, price, side, qty);

        return order;
    }

}

