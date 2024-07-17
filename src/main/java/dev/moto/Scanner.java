package dev.moto;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class Scanner {
    private final HttpClient client;
    private final DefaultListModel<String> model;

    public Scanner(DefaultListModel<String> model) {
        this.client = HttpClient
                .newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.model = model;
    }

    public void scan(List<String> domains, String siteName) throws URISyntaxException {
        for (String domain : domains) {
            String link = String.format("http://%s.%s", siteName, domain);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(new URI(link))
                    .HEAD()
                    .build();
            client
                    .sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(r -> {
                        int status = r.statusCode();
                        if (status < 200 || status > 299) return;
                        model.addElement(r.uri().toString());
                    });
        }
    }
}
