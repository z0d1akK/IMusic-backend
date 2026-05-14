package imusic.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "app.uploads")
@Getter
@Setter
public class AppUploadsProperties {

    private String baseDir = "src/main/resources/uploads";

    public Path basePath() {
        return Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public String resourceLocation() {
        String uri = basePath().toUri().toString();
        return uri.endsWith("/") ? uri : uri + "/";
    }
}
