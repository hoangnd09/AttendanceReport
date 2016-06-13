package soosan.kr.co.attendancereport.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import soosan.kr.co.attendancereport.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {

    private String smtp_host = "mail.soosan.co.kr";
    private String smtp_port = "25";
    private String smtp_auth = "false";
    private String smtp_username = "hoangnguyen";
    private String smtp_password = "qwe123";

    private String sender = "hoangnguyen@soosan.co.kr";
    private String recipient = "hoangnguyen@soosan.co.kr";
    // private String recipient = "maichi@soosan.co.kr";

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final Button sendAttendance = (Button) this.getActivity().findViewById(R.id.btn_attending);
        sendAttendance.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            String currentDateTimeString = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US).format(new Date());
            String subject = "[Attending Report] " + currentDateTimeString;
            sendMail(subject);
            }});

        final Button sendLeave = (Button) this.getActivity().findViewById(R.id.btn_leaving);
        sendLeave.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            String currentDateTimeString = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US).format(new Date());
            String subject = "[Leaving Report] " + currentDateTimeString;
            sendMail(subject);
        } });

        return view;
    }

    private void sendMail (String subject) {
        // ProgressDialog dialog = ProgressDialog.show(this.getActivity().getApplicationContext(), "Sending Email", "Sending Email");
        try {
            sendMail(subject, "Regards");
            Toast.makeText(this.getActivity().getApplicationContext(), "sending success", Toast.LENGTH_LONG).show();
        } catch (MessagingException e) {
            Log.e("Send Mail", "sending failure", e);
            Toast.makeText(this.getActivity().getApplicationContext(), "sending failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Exception ex = e.getNextException();
            if (ex != null & ex.getMessage() != null) {
                Toast.makeText(this.getActivity().getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } finally {
            // dialog.dismiss();
        }
    }

    private void sendMail (String subject, String content) throws MessagingException {
        // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
        Properties props = new Properties();
        props.put("mail.smtp.host", this.smtp_host);
        props.put("mail.smtp.port", this.smtp_port);
        props.put("mail.smtp.auth", this.smtp_auth);
        props.put("mail.smtp.socketFactory.port", this.smtp_port);
        props.put("mail.smtp.socketFactory.class", "mail.smtp.socketFactory");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(AttendanceFragment.this.smtp_username, AttendanceFragment.this.smtp_password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(this.sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.recipient));
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(this.sender));

        message.setSubject(subject);
        message.setText(content);
        message.setSentDate(new Date());

        Transport.send(message);
    }
}
