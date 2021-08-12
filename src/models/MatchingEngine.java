package models;

import java.util.HashMap;
import java.util.PriorityQueue;

public class MatchingEngine {
    private HashMap<String, PriorityQueue<Order>> buyOrderBook;
    private HashMap<String, PriorityQueue<Order>> sellOrderBook;

    public String addAndMatch(Order order) {
        if(order.getSide().equals(BuySell.Buy)){
            //ToDo should try match first
            matchBuy(order);
            buyOrderBook.get(order.getSymbol()).add(order);
        }else{
            sellOrderBook.get(order.getSymbol()).add(order);
        }

        return null;
    }

    private void matchBuy(Order newBuyOrder) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newBuyOrder.getSymbol());

        //match
        if( sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()){
            Order matchedSellOrder = sellOrderBookPerSymbol.poll();

            newBuyOrder.fill(matchedSellOrder);
            matchedSellOrder.fill(newBuyOrder);

            sellOrderBookPerSymbol.add(matchedSellOrder);
        }

        buyOrderBook.get(newBuyOrder.getSymbol()).add(newBuyOrder);
    }
}
