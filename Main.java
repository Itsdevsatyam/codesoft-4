package demo;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    private JComboBox<String> Cb1, Cb2;
    private JTextField tfAmount;
    private JButton b1;
    private JLabel l, l2, a, l3, l4, resultLabel;
    private JFrame f;

    public Main() {
        f = new JFrame();
        f.getContentPane().setBackground(new Color(49, 207, 218));
        f.setTitle("CURRENCY CONVERTER");
        f.setLocation(488, 213);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(null);
        f.setSize(500, 400);

        l = new JLabel("Select the from currency:");
        l.setForeground(Color.WHITE);
        l.setBounds(10, 50, 250, 20);
        l.setFont(new Font("Arial", Font.PLAIN, 16));

        l2 = new JLabel("Select the to currency:");
        l2.setForeground(Color.WHITE);
        l2.setBounds(10, 100, 250, 20);
        l2.setFont(new Font("Arial", Font.PLAIN, 16));

        a = new JLabel("Enter the amount to convert:");
        a.setForeground(Color.WHITE);
        a.setBounds(10, 150, 250, 20);
        a.setFont(new Font("Arial", Font.PLAIN, 16));

        l3 = new JLabel("FROM");
        l3.setForeground(Color.WHITE);
        l3.setBounds(275, 45, 50, 20);
        l3.setFont(new Font("Arial", Font.PLAIN, 14));

        l4 = new JLabel("TO");
        l4.setForeground(Color.WHITE);
        l4.setBounds(275, 94, 50, 20);
        l4.setFont(new Font("Arial", Font.PLAIN, 14));

        Cb1 = new JComboBox<>();
        Cb1.setBounds(190, 45, 80, 25);

        Cb2 = new JComboBox<>();
        Cb2.setBounds(190, 94, 80, 25);

        tfAmount = new JTextField();
        tfAmount.setBounds(260, 145, 80, 25);

        b1 = new JButton("Convert");
        b1.setFont(new Font("Arial", Font.PLAIN, 14));
        b1.setBounds(150, 250, 100, 30);
        b1.setBackground(new Color(18, 192, 207));
        b1.setForeground(Color.WHITE);

        resultLabel = new JLabel("");
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBounds(10, 180, 400, 60);

        // Fetch the list of currency codes and populate JComboBox components
        fetchCurrencyCodes();

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fromCurrencyCode = Cb1.getSelectedItem().toString();
                String toCurrencyCode = Cb2.getSelectedItem().toString();
                int amount = Integer.parseInt(tfAmount.getText());
                fetchCurrencyConversion(fromCurrencyCode, toCurrencyCode, amount);
            }
        });

        f.add(l);
        f.add(l2);
        f.add(l3);
        f.add(l4);
        f.add(a);
        f.add(Cb1);
        f.add(Cb2);
        f.add(tfAmount);
        f.add(b1);
        f.add(resultLabel);

        f.setVisible(true);
    }

    
    private void fetchCurrencyCodes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://currency-converter5.p.rapidapi.com/currency/list"))
                    .header("X-RapidAPI-Key", "684860b552msha1797bf178ae021p16262ajsn51f13be3c2d7")
                    .header("X-RapidAPI-Host", "currency-converter5.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                JSONObject json = new JSONObject(responseBody);

                if (json.has("currencies")) {
                    JSONObject currencies = json.getJSONObject("currencies");

                    java.util.Iterator<String> keys = currencies.keys();

                    while (keys.hasNext()) {
                        String currencyCode = keys.next();
                        Cb1.addItem(currencyCode);
                        Cb2.addItem(currencyCode);
                    }
                } else {
                    resultLabel.setText("Error: 'currencies' key not found in response");
                }
            } else {
                resultLabel.setText("Error: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    

    private void fetchCurrencyConversion(String from, String to, int amount) {
    	 try {
             HttpRequest request = HttpRequest.newBuilder()
                     .uri(URI.create("https://currency-converter5.p.rapidapi.com/currency/convert?format=json&from="
                             + from + "&to=" + to + "&amount=" + amount)) // Use the provided amount
                     .header("X-RapidAPI-Key", "684860b552msha1797bf178ae021p16262ajsn51f13be3c2d7")
                     .header("X-RapidAPI-Host", "currency-converter5.p.rapidapi.com")
                     .method("GET", HttpRequest.BodyPublishers.noBody())
                     .build();

             HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

             if (response.statusCode() == 200) {
                 String responseBody = response.body();
                 JSONObject json = new JSONObject(responseBody);
                 JSONObject rates = json.getJSONObject("rates");

                 if (rates.has(to)) {
                     String rateForConversion = rates.getJSONObject(to).getString("rate");
                     double exchangeRate = Double.parseDouble(rateForConversion);
                     double convertedAmount = amount * exchangeRate;
                     String formattedAmount = String.format("%.2f", convertedAmount); 
                     String formattedAmount2 = String.format("%.2f", exchangeRate); 
                     resultLabel.setText("Converted Amount: " + formattedAmount +"     "+"Rate of conversion: " + formattedAmount2 );
                     
                   

                 } else {
                     resultLabel.setText("Rate not found for the specified currency code: " + to);
                 }
             } else {
                 resultLabel.setText("Error: HTTP " + response.statusCode());
             }
         } catch (IOException | InterruptedException e) {
             e.printStackTrace();
         }
    }

    public static void main(String[] args) {
        new Main();
    }
}
