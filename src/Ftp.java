import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class Ftp {

    private static final int BUFFER_SIZE = 4096;

    public static void main() throws IOException {

        FileInputStream fileInputStream = new FileInputStream("./src/config.properties");

        Properties properties = new Properties();
        properties.load(fileInputStream);

        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        String host = "mister-velo.ru";
        String user = properties.getProperty("ftp.user");
        String pass = properties.getProperty("ftp.password");
        String filePath = "tmp\\spisok-tovarov.csv";
        String uploadPath = "/public_html/csv_catalog/spisok-tovarov.csv";

        ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
        System.out.println("Upload URL: " + ftpUrl);

        try {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(filePath);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("File uploaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}