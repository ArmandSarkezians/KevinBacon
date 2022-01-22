package ca.utoronto.utm.mcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * This class converts InputStreams to Strings
 */
public class Utils {

    /**
     * This method converts InputStreams to Strings
     * @param inputStream the Inputstream to be converted
     * @return the new converted String
     * @throws IOException
     */
    public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
