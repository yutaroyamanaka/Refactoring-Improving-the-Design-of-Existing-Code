package chapter01;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.*;

public class CreditCalculatorTest {

    CreditsCalculator creditsCalculator;

    @BeforeEach
    void setup() {
        try {
            creditsCalculator = new CreditsCalculator("./src/main/resources/plays.json",
                    "./src/main/resources/invoices.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testStatement() {
        assertEquals(creditsCalculator.statement(), "Statement for BigCo\n" +
                " Hamlet :  $650 55 seats\n" +
                " As You Like It :  $580 35 seats\n" +
                " Othello :  $500 40 seats\n" +
                "Amount owed is $1730\n" +
                "You earned 40 credits\n");
    }
}
