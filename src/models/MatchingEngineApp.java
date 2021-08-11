package models;

public class MatchingEngineApp {
    public String addInput(String input) {
        String[] split = input.split("\n");
        if(!split[0].equals("#OrderID,Symbol,Price,Side,OrderQuantity")){
            return "Invalid headers";
        }
        return input;
    }
}
