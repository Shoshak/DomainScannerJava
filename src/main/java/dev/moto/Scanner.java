package dev.moto;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.List;

public class Scanner {
    private final Connection connection;
    private final DefaultListModel<String> model;

    public Scanner(DefaultListModel<String> model) {
        this.connection = Jsoup.newSession().timeout(5000);
        this.model = model;
    }

    public void scan(List<String> domains, String siteName) throws URISyntaxException, IOException {
        for (String domain : domains) {
            String link = siteName + "." + domain;
            Thread.ofVirtual().start(() -> {
                try {
                    InetAddress address = InetAddress.getByName(link);
                    if (address == null) return;

                    String title = this.connection.newRequest("http://" + link).get().title();
                    if (!title.isEmpty()) {
                        SwingUtilities.invokeLater(() -> model.addElement(String.format("%s (%s)", link, title)));
                    } else {
                        SwingUtilities.invokeLater(() -> model.addElement(link));
                    }
                } catch (IOException ignored) {}
            });
        }
    }
}
