package models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class MatchingEngine {
    private HashMap<String, PriorityQueue<Order>> buyOrderBook;
    private HashMap<String, PriorityQueue<Order>> sellOrderBook;

    public MatchingEngine() {
        buyOrderBook = new HashMap<>();
        sellOrderBook = new HashMap<>();
    }

    public String addAndMatch(List<Order> orderList) {
        String output = "";
        for (Order order : orderList) {
            if (order.getSide().equals(BuySell.Buy)) {
                //ToDo should try match first
                output = output + matchBuy(order);
            } else {

                output = output + matchSell(order);
            }

        }

        return output;
    }

    private String matchSell(Order newSellOrder) {
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newSellOrder.getSymbol());
        String output="";

        //match
        if (buyOrderBookPerSymbol != null) {
            if (buyOrderBookPerSymbol.peek().getPrice() >= newSellOrder.getPrice()) {
                Order matchedBuyOrder = buyOrderBookPerSymbol.poll();

                //fill
                output  = output + matchedBuyOrder.fill(newSellOrder);
                output  = output + newSellOrder.fill(matchedBuyOrder);

                tryAddToOrderBook(matchedBuyOrder, sellOrderBook);

//                buyOrderBookPerSymbol.add(filledBuyOrder);
            }
        }

        tryAddToOrderBook(newSellOrder, sellOrderBook);

        return output;
    }

    public static Comparator<Order> orderComparator = new Comparator<Order>() {

        @Override
        public int compare(Order c1, Order c2) {
            return (int) (c1.getPrice() - c2.getPrice());
        }
    };

    private String matchBuy(Order newBuyOrder) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newBuyOrder.getSymbol());
        String output="";

        //match with existing sell order book
        Order filledSellOrder = null;
        if (sellOrderBookPerSymbol != null) {
            if (sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()) {
                Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                //filled
                output = output+matchedSellOrder.fill(newBuyOrder);
                output = output+ newBuyOrder.fill(matchedSellOrder);

                //add filled ones back to order book
                tryAddToOrderBook(matchedSellOrder, buyOrderBook);

//                sellOrderBookPerSymbol.add(filledSellOrder);
            }
        }

        //add the new order to order book, either filled or not
        tryAddToOrderBook(newBuyOrder, buyOrderBook);

        return output;
    }

    private void tryAddToOrderBook(Order orderToAdd, HashMap<String, PriorityQueue<Order>> buyOrderBook) {
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(orderToAdd.getSymbol());
        if (buyOrderBookPerSymbol == null) {
            buyOrderBookPerSymbol = new PriorityQueue<>(orderComparator);
            buyOrderBookPerSymbol.add(orderToAdd);
            buyOrderBook.put(orderToAdd.getSymbol(), buyOrderBookPerSymbol);
        }
        buyOrderBookPerSymbol.add(orderToAdd);
    }
}
