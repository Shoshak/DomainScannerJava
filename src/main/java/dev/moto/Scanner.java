package dev.moto;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.List;

public class Scanner {
    private final DefaultListModel<String> model;

    public Scanner(DefaultListModel<String> model) {
        this.model = model;
    }

    public void scan(List<String> domains, String siteName) throws URISyntaxException, IOException {
        for (String domain : domains) {
            String link = String.format("%s.%s", siteName, domain);
            Thread.ofVirtual().start(() -> {
                try {
                    InetAddress address = InetAddress.getByName(link);
                    if (address != null) {
                        model.addElement(link);
                    }
                } catch (IOException ignored) {}
            });
        }
    }
}
