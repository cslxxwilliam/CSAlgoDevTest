package models;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngineApp {
    public MatchingEngine engine;

    public MatchingEngineApp(){
        engine = new MatchingEngine();

    }
    public String addInput(String input) {
        String[] split = input.split("\n");
        String output ="";
        List<Order> orderList = new ArrayList<>();
        if(!split[0].equals("#OrderID,Symbol,Price,Side,OrderQuantity")){
            return "Invalid headers";
        }else{
             output = output + "#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n";
        }

        //validate orders
        for(int i =1;i<split.length; i++){
            String[] orderDetails = split[i].split(",");
            Order order = new Order(Status.Ack, orderDetails[0], orderDetails[1], Double.parseDouble(orderDetails[2]), BuySell.valueOf(orderDetails[3]), Integer.parseInt(orderDetails[4]));

            orderList.add(order);
            output = output + order.toString();
        }

        //add to orderbook
        output= output + engine.addAndMatch(orderList);

        return output;
    }
}
