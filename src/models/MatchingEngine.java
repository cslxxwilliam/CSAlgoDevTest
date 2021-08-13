package models;

import java.util.*;

public class MatchingEngine {
    private HashMap<String, PriorityQueue<Order>> buyOrderBook;
    private HashMap<String, PriorityQueue<Order>> sellOrderBook;
    private List<Order> rejectedOrders;

    public MatchingEngine() {
        buyOrderBook = new HashMap<>();
        sellOrderBook = new HashMap<>();
        rejectedOrders = new ArrayList<>();
    }

    public String addAndMatch(List<Order> orderList) {
        String output = "";
        for (Order order : orderList) {
            if (order.getStatus().equals(Status.Reject)) {
                rejectedOrders.add(order);
            } else {
                if (order.getSide().equals(BuySell.Buy)) {
                    //ToDo should try match first
                    output = output + matchBuy(order);
                } else {

                    output = output + matchSell(order);
                }
            }
        }

        return output;
    }

    private String matchSell(Order newSellOrder) {
        tryAddToOrderBook(newSellOrder, sellOrderBook);

        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newSellOrder.getSymbol());
        String output = "";

        //match
        if (buyOrderBookPerSymbol != null) {
            if (buyOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT) || buyOrderBookPerSymbol.peek().getPrice() >= newSellOrder.getPrice()) {
                Order matchedBuyOrder = buyOrderBookPerSymbol.poll();

                //fill
                output = output + matchedBuyOrder.fill(newSellOrder);
                output = output + newSellOrder.fill(matchedBuyOrder);

                tryAddToOrderBook(matchedBuyOrder, sellOrderBook);

//                buyOrderBookPerSymbol.add(filledBuyOrder);
            }
        }

        return output;
    }

    public static Comparator<Order> orderComparator = (c1, c2) -> {
        if (c1.getOrderType().compareTo(c2.getOrderType()) == 0) {
            return (int) (c1.getPrice() - c2.getPrice());
        } else {
            return c1.getOrderType().compareTo(c2.getOrderType());
        }
    };

    //refactor buy and sell
    private String matchBuy(Order newBuyOrder) {
        //add the new order to order book, either filled or not
        tryAddToOrderBook(newBuyOrder, buyOrderBook);

        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newBuyOrder.getSymbol());
        String output = "";

        //match with existing sell order book
        Order filledSellOrder = null;
        if (sellOrderBookPerSymbol != null) {
            if (!newBuyOrder.getOrderType().equals(OrderType.MKT)) {
                if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT) || sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()) {
                    Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                    //filled
                    output = output + matchedSellOrder.fill(newBuyOrder);
                    output = output + newBuyOrder.fill(matchedSellOrder);

                    //add filled ones back to order book
                    tryAddToOrderBook(matchedSellOrder, buyOrderBook);

//                sellOrderBookPerSymbol.add(filledSellOrder);
                }
            } else {
                if (orderBookHasLimitOrderPerSymbol(newBuyOrder.getSymbol())) {
                    //if both sides are Market order
                    if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT)) {
                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newBuyOrder.getSymbol());
                        //filled
                        double latestPrice = findLatestPrice(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
                        output = output + matchedSellOrder.fill(newBuyOrder, latestPrice);
                        output = output + newBuyOrder.fill(matchedSellOrder,latestPrice);

                        //add filled ones back to order book
                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);

                    }
                    //one side is Limit order
                    else if (sellOrderBookPerSymbol.peek().getPrice() >= newBuyOrder.getPrice()) {
                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                        //filled
                        output = output + matchedSellOrder.fill(newBuyOrder);
                        output = output + newBuyOrder.fill(matchedSellOrder);

                        //add filled ones back to order book
                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);

                    }
                }
            }

        }


        return output;
    }

    //find the top Limit order price in both buy and sell order books
    //if same price, compare sequence
    private double findLatestPrice(PriorityQueue<Order> sellOrderBookPerSymbol, PriorityQueue<Order> buyOrderBookPerSymbol) {
        Optional<Order> firstInSellOrderBook = sellOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(OrderType.LMT)).findFirst();
        Optional<Order> firstInBuyOrderBook = buyOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(OrderType.LMT)).findFirst();

        if(firstInBuyOrderBook.isPresent()&&firstInSellOrderBook.isEmpty()){
            return firstInBuyOrderBook.get().getPrice();
        }else if (firstInBuyOrderBook.isEmpty()&&firstInSellOrderBook.isPresent()){
            return firstInSellOrderBook.get().getPrice();
        }else{
            if(firstInBuyOrderBook.get().getSeq()<firstInSellOrderBook.get().getSeq()){
                return firstInBuyOrderBook.get().getPrice();
            }      else {
                return firstInSellOrderBook.get().getPrice();
            }
        }
    }

    private boolean orderBookHasLimitOrderPerSymbol(String symbol) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(symbol);

        return sellOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(OrderType.LMT));
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
