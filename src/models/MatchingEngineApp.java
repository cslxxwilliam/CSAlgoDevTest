package models;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngineApp {
    public MatchingEngine engine;

    public MatchingEngineApp() {
        engine = new MatchingEngine();

    }

    public String addInput(String input) {
        String[] split = input.split("\n");
        String output = "";
        List<Order> orderList = new ArrayList<>();
        if (!split[0].equals("#OrderID,Symbol,Price,Side,OrderQuantity")) {
            return "Invalid headers";
        } else {
            output = output + "#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n";
        }


        //validate orders
        for (int i = 1; i < split.length; i++) {
            String[] orderDetails = split[i].split(",");

            //ToDo convert to a factory with validation
            Order order = null;
            Status status = Status.Ack;
            String orderId = orderDetails[0];
            String symbol = orderDetails[1];
            BuySell side = BuySell.valueOf(orderDetails[3]);
            int qty = Integer.parseInt(orderDetails[4]);
            if (qty >= 10000000) {
                status = Status.Reject;
                order = buildOrder(orderDetails, status, orderId, symbol, side, qty);
            } else {
                order = buildOrder(orderDetails, status, orderId, symbol, side, qty);
            }
            orderList.add(order);
            output = output + order.toString()+"\n";
        }

        //add to orderbook
        output = output + engine.addAndMatch(orderList);

        return output;
    }

    private Order buildOrder(String[] orderDetails, Status status, String orderId, String symbol, BuySell side, int qty) {
        Order order;
        if (orderDetails[2].equals("MKT")) {
            order = new Order(status, orderId, symbol, OrderType.MKT, side, qty);

        } else {
            order = new Order(status, orderId, symbol, Double.parseDouble(orderDetails[2]), side, qty);
        }
        return order;
    }
}
