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

        Order topBuyOrder = findTopBuyOrder(buyOrderBookPerSymbol);
        //match with existing sell order book
        Order filledSellOrder = null;
        if (sellOrderBookPerSymbol != null) {
            if (!topBuyOrder.getOrderType().equals(OrderType.MKT)) {
                if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT) || sellOrderBookPerSymbol.peek().getPrice() >= topBuyOrder.getPrice()) {
                    Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                    //filled
                    output = output + matchedSellOrder.fill(topBuyOrder);
                    output = output + topBuyOrder.fill(matchedSellOrder);

                    //add filled ones back to order book
                    tryAddToOrderBook(matchedSellOrder, buyOrderBook);

//                sellOrderBookPerSymbol.add(filledSellOrder);
                }
            } else {
                if (orderBookHasLimitOrderPerSymbol(topBuyOrder.getSymbol())) {
                    //if both sides are Market order
                    if (sellOrderBookPerSymbol.peek().getOrderType().equals(OrderType.MKT)) {
                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                        //filled
                        double latestPrice = findLatestPrice(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
                        output = output + matchedSellOrder.fill(topBuyOrder, latestPrice);
                        output = output + topBuyOrder.fill(matchedSellOrder,latestPrice);

                        //add filled ones back to order book
                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);

                    }
                    //one side is Limit order
                    else if (sellOrderBookPerSymbol.peek().getPrice() >= topBuyOrder.getPrice()) {
                        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

                        //filled
                        output = output + matchedSellOrder.fill(topBuyOrder);
                        output = output + topBuyOrder.fill(matchedSellOrder);

                        //add filled ones back to order book
                        tryAddToOrderBook(matchedSellOrder, buyOrderBook);

                    }
                }
            }

        }


        return output;
    }

    private Order findTopBuyOrder(PriorityQueue<Order> buyOrderBookPerSymbol) {
        return buyOrderBookPerSymbol.poll();
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
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(symbol);

        boolean sellOrderBookHasLimitOrder = sellOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(OrderType.LMT));
        boolean buyOrderBookHasLimitOrder = buyOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(OrderType.LMT));
        return sellOrderBookHasLimitOrder||buyOrderBookHasLimitOrder;
    }

    private void tryAddToOrderBook(Order orderToAdd, HashMap<String, PriorityQueue<Order>> orderBook) {
        PriorityQueue<Order> orderBookPerSymbol = orderBook.get(orderToAdd.getSymbol());
        if (orderBookPerSymbol == null) {
            orderBookPerSymbol = new PriorityQueue<>(orderComparator);
            orderBookPerSymbol.add(orderToAdd);
            orderBook.put(orderToAdd.getSymbol(), orderBookPerSymbol);
        }
        orderBookPerSymbol.add(orderToAdd);
    }
}
