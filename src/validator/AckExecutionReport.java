package validator;

import model.ExecutionReportable;
import model.Order;

public class AckExecutionReport implements ExecutionReportable {

    public static final int ACK_REPORT_PRIORITY = 2;
    private String report;
    private Order order;

    public AckExecutionReport(Order order, String report) {

        this.order = order;
        this.report = report;
    }

    @Override
    public String getReport() {
        return this.report;
    }

    @Override
    public int getPriority() {
        return ACK_REPORT_PRIORITY;
    }

    @Override
    public boolean isFatal() {
        return false;
    }

    @Override
    public int compareTo(ExecutionReportable o) {
        if(o instanceof AckExecutionReport){
            return this.order.compareTo(((AckExecutionReport) o).getOrder());
        }

        return this.getPriority()-o.getPriority();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
