package models;

public class ExecutionReport {
    private ReportType type;
    private Order order;
    private String report;
    private Fill fill;

    public ExecutionReport(ReportType type, Order order, Fill fill, String report) {
        this.fill = fill;
        this.type = type;
        this.order = order;
        this.report = report;
    }

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

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Fill getFill() {
        return this.fill;
    }
}
