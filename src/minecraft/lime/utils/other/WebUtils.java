package lime.utils.other;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class WebUtils {

    public static boolean a = false;

    public static String getSource(String url) throws IOException
    {
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        return toString(urlConnection.getInputStream());
    }

    public static String post(String url, String post, String... headers) throws Exception {
        URL urlObject = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConnection.setRequestProperty("Cookie", "PHPSESSID=ae");
        urlConnection.setInstanceFollowRedirects(true);
        for (String header : headers) {
            urlConnection.setRequestProperty(header.split(":")[0], header.split(":")[1]);
        }
        urlConnection.setDoOutput(true);
        byte[] b = post.getBytes();
        urlConnection.setFixedLengthStreamingMode(b.length);
        urlConnection.connect();
        try(OutputStream os = urlConnection.getOutputStream()) {
            os.write(b);
        }
        return toString(urlConnection.getInputStream());
    }

    public static String toString(InputStream is) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }
}
