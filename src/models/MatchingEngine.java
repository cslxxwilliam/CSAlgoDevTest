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

        //match
        if (buyOrderBookPerSymbol != null) {
            if (buyOrderBookPerSymbol.peek().getPrice() >= newSellOrder.getPrice()) {
                Order matchedBuyOrder = buyOrderBookPerSymbol.poll();

//
//                //filled
//                Order filledBuyOrder = newBuyOrder.fill(matchedSellOrder);
//                Order filledSellOrder = matchedSellOrder.fill(newBuyOrder);
//
//                //add filled ones back to order book
//                tryAddToOrderBook(filledBuyOrder, buyOrderBook);
//
//                sellOrderBookPerSymbol.add(filledSellOrder);
//
                Order filledSellOrder=newSellOrder.fill(matchedBuyOrder);
                Order filledBuyOrder = matchedBuyOrder.fill(newSellOrder);

                tryAddToOrderBook(filledSellOrder, sellOrderBook);

                buyOrderBookPerSymbol.add(matchedBuyOrder);
            }
        }

        tryAddToOrderBook(newSellOrder, sellOrderBook);

        return null;
    }

    public static Comparator<Order> orderComparator = new Comparator<Order>() {

        @Override
        public int compare(Order c1, Order c2) {
            return (int) (c1.getPrice() - c2.getPrice());
        }
    };

    private String matchBuy(Order newBuyOrder) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newBuyOrder.getSymbol());

        //match with existing sell order book
        if (sellOrderBookPerSymbol != null) {
            if (sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()) {
                Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                //filled
                Order filledBuyOrder = newBuyOrder.fill(matchedSellOrder);
                Order filledSellOrder = matchedSellOrder.fill(newBuyOrder);

                //add filled ones back to order book
                tryAddToOrderBook(filledBuyOrder, buyOrderBook);

                sellOrderBookPerSymbol.add(filledSellOrder);
            }
        }

        //add the new order to order book, either filled or not
        tryAddToOrderBook(newBuyOrder, buyOrderBook);
        return null;
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
