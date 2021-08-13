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
    private String matchBuy(Order newNewBuyOrder) {
        //add the new order to order book, either filled or not
        tryAddToOrderBook(newNewBuyOrder, buyOrderBook);

        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newNewBuyOrder.getSymbol());
        String output = "";

        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newNewBuyOrder.getSymbol());

        Order peekTopBuyOrder = findTopBuyOrder(buyOrderBookPerSymbol);

        //match with existing sell order book
        Order filledSellOrder = null;
        if (sellOrderBookPerSymbol != null) {

            Order topBuyOrder;
            while (hasMatch(peekTopBuyOrder, buyOrderBookPerSymbol, sellOrderBookPerSymbol)) {
                topBuyOrder = buyOrderBookPerSymbol.poll();
                output=output+match(buyOrderBookPerSymbol, sellOrderBookPerSymbol, topBuyOrder);
                peekTopBuyOrder = findTopBuyOrder(buyOrderBookPerSymbol);
            }


//            if (!topBuyOrder.getOrderType().equals(OrderType.MKT)) {
//                if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT) || sellOrderBookPerSymbol.peek().getPrice() >= topBuyOrder.getPrice()) {
//                    Order matchedSellOrder = sellOrderBookPerSymbol.poll();
//
//                    //filled
//                    output = output + matchedSellOrder.fill(topBuyOrder);
//                    output = output + topBuyOrder.fill(matchedSellOrder);
//
//                    //add filled ones back to order book
//                    tryAddToOrderBook(matchedSellOrder, buyOrderBook);
//
////                sellOrderBookPerSymbol.add(filledSellOrder);
//                }
//            } else {
//
//                    //if both sides are Market order
//                    if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT)&&
//                            orderBookHasLimitOrderPerSymbol(topBuyOrder.getSymbol()))  {
//                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();
//
//                        //filled
//                        double latestPrice = findLatestPrice(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
//                        output = output + matchedSellOrder.fill(topBuyOrder, latestPrice);
//                        output = output + topBuyOrder.fill(matchedSellOrder,latestPrice);
//
//                        //add filled ones back to order book
//                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);
//
//                    }
//                    //one side is Limit order
//                    else if (sellOrderBookPerSymbol.peek().getPrice() >= topBuyOrder.getPrice()) {
//                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();
//
//                        //filled
//                        output = output + matchedSellOrder.fill(topBuyOrder);
//                        output = output + topBuyOrder.fill(matchedSellOrder);
//
//                        //add filled ones back to order book
//                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);
//
//                    }
//                }
//            }

        }


        return output;
    }

    private String match(PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol, Order topBuyOrder) {
        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

        //filled
        double latestPrice;

        if (topBuyOrder.getOrderType().equals(OrderType.MKT) && matchedSellOrder.getOrderType().equals(OrderType.MKT)) {
            latestPrice = findLatestPrice(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
        } else {
            latestPrice = matchedSellOrder.getPrice();
        }
        String output = matchedSellOrder.fill(topBuyOrder, latestPrice);
        output = output + topBuyOrder.fill(matchedSellOrder, latestPrice);

        return output;
    }

    private boolean hasMatch(Order topBuyOrder, PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol) {
        //if new order LMT order
        //if matching order is MKT order
        // if matching order is LMT order and price < buy order
        //if new order MKT


        Order peekSellOrder = sellOrderBookPerSymbol.peek();
        if (peekSellOrder == null) {
            return false;
        }
        if (!topBuyOrder.getOrderType().equals(OrderType.MKT)) {
            return peekSellOrder.getOrderType().equals(OrderType.MKT) || peekSellOrder.getPrice() <= topBuyOrder.getPrice();
        } else {
            if (peekSellOrder.getOrderType().equals(OrderType.MKT)) {
                return orderBookHasLimitOrderPerSymbol(topBuyOrder.getSymbol());
            } else {
                return peekSellOrder.getPrice() >= topBuyOrder.getPrice();
            }
        }
    }

    private Order findTopBuyOrder(PriorityQueue<Order> buyOrderBookPerSymbol) {
        return buyOrderBookPerSymbol.peek();
    }

    //find the top Limit order price in both buy and sell order books
    //if same price, compare sequence
    private double findLatestPrice(PriorityQueue<Order> sellOrderBookPerSymbol, PriorityQueue<Order> buyOrderBookPerSymbol) {
        Optional<Order> firstInSellOrderBook = sellOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(OrderType.LMT)).findFirst();
        Optional<Order> firstInBuyOrderBook = buyOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(OrderType.LMT)).findFirst();

        if (firstInBuyOrderBook.isEmpty() && firstInSellOrderBook.isEmpty()) {
            return 0;
        } else if (firstInBuyOrderBook.isPresent() && firstInSellOrderBook.isEmpty()) {
            return firstInBuyOrderBook.get().getPrice();
        } else if (firstInBuyOrderBook.isEmpty() && firstInSellOrderBook.isPresent()) {
            return firstInSellOrderBook.get().getPrice();
        } else {
            if (firstInBuyOrderBook.get().getSeq() < firstInSellOrderBook.get().getSeq()) {
                return firstInBuyOrderBook.get().getPrice();
            } else {
                return firstInSellOrderBook.get().getPrice();
            }
        }
    }

    private boolean orderBookHasLimitOrderPerSymbol(String symbol) {
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(symbol);
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(symbol);

        boolean sellOrderBookHasLimitOrder = sellOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(OrderType.LMT));
        boolean buyOrderBookHasLimitOrder = buyOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(OrderType.LMT));
        return sellOrderBookHasLimitOrder || buyOrderBookHasLimitOrder;
    }

    private void tryAddToOrderBook(Order orderToAdd, HashMap<String, PriorityQueue<Order>> orderBook) {
        PriorityQueue<Order> orderBookPerSymbol = orderBook.get(orderToAdd.getSymbol());
        if (orderBookPerSymbol == null) {
            orderBookPerSymbol = new PriorityQueue<>(orderComparator);
            orderBookPerSymbol.add(orderToAdd);
            orderBook.put(orderToAdd.getSymbol(), orderBookPerSymbol);
        } else {
            orderBookPerSymbol.add(orderToAdd);
        }
    }
}
