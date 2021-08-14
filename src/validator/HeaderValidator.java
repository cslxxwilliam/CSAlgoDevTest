package validator;

import model.ExecutionReport;
import model.ReportType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeaderValidator {
    public static List<ExecutionReport> validate(String headers) {
        List<ExecutionReport> reports = new ArrayList<>();
        if (!headers.equals("#OrderID,Symbol,Price,Side,OrderQuantity")) {
            reports.add(new ExecutionReport(ReportType.Header,"Invalid headers"));
            return reports;
        }

        reports.add(new ExecutionReport(ReportType.Header, "#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n"));

        return reports;
    }
}
