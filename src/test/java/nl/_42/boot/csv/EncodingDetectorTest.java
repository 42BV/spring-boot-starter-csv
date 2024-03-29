package nl._42.boot.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class EncodingDetectorTest {

    private EncodingDetector detector = new EncodingDetector();

    @Test
    public void getEncoding_ascii() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons.csv").getInputStream()) {
            String encoding = detector.getEncoding(is);
            Assertions.assertEquals("UTF-8", encoding);
        }
    }

    @Test
    public void getEncoding_ascii_invalid() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons-fail.csv").getInputStream()) {
            String encoding = detector.getEncoding(is);
            Assertions.assertEquals("UTF-8", encoding);
        }
    }

    @Test
    public void getEncoding_iso() throws IOException {
        try (InputStream is = new ClassPathResource("csv/person-iso.csv").getInputStream()) {
            String encoding = detector.getEncoding(is);
            Assertions.assertEquals("WINDOWS-1252", encoding);
        }
    }

}
