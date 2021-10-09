import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, MessagingException {
        FileInputStream fileInputStream = new FileInputStream("src/config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);

        String user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");

        Properties prop = new Properties();
        prop.put("mail.store.protocol", "imaps");

        Store store = Session.getInstance(prop).getStore();
        store.connect(host, user, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        System.out.println("Всего сообщений: " + inbox.getMessageCount());

        Message message = inbox.getMessage(inbox.getMessageCount());

        System.out.println(message.getSubject());
        System.out.println(message.getContent());
        //System.out.println(message.getDescription());

        Multipart multipart = (Multipart) message.getContent();

        // Вывод содержимого в консоль
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);


            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                System.out.println(part.getFileName());

                // save an attachment from a MimeBodyPart to a file
                String destFilePath = "tmp/" + part.getFileName();

                FileOutputStream output = new FileOutputStream(destFilePath);

                InputStream input = part.getInputStream();

                byte[] buffer = new byte[4096];

                int byteRead;

                while ((byteRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, byteRead);
                }
                output.close();
            }
        }
    }
}
