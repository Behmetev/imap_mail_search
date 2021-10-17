import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SubjectTerm;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class MailSearch{
    public static void downloadCsv() throws IOException, MessagingException {

        FileInputStream fileInputStream = new FileInputStream("./src/config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);

        String user = properties.getProperty("mail.user");
        String password = properties.getProperty("mail.password");
        String host = properties.getProperty("mail.host");
        String sender = properties.getProperty("mail.sender");
        String subject = properties.getProperty("mail.subject");

        Properties prop = new Properties();
        prop.put("mail.store.protocol", "imaps");

        Store store = Session.getInstance(prop).getStore();
        store.connect(host, user, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        System.out.println("Всего сообщений: " + inbox.getMessageCount());

        //Message message = inbox.getMessage(inbox.getMessageCount());

        //System.out.println(message.getSubject());
        //System.out.println(message.getContent());
        //System.out.println(message.getDescription());

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        String bookingDate = sdf.format(date);
        //System.out.println(bookingDate);

        //String nowDate = date.getMonth() + "/" + date.getDate() + "/" + sdf.format(date);
        //System.out.println(nowDate);

        //Date date = new SimpleDateFormat("MM/dd/yy").parse(nowDate);
        System.out.println("Дата поиска: " + date);
        //System.out.println("Email для фильтрации: " + mailSender);

        //Поиск сегоднешниж писем
        final FromTerm fromTerm = new FromTerm(new InternetAddress(sender));
        //final SearchTerm tdDate = new ReceivedDateTerm(ComparisonTerm.EQ, date);
        final SubjectTerm subjectTerm = new SubjectTerm(subject);
        final AndTerm termsSummary = new AndTerm(fromTerm, subjectTerm);
        final Message[] foundMessages = inbox.search(termsSummary);

        System.out.println("Осталось сообщений после фильтрации: " + foundMessages.length);

        Multipart multipart = (Multipart) foundMessages[foundMessages.length - 1].getContent();
        // Вывод содержимого в консоль

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            //System.out.println(bodyPart.getContent());

            MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                System.out.println("Прикреплённый фаил: " + part.getFileName());

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
