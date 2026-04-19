package in.scalive.votezy.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Component
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        System.out.println("Firebase Path = " + credentialsPath);

        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException("firebase.credentials.path is not configured");
        }

        String finalPath = credentialsPath.startsWith("classpath:") || credentialsPath.startsWith("file:")
                ? credentialsPath
                : "file:" + credentialsPath;

        Resource resource = resourceLoader.getResource(finalPath);

        try (InputStream inputStream = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");
        }
    }
}