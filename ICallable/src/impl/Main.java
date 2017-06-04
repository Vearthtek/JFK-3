package impl;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    static public Double GETToRate(String currency) {
        String httpsURL = "https://api.nbp.pl/api/exchangerates/rates/A/" + currency;
        try {
            URL myurl = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String wordToFind = "\"mid\":[0-9]+.[0-8]*";
                Pattern word = Pattern.compile(wordToFind);
                Matcher match = word.matcher(inputLine);

                while (match.find()) {
                    String numberToFind = "[0-9]+.[0-8]*";
                    Pattern number = Pattern.compile(numberToFind);
                    Matcher match2 = number.matcher(match.group());
                    while (match2.find()) {
                        return Double.parseDouble(match2.group());
                    }
                }
            }

            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.;
    }

    public static void main(String[] args) {
        GETToRate("EUR");
    }
}
