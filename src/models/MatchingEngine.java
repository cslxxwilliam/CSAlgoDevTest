package models;

import java.util.*;

import static java.lang.Math.min;

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

    private void tryAddToSellOrderBook(Order sellOrder, HashMap<String, PriorityQueue<Order>> orderBook) {

    }

    public static Comparator<Order> buyOrderComparator = (c1, c2) -> {
        //price larger, the less
        if (c1.getOrderType().compareTo(c2.getOrderType()) == 0) {
            return (int) - (c1.getPrice() - c2.getPrice());
        }
        //market order less than limit
        else {
            return c1.getOrderType().compareTo(c2.getOrderType());
        }
    };

    public static Comparator<Order> sellOrderComparator = (c1, c2) -> {
        //price lower, the less
        if (c1.getOrderType().compareTo(c2.getOrderType()) == 0) {
            return (int) (c1.getPrice() - c2.getPrice());
        }
        //market order less than limit
        else {
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
        Order topBuyOrder;
        if (sellOrderBookPerSymbol != null) {

            while (hasMatch(peekTopBuyOrder, buyOrderBookPerSymbol, sellOrderBookPerSymbol)) {
                topBuyOrder = buyOrderBookPerSymbol.poll();
                output=output+match(buyOrderBookPerSymbol, sellOrderBookPerSymbol, topBuyOrder);
                peekTopBuyOrder = findTopBuyOrder(buyOrderBookPerSymbol);
            }

        }


        return output;
    }

    private String match(PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol, Order topBuyOrder) {
        Order matchedSellOrder = sellOrderBookPerSymbol.poll();

        //filled
        double latestPrice;

        if (topBuyOrder.getOrderType().equals(OrderType.MKT) && matchedSellOrder.getOrderType().equals(OrderType.MKT)) {
            latestPrice = findLatestPriceInOrderBooks(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
        } else {
            if(topBuyOrder.getOrderType().equals(OrderType.MKT)){
                latestPrice = matchedSellOrder.getPrice();
            } else if(matchedSellOrder.getOrderType().equals(OrderType.MKT)){
                latestPrice = topBuyOrder.getPrice();
            }
            else{
                latestPrice = findLatestPriceBySeq(topBuyOrder, matchedSellOrder);
            }
        }

        int fillQty = calculateFillQty(topBuyOrder, matchedSellOrder);
        String output = matchedSellOrder.fill(fillQty, latestPrice);
        output = output + topBuyOrder.fill(fillQty, latestPrice);

        if(!matchedSellOrder.isFullyFilled()){
            sellOrderBookPerSymbol.add(matchedSellOrder);
        }

        if(!topBuyOrder.isFullyFilled()){
            buyOrderBookPerSymbol.add(topBuyOrder);
        }
        return output;
    }

    private double findLatestPriceBySeq(Order buy, Order sell) {
        if(buy.getSeq()<sell.getSeq()){
            return buy.getPrice();
        }

        return sell.getPrice();
    }

    private int calculateFillQty(Order buy, Order sell) {

        return min(buy.getUnFilledQty(), sell.getUnFilledQty());
    }

    private boolean hasMatch(Order topBuyOrder, PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol) {
        if(topBuyOrder==null){
            return false;
        }

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
    private double findLatestPriceInOrderBooks(PriorityQueue<Order> sellOrderBookPerSymbol, PriorityQueue<Order> buyOrderBookPerSymbol) {
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
            if(orderToAdd.getSide().equals(BuySell.Buy)) {
                orderBookPerSymbol = new PriorityQueue<>(buyOrderComparator);
            }else{
                orderBookPerSymbol = new PriorityQueue<>(sellOrderComparator);
            }

            orderBookPerSymbol.add(orderToAdd);
            orderBook.put(orderToAdd.getSymbol(), orderBookPerSymbol);
        } else {
            orderBookPerSymbol.add(orderToAdd);
        }
    }
}
