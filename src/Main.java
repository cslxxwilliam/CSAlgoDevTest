import controller.MatchingEngineApp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        MatchingEngineApp matchingEngineApp = new MatchingEngineApp();
        System.out.println("Welcome to Matching Engine!\nUsage: 2 newlines to end your orders");

        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        stdin.useDelimiter("\\R");
        while (true) {

            String inputOrders = "";
            String line = "";
            while (!(line = stdin.nextLine()).isEmpty()) {
                inputOrders = inputOrders.concat(line).concat("\n");
            }
            String output = matchingEngineApp.addInput(inputOrders);

            System.out.println(output);
        }
    }

}