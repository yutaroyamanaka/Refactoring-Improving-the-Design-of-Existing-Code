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
    StringBuilder result =
        new StringBuilder("Statement for " + (String) invoices.get("customer") + "\n");

    ArrayList<HashMap<String, Integer>> performances =
        (ArrayList<HashMap<String, java.lang.Integer>>) invoices.get("performances");
    for (HashMap<String, Integer> perf : performances) {
      volumeCredits += volumeCreditsFor(perf);

      // print line for this order
      result.append(
          String.format(
              " %s :  $%d %d seats\n",
              playFor(perf).get("name"), amountFor(perf) / 100, perf.get("audience")));
      totalAmount += amountFor(perf);
    }
    result.append("Amount owed is $" + (totalAmount / 100) + "\n");
    result.append("You earned " + volumeCredits + " credits\n");
    return result.toString();
  }

  public int amountFor(HashMap<String, Integer> aPerformance) {
    int result = 0;

    switch (playFor(aPerformance).get("type")) {
      case "tragedy":
        result = 40000;
        if (aPerformance.get("audience") > 30) {
          result += 1000 * (aPerformance.get("audience") - 30);
        }
        break;
      case "comedy":
        result = 30000;
        if (aPerformance.get("audience") > 20) {
          result += 10000 + 500 * (aPerformance.get("audience") - 20);
        }
        result += 300 * aPerformance.get("audience");
        break;
      default:
        throw new RuntimeException("unknown type : " + playFor(aPerformance).get("type"));
    }
    return result;
  }

  public HashMap<String, String> playFor(HashMap<String, Integer> aPerformance) {
    return (HashMap<String, String>) plays.get(aPerformance.get("playID"));
  }

  public int volumeCreditsFor(HashMap<String, Integer> perf) {
    int volumeCredits = 0;
    volumeCredits += Math.max(perf.get("audience") - 30, 0);
    if ("comedy" == playFor(perf).get("type")) {
      volumeCredits += Math.floor(perf.get("audience") / 5);
    }
    return volumeCredits;
  }
}
