package controller;

import model.ExecutionReport;
import model.Order;
import validator.HeaderValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MatchingEngineApp {
    public MatchingEngine engine;
    public static int seq = 0;

    public MatchingEngineApp() {
        engine = new MatchingEngine();
    }

    public String addInput(String input) {
        String[] split = input.split("\\R");

        List<ExecutionReport> headerValidation = HeaderValidator.validate(split[0]);
        List<ExecutionReport> executionReports = new ArrayList<>(headerValidation);

        //no error
        if (!isFatal(headerValidation)) {

            //validate orders
            Order order;
            for (int i = 1; i < split.length; i++) {
                String[] orderDetails = split[i].split(",");
                order = OrderFactory.create(orderDetails[0], orderDetails[1], orderDetails[2], orderDetails[3], orderDetails[4]);

                executionReports.addAll(engine.add(order));
                executionReports.addAll(engine.match(order));
            }
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
