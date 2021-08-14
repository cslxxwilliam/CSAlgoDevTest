package controller;

import model.ExecutionReport;
import model.Fill;
import model.Order;

import java.util.*;

import static java.lang.Math.min;
import static model.BuySell.Buy;
import static model.OrderType.LMT;
import static model.OrderType.MKT;
import static model.ReportType.Ack;
import static model.Status.Reject;

public class MatchingEngine {
    private HashMap<String, PriorityQueue<Order>> buyOrderBook;
    private HashMap<String, PriorityQueue<Order>> sellOrderBook;
    private List<Order> rejectedOrders;

    public MatchingEngine() {
        buyOrderBook = new HashMap<>();
        sellOrderBook = new HashMap<>();
        rejectedOrders = new ArrayList<>();
    }


    public List<ExecutionReport> add(Order order) {
        List<ExecutionReport> reports = new ArrayList<>();
        if(order.getStatus().equals(Reject)){
            rejectedOrders.add(order);
        }else{
            if(order.getSide().equals(Buy)){
                tryAddToOrderBook(order, buyOrderBook);
            }else{
                tryAddToOrderBook(order, sellOrderBook);
            }

        }
        reports.add(new ExecutionReport(Ack, order, null, order.toString() + "\n"));

        return reports;
    }

    public List<ExecutionReport> match(Order newOrder) {
        PriorityQueue<Order> buyOrderBookPerSymbol = buyOrderBook.get(newOrder.getSymbol());
        PriorityQueue<Order> sellOrderBookPerSymbol = sellOrderBook.get(newOrder.getSymbol());

        List<ExecutionReport> executionReports = new ArrayList<>();

        //match with existing sell order book
        while (hasMatch(buyOrderBookPerSymbol, sellOrderBookPerSymbol)) {
            executionReports.addAll(match(buyOrderBookPerSymbol, sellOrderBookPerSymbol));
        }

        return executionReports;
    }


    private List<ExecutionReport> match(PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol) {
        Order sell = sellOrderBookPerSymbol.poll();
        Order buy = buyOrderBookPerSymbol.poll();

        //filled
        double latestPrice;

        if (buy.getOrderType().equals(MKT) && sell.getOrderType().equals(MKT)) {
            latestPrice = findLatestPriceInOrderBooks(sellOrderBookPerSymbol, buyOrderBookPerSymbol);
        } else {
            if (buy.getOrderType().equals(LMT) && sell.getOrderType().equals(MKT)) {
                latestPrice = buy.getPrice();
            } else if (buy.getOrderType().equals(MKT) && sell.getOrderType().equals(LMT)) {
                latestPrice = sell.getPrice();
            } else {
                latestPrice = findLatestPriceBySeq(buy, sell);
            }
        }

        int fillQty = calculateFillQty(buy, sell);

        //ToDo refactor
        List<ExecutionReport> executionReports = new ArrayList<>(fill(buy, sell, fillQty, latestPrice));

        if (!sell.isFullyFilled()) {
            sellOrderBookPerSymbol.add(sell);
        }

        if (!buy.isFullyFilled()) {
            buyOrderBookPerSymbol.add(buy);
        }
        return executionReports;
    }

    private List<ExecutionReport> fill(Order buy, Order sell, int fillQty, double latestPrice) {
        List<ExecutionReport> reports=new ArrayList<>();
        int fillSeq = Fill.nextSeq();


        reports.add(sell.fill(fillQty, latestPrice, fillSeq));
        reports.add(buy.fill(fillQty, latestPrice, fillSeq));

        return reports;
    }

    private double findLatestPriceBySeq(Order buy, Order sell) {
        if (buy.getSeq() < sell.getSeq()) {
            return buy.getPrice();
        }

        return sell.getPrice();
    }

    private int calculateFillQty(Order buy, Order sell) {

        return min(buy.getUnFilledQty(), sell.getUnFilledQty());
    }

    //for both buy and sell
    private boolean hasMatch(PriorityQueue<Order> buyOrderBookPerSymbol, PriorityQueue<Order> sellOrderBookPerSymbol) {
        if (buyOrderBookPerSymbol== null || sellOrderBookPerSymbol == null) {
            return false;
        }
        if (buyOrderBookPerSymbol.isEmpty() || sellOrderBookPerSymbol.isEmpty()) {
            return false;
        }

        Order buyOrder = buyOrderBookPerSymbol.peek();
        if (buyOrder == null) {
            return false;
        }

        Order sellOrder = sellOrderBookPerSymbol.peek();
        if (sellOrder == null) {
            return false;
        }

        if (buyOrder.getOrderType().equals(LMT) && sellOrder.getOrderType().equals(LMT)) {
            return buyOrder.getPrice() >= sellOrder.getPrice();
        }

        if (buyOrder.getOrderType().equals(LMT) && sellOrder.getOrderType().equals(MKT)) {
            return true;
        }

        if (buyOrder.getOrderType().equals(MKT) && sellOrder.getOrderType().equals(LMT)) {
            return true;
        }

        return orderBookHasLimitOrderPerSymbol(buyOrder.getSymbol());
    }

    //find the top Limit order price in both buy and sell order books
    //if same price, compare sequence
    private double findLatestPriceInOrderBooks(PriorityQueue<Order> sellOrderBookPerSymbol, PriorityQueue<Order> buyOrderBookPerSymbol) {
        Optional<Order> firstInSellOrderBook = sellOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(LMT)).findFirst();
        Optional<Order> firstInBuyOrderBook = buyOrderBookPerSymbol.stream().filter(o -> o.getOrderType().equals(LMT)).findFirst();

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

        boolean sellOrderBookHasLimitOrder = sellOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(LMT));
        boolean buyOrderBookHasLimitOrder = buyOrderBookPerSymbol.stream().anyMatch(o -> o.getOrderType().equals(LMT));
        return sellOrderBookHasLimitOrder || buyOrderBookHasLimitOrder;
    }

    private void tryAddToOrderBook(Order orderToAdd, HashMap<String, PriorityQueue<Order>> orderBook) {
        PriorityQueue<Order> orderBookPerSymbol = orderBook.get(orderToAdd.getSymbol());
        if (orderBookPerSymbol == null) {
            if (orderToAdd.getSide().equals(Buy)) {
                orderBookPerSymbol = new PriorityQueue<>(Order.buyOrderComparator);
            } else {
                orderBookPerSymbol = new PriorityQueue<>(Order.sellOrderComparator);
            }

            orderBookPerSymbol.add(orderToAdd);
            orderBook.put(orderToAdd.getSymbol(), orderBookPerSymbol);
        } else {
            orderBookPerSymbol.add(orderToAdd);
        }
    }
}
