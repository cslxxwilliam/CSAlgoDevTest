package validator;

import model.ExecutionReport;

import java.util.List;

public class OrderValidator implements Validator{
    public static List<ExecutionReport> validate(int qty) {
        return null;
    }

    @Override
    public String getType() {
        return "Order";
    }
}
