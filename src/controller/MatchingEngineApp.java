package controller;

import model.ExecutionReportable;
import model.Order;
import validator.HeaderValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchingEngineApp {
    public MatchingEngine engine;

    public MatchingEngineApp() {
        engine = new MatchingEngine();
    }

    public String addInput(String input) {
        String[] split = input.split("\\R");

        List<ExecutionReportable> headerValidation = HeaderValidator.validate(split[0]);
        List<ExecutionReportable> executionReports = new ArrayList<>(headerValidation);

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

    private boolean isFatal(List<ExecutionReportable> headerValidation) {
        return headerValidation.stream().allMatch(ExecutionReportable::isFatal);
    }

    private String printReport(List<ExecutionReportable> executionReports) {
        List<ExecutionReportable> sorted = executionReports.stream().sorted().collect(Collectors.toList());

        return sorted.stream().map(ExecutionReportable::getReport).collect(Collectors.joining("\n"));
    }

}
