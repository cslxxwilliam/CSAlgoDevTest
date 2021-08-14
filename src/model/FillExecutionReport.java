package model;

public class FillExecutionReport implements ExecutionReportable {
    public static final int FILL_REPORT_PRIORITY = 3;
    private final Order order;
    private final Fill fill;
    private final String report;

    public FillExecutionReport(Order order, Fill fill, String report) {
        this.fill = fill;
        this.order = order;
        this.report = report;
    }

    public Order getOrder() {
        return order;
    }

    public Fill getFill() {
        return this.fill;
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
    public boolean isFatal() {
        return false;
    }

    @Override
    public int compareTo(ExecutionReportable o) {
        if(o instanceof FillExecutionReport){
            int fillCompared = this.fill.compareTo(((FillExecutionReport) o).getFill());
            return fillCompared ==0?this.order.compareTo(((FillExecutionReport) o).getOrder()):fillCompared;
        }

        return this.getPriority()-o.getPriority();
    }
}
