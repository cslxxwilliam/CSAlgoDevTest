import models.MatchingEngineApp;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLogic {
    private MatchingEngineApp app = new MatchingEngineApp();

//ToDo test structure

    @Test
    public void validateHeaders() {
        String invalidInputHeader = "#OrderIDTest,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,20000";
        String output = app.addInput(invalidInputHeader);

        assertEquals("Invalid headers", output);
    }

    @Test
    public void ackAndFillBasic() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,20000\nOrder2,0700.HK,610,Sell,10000\n" +
                "Order3,0700.HK,610,Buy,10000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,610,Sell,20000\n" +
                "Ack,Order2,0700.HK,610,Sell,10000\n" +
                "Ack,Order3,0700.HK,610,Buy,10000\n" +
                "Fill,Order1,0700.HK,610,Sell,20000,610,10000\n" +
                "Fill,Order3,0700.HK,610,Buy,10000,610,10000\n", output);
    }

    @Test
    public void fillMarketOrder() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,20000\n" +
                "Order2,0700.HK,MKT,Sell,10000\n" +
                "Order3,0700.HK,610,Buy,10000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,610,Sell,20000\n" +
                "Ack,Order2,0700.HK,MKT,Sell,10000\n" +
                "Ack,Order3,0700.HK,610,Buy,10000\n" +
                "Fill,Order2,0700.HK,MKT,Sell,10000,610,10000\n" +
                "Fill,Order3,0700.HK,610,Buy,10000,610,10000\n", output);
    }

    @Test
    public void qtyValidation() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,10000\n" +
                "Order2,0700.HK,610,Buy,10000000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,610,Sell,10000\n" +
                "Reject,Order2,0700.HK,610,Buy,10000000\n", output);
    }

    @Test
    public void matchMultipleSymbol() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,10000\n" +
                "Order2,0005.HK,49.8,Sell,10000\n" +
                "Order3,0005.HK,49.8,Buy,10000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,610,Sell,10000\n" +
                "Ack,Order2,0005.HK,49.8,Sell,10000\n" +
                "Ack,Order3,0005.HK,49.8,Buy,10000\n" +
                "Fill,Order2,0005.HK,49.8,Sell,10000,49.8,10000\n" +
                "Fill,Order3,0005.HK,49.8,Buy,10000,49.8,10000\n", output);
    }

    @Test
    public void onlyMarketOrdersWillNotMatch() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,MKT,Sell,20000\n" +
                "Order2,0700.HK,MKT,Buy,10000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,MKT,Sell,20000\n" +
                "Ack,Order2,0700.HK,MKT,Buy,10000\n", output);
    }

    @Test
    public void marketOrdersMatchByLatestLimitOrderPrice() {
        String validInput = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,MKT,Sell,10000\n" +
                "Order2,0700.HK,MKT,Buy,10000\n" +
                "Order3,0700.HK,610,Buy,10000\n\n";
        String output = app.addInput(validInput);

        assertEquals("#ActionType,OrderID,Symbol,Price,Side,OrderQuantity,FillPrice,FillQuantity\n" +
                "Ack,Order1,0700.HK,MKT,Sell,10000\n" +
                "Ack,Order2,0700.HK,MKT,Buy,10000\n" +
                "Ack,Order3,0700.HK,610,Buy,10000\n" +
                "Fill,Order1,0700.HK,MKT,Sell,10000,610,10000\n" +
                "Fill,Order2,0700.HK,MKT,Buy,10000,610,10000\n", output);
    }

    //Same price, timestamp matters

    //Match across multiple orders

}