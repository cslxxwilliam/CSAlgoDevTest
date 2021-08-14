package controller;

import model.*;
import validator.HeaderValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static model.ReportType.Ack;

public class MatchingEngineApp {
    public MatchingEngine engine;
    public static int seq = 0;

    public MatchingEngineApp() {
        engine = new MatchingEngine();
    }

    public String addInput(String input) {
        String[] split = input.split("\\R");
        List<Order> orderList = new ArrayList<>();

        //validate header
        //each order
        //validate order
        //call factory to create

        List<ExecutionReport> headerValidation = HeaderValidator.validate(split[0]);
        List<ExecutionReport> executionReports = new ArrayList<>(headerValidation);

        //no error
        if (!isFatal(headerValidation)) {

            //validate orders
            for (int i = 1; i < split.length; i++) {
                String[] orderDetails = split[i].split(",");

                //ToDo convert to a factory with validation
                Order order = null;
                Status status = Status.Ack;
                String orderId = orderDetails[0];
                String symbol = orderDetails[1];
                BuySell side = BuySell.valueOf(orderDetails[3]);
                int qty = Integer.parseInt(orderDetails[4]);
                if (qty >= 10000000) {
                    status = Status.Reject;
                    order = buildOrder(seq++, orderDetails, status, orderId, symbol, side, qty);
                } else {
                    order = buildOrder(seq++, orderDetails, status, orderId, symbol, side, qty);
                }
                orderList.add(order);
                executionReports.add(new ExecutionReport(Ack, order, null, order.toString() + "\n"));
            }

            //add to orderbook
            executionReports.addAll(engine.addAndMatch(orderList));
        }

        return printReport(executionReports);
    }

    private boolean isFatal(List<ExecutionReport> headerValidation) {
        return headerValidation.stream().allMatch(v ->v.getReport().equals("Invalid headers"));
    }

    private String printReport(List<ExecutionReport> executionReports) {
        executionReports.sort(reportComparator);

        String output = "";
        for (ExecutionReport report : executionReports) {
            output = output + report.getReport();
        }
        return output;
    }

    private Order buildOrder(int seq, String[] orderDetails, Status status, String orderId, String symbol, BuySell side, int qty) {
        Order order;
        if (orderDetails[2].equals("MKT")) {
            order = new Order(seq, status, orderId, symbol, OrderType.MKT, side, qty);

        } else {
            order = new Order(seq, status, orderId, symbol, Double.parseDouble(orderDetails[2]), side, qty);
        }
        return order;
    }


    //refator for a better comparator
    public static Comparator<ExecutionReport> reportComparator = (r1, r2) -> {
        //price larger, the less
        if (r1.getType().compareTo(r2.getType()) == 0) {
            if (r1.getFill() == null || r2.getFill() == null) {
                return r1.getOrder().getSeq() - r2.getOrder().getSeq();
            }

            if (r1.getFill().getSeq() == r2.getFill().getSeq()) {
                return r1.getOrder().getSeq() - r2.getOrder().getSeq();
            }
            return r1.getFill().getSeq() - r2.getFill().getSeq();
        }
        //market order less than limit
        else {
            return r1.getType().compareTo(r2.getType());
        }
    };
}
