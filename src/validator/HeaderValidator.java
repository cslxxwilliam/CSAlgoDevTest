package validator;

import model.HeaderExecutionReport;
import model.ExecutionReportable;

import java.util.ArrayList;
import java.util.List;

public class HeaderValidator implements Validator{
    public static List<ExecutionReportable> validate(String headers) {
        List<ExecutionReportable> reports = new ArrayList<>();
        if (!headers.equals("#OrderID,Symbol,Price,Side,OrderQuantity")) {
            reports.add(new HeaderExecutionReport("Invalid headers"));
            return reports;
        }

        reports.add(new HeaderExecutionReport("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n"));

        return reports;
    }

    @Override
    public String getType() {
        return "Header";
    }
}
