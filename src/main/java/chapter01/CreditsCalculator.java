package chapter01;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CreditsCalculator {
    private HashMap plays;
    private HashMap invoices;

    public CreditsCalculator(String pathPlays, String pathInvoices) throws IOException {
        InputStream inputStreamPlay = new FileInputStream(pathPlays);
        InputStream inputStreamInvoices = new FileInputStream(pathInvoices);
        plays = new ObjectMapper().readValue(inputStreamPlay, HashMap.class);
        invoices = new ObjectMapper().readValue(inputStreamInvoices, HashMap.class);
    }

    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder("Statement for " + (String) invoices.get("customer") + "\n");

        ArrayList<HashMap<String, Integer>> performances = (ArrayList<HashMap<String,java.lang.Integer>>) invoices.get("performances");
        for (HashMap<String, Integer> perf : performances) {
            HashMap<String, String> play = (HashMap<String, String>) plays.get(perf.get("playID"));
            int thisAmount = 0;

            switch (play.get("type")) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.get("audience") > 30) {
                        thisAmount += 1000 * (perf.get("audience") - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.get("audience") > 20) {
                        thisAmount += 10000 + 500 * (perf.get("audience") - 20);
                    }
                    thisAmount += 300 * perf.get("audience");
                    break;
                default:
                    throw new RuntimeException("unknown type : " + play.get("type"));
            }

            volumeCredits += Math.max(perf.get("audience") - 30, 0);
            if ("comedy" == play.get("type")) volumeCredits += Math.floor(perf.get("audience") / 5);
            result.append(String.format(" %s :  $%d %d seats\n", play.get("name"), thisAmount / 100, perf.get("audience")));
            totalAmount += thisAmount;
        }
        result.append("Amount owed is $" + (totalAmount / 100) + "\n");
        result.append("You earned " + volumeCredits + " credits\n");
        return result.toString();
    }
}
