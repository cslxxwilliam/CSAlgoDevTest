package model;

import java.util.Comparator;

public class FillExecutionReport implements ExecutionReport {
    public static final int FILL_REPORT_PRIORITY = 3;
    private ReportType type;
    private Order order;
    private Fill fill;
    private String report;

    public FillExecutionReport(ReportType type, Order order, Fill fill, String report) {
        this.fill = fill;
        this.type = type;
        this.order = order;
        this.report = report;
    }

    @Override
    public String getReport() {
        return report;
    }

    @Override
    public int getPriority() {
        return FILL_REPORT_PRIORITY;
    }

    @Override
    public int compareTo(ExecutionReport o) {
        if(o instanceof FillExecutionReport){
            int fillCompared = this.fill.compareTo(((FillExecutionReport) o).getFill());
            return fillCompared ==0?this.order.compareTo(((FillExecutionReport) o).getOrder()):fillCompared;
        }

        return this.getPriority()-o.getPriority();
    }

    @Override
    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Fill getFill() {
        return this.fill;
    }

}
