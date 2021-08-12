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
    public void ackOrder() {
        String invalidInputHeader = "#OrderID,Symbol,Price,Side,OrderQuantity\n" +
                "Order1,0700.HK,610,Sell,20000";
        String output = app.addInput(invalidInputHeader);

        assertEquals("Ack,Order1,0700.HK,610,Sell,20000", output);
    }
}