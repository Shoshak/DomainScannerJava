package dev.moto;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Scanner {
    private final Connection connection;
    private final DefaultListModel<String> model;
    private int handling;

    public Scanner(DefaultListModel<String> model) {
        this.connection = Jsoup.newSession().timeout(5000);
        this.model = model;
        this.handling = 0;
    }

    public void scan(List<String> domains, String siteName) throws URISyntaxException, IOException {
        this.handling++;
        int currentHandling = this.handling;
        for (String domain : domains) {
            String link = siteName + "." + domain;
            Thread.ofVirtual().start(() -> {
                try {
                    String title = this.connection.newRequest("http://" + link).get().title();
                    if (!title.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            if (currentHandling == this.handling) {
                                model.addElement(String.format("%s (%s)", link, title));
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            if (currentHandling == this.handling) {
                                model.addElement(link);
                            }
                        });
                    }
                } catch (IOException ignored) {}
            });
        }
    }
}
