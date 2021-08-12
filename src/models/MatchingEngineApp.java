package models;

public class MatchingEngineApp {
    public MatchingEngine engine;

    public MatchingEngineApp(){
        engine = new MatchingEngine();

    }
    public String addInput(String input) {
        String[] split = input.split("\n");
        if(!split[0].equals("#OrderID,Symbol,Price,Side,OrderQuantity")){
            return "Invalid headers";
        }

        String output ="";
        for(int i =1;i<split.length; i++){
            String[] orderDetails = split[i].split(",");
            Order order = new Order(Status.ACCEPTED, orderDetails[0], orderDetails[1], Double.parseDouble(orderDetails[2]), BuySell.valueOf(orderDetails[3]), Integer.parseInt(orderDetails[4]));

//            "Order1,0700.HK,610,Sell,20000"

            output = output + order.toString();
        }

        return output;
    }
}
