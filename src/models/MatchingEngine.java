package models;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class MatchingEngine {
    private HashMap<String, PriorityQueue<Order>> buyOrderBook;
    private HashMap<String, PriorityQueue<Order>> sellOrderBook;

    public MatchingEngine(){
        buyOrderBook = new HashMap<>();
        sellOrderBook = new HashMap<>();
    }
    public String addAndMatch(List<Order> orderList) {
        String output ="";
        for (Order order : orderList) {
            if(order.getSide().equals(BuySell.Buy)){
                //ToDo should try match first
                output = output + matchBuy(order);
            }else{

                output = output + matchSell(order);
            }

        }

        return output;
    }

    private String matchSell(Order newSellOrder) {
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newSellOrder.getSymbol());

        //match
        if(buyOrderBookPerSymbol!=null) {
            if (buyOrderBookPerSymbol.peek().getPrice() >= newSellOrder.getPrice()) {
                Order matchedBuyOrder = buyOrderBookPerSymbol.poll();

                newSellOrder.fill(matchedBuyOrder);
                matchedBuyOrder.fill(newSellOrder);

                buyOrderBookPerSymbol.add(matchedBuyOrder);
            }
        }

        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newSellOrder.getSymbol());
        if(sellOrderBookPerSymbol==null)
        {
            sellOrderBookPerSymbol = new PriorityQueue<>(orderComparator);
            sellOrderBookPerSymbol.add(newSellOrder);
            sellOrderBook.put(newSellOrder.getSymbol(), sellOrderBookPerSymbol);
        }

        sellOrderBookPerSymbol.add(newSellOrder);

        return null;
    }


    private String matchBuy(Order newBuyOrder) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newBuyOrder.getSymbol());

        //match
        if(sellOrderBookPerSymbol!=null) {
            if (sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()) {
                Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                newBuyOrder.fill(matchedSellOrder);
                matchedSellOrder.fill(newBuyOrder);

                sellOrderBookPerSymbol.add(matchedSellOrder);
            }
        }

        //add the new order to order book
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newBuyOrder.getSymbol());
        if(buyOrderBookPerSymbol==null)
        {
            buyOrderBookPerSymbol = new PriorityQueue<>();
            buyOrderBookPerSymbol.add(newBuyOrder);
            buyOrderBook.put(newBuyOrder.getSymbol(), buyOrderBookPerSymbol);
        }

        buyOrderBookPerSymbol.add(newBuyOrder);

        return null;
    }
}
