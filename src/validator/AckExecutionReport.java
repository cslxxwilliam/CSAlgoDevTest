package validator;

import model.ExecutionReport;
import model.FillExecutionReport;
import model.Order;
import model.ReportType;

public class AckExecutionReport implements ExecutionReport {

    public static final int ACK_REPORT_PRIORITY = 2;
    private String report;
    private Order order;

    public AckExecutionReport(Order order, String report) {

        this.order = order;
        this.report = report;
    }

    @Override
    public ReportType getType() {
        return ReportType.Ack;
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
    public int compareTo(ExecutionReport o) {
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
