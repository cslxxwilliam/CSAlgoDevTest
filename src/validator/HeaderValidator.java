package validator;

import model.HeaderExecutionReport;
import model.ExecutionReportable;

import java.util.ArrayList;
import java.util.List;

public class HeaderValidator implements Validator{

    public static final String INPUT_ORDER_HEADERS = "#OrderID,Symbol,Price,Side,OrderQuantity";
    public static final String INVALID_HEADERS = "Invalid headers";
    public static final String EXEC_REPORT_HEADERS = "#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity";

    public static List<ExecutionReportable> validate(String headers) {
        List<ExecutionReportable> reports = new ArrayList<>();
        if (!headers.equals(INPUT_ORDER_HEADERS)) {
            reports.add(new HeaderExecutionReport(true, INVALID_HEADERS));
            return reports;
        }

        reports.add(new HeaderExecutionReport(false, EXEC_REPORT_HEADERS));

        return reports;
    }

    @Override
    public String getType() {
        return "Header";
    }
}
