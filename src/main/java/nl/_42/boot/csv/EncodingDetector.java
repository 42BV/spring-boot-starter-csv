package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class EncodingDetector {

    private static final String DEFAULT_ENCODING = "UTF-8";

    public String getEncoding(InputStream is) throws IOException {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }

        is.mark(Integer.MIN_VALUE);
        String detected = UniversalDetector.detectCharset(is);
        is.reset();

        String encoding = StringUtils.defaultIfBlank(detected, DEFAULT_ENCODING);
        if (encoding.contains("ASCII")) {
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }

}
