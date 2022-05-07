package chapter01;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
    StringBuilder result =
        new StringBuilder("Statement for " + (String) invoices.get("customer") + "\n");

    for (HashMap<String, Integer> perf :
        (ArrayList<HashMap<String, Integer>>) invoices.get("performances")) {
      // print line for this order
      result.append(
          String.format(
              " %s :  %s %d seats\n",
              playFor(perf).get("name"), usd(amountFor(perf)), perf.get("audience")));
    }
    result.append(String.format("Amount owed is %s\n", usd(totalAmount())));
    result.append(String.format("You earned %d credits\n", totalVolumeCredits()));
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

  public int totalAmount() {
    int result = 0;
    for (HashMap<String, Integer> perf :
        (ArrayList<HashMap<String, Integer>>) invoices.get("performances")) {
      result += amountFor(perf);
    }
    return result;
  }

  public int totalVolumeCredits() {
    int result = 0;
    for (HashMap<String, Integer> perf :
        (ArrayList<HashMap<String, Integer>>) invoices.get("performances")) {
      result += volumeCreditsFor(perf);
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

  public String usd(int aNumber) {
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    numberFormat.setMinimumFractionDigits(2);
    return numberFormat.format(aNumber / 100);
  }
}
