package br.gov.mme.auth.dto;

import br.gov.mme.auth.exceptions.EmailException;
import br.gov.mme.auth.util.CollectionUtil;
import br.gov.mme.auth.util.Util;
import org.apache.commons.lang3.StringUtils;
import javax.mail.Multipart;
import javax.mail.Session;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
public class EmailUtil {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("email-config");
    private static final String USER_NAME = RESOURCE_BUNDLE.getString("mail.user");
    private static final String PASSWORD = RESOURCE_BUNDLE.getString("mail.password");
    private static final String HOST_NAME = RESOURCE_BUNDLE.getString("mail.host");
    private static final String SMTP_PORT = RESOURCE_BUNDLE.getString("mail.smtp-port");
    private static final String DEFAULT_REMITTER_ADDRESS = RESOURCE_BUNDLE.getString("mail.default-remitter-address");
    private static final String DEFAULT_CHARSET = RESOURCE_BUNDLE.getString("mail.default-charset");

    private String remitterAddress;
    private Set<String> addressesTO;
    private Set<String> addressesCC;
    private Set<String> addressesBCC;
    private Map<String, byte[]> attachments;
    private Map<String, byte[]> attachmentsImage;

    private String charset;
    private String subject;
    private String body;

    /**
     * Constructor classe.
     */
    public EmailUtil() {
        this.attachments = new HashMap<String, byte[]>();
        this.attachmentsImage = new HashMap<String, byte[]>();
        this.addressesTO = new LinkedHashSet<>();
        this.addressesCC = new LinkedHashSet<String>();
        this.addressesBCC = new LinkedHashSet<String>();
    }

    /**
     * Add addressTO.
     *
     * @param addressTO
     * @return
     */
    public EmailUtil addAddressTO(final String addressTO) {
        if (!StringUtils.isBlank(addressTO)) {
            addressesTO.add(addressTO.toLowerCase().trim());
        }
        return this;
    }

    /**
     * Add all addressesTO.
     *
     * @param addressesTO
     * @return
     */
    public EmailUtil addAllAddressTO(final List<String> addressesTO) {
        if (!CollectionUtil.isEmpty(addressesTO)) {
            this.addressesTO.addAll(addressesTO);
        }
        return this;
    }

    /**
     * @return the addressesTO
     */
    public Set<String> getAddressesTO() {
        return addressesTO;
    }

    /**
     * Add addressCC.
     *
     * @param addressCC
     * @return
     */
    public EmailUtil addAddressCC(final String addressCC) {
        if (!StringUtils.isBlank(addressCC)) {
            addressesCC.add(addressCC.toLowerCase().trim());
        }
        return this;
    }

    /**
     * Add all addressesCC.
     *
     * @param addressesCC
     * @return
     */
    public EmailUtil addAllAddressCC(final List<String> addressesCC) {
        if (!CollectionUtil.isEmpty(addressesTO)) {
            addressesCC.addAll(addressesCC);
        }
        return this;
    }

    /**
     * @return the addressesCC
     */
    public Set<String> getAddressesCC() {
        return addressesCC;
    }

    /**
     * Add addressBCC.
     *
     * @param addressBCC
     * @return
     */
    public EmailUtil addAddressBCC(final String addressBCC) {
        if (!StringUtils.isBlank(addressBCC)) {
            addressesBCC.add(addressBCC.toLowerCase().trim());
        }
        return this;
    }

    /**
     * Add all addressesBCC.
     *
     * @param addressesBCC
     * @return
     */
    public EmailUtil addAllAddressBCC(final List<String> addressesBCC) {
        if (!CollectionUtil.isEmpty(addressesBCC)) {
            addressesBCC.addAll(addressesBCC);
        }
        return this;
    }

    /**
     * @return the addressesBCC
     */
    public Set<String> getAddressesBCC() {
        return addressesBCC;
    }

    /**
     * Add attachments image.
     *
     * @param cid
     * @return
     */
    public EmailUtil addAttachmentImage(final String cid, byte[] data) {
        if (!Util.isEmpty(cid) && data != null) {
            attachmentsImage.put(cid, data);
        }
        return this;
    }

    /**
     * Add attachments.
     *
     * @param name
     * @return
     */
    public EmailUtil addAttachment(final String name, byte[] data) {
        if (!Util.isEmpty(name) && data != null) {
            attachments.put(name, data);
        }
        return this;
    }

    /**
     * @param charset the charset to set
     * @return
     */
    public EmailUtil setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return Util.isEmpty(charset) ? DEFAULT_CHARSET : charset;
    }

    /**
     * @return the remitterAddress
     */
    public String getRemitterAddress() {
        return Util.isEmpty(remitterAddress) ? DEFAULT_REMITTER_ADDRESS : remitterAddress;
    }

    /**
     * @param remitterAddress the remitterAddress to set
     * @return
     */
    public EmailUtil setRemitterAddress(String remitterAddress) {
        this.remitterAddress = remitterAddress;
        return this;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     * @return
     */
    public EmailUtil setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return Util.isEmpty(body) ? " " : body;
    }

    /**
     * @param body the body to set
     * @return
     */
    public EmailUtil setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Realiza a validação dos atributos associados ao e-mail.
     *
     * @throws EmailException
     */
    private void validateParams() throws EmailException {
        List<String> invalidAddresses = new ArrayList<String>();

        if (addressesTO.isEmpty() && addressesCC.isEmpty()) {
            throw new EmailException("Email must have at least one recipient.");
        }

        /* Charset */
        if (this.charset != null) {
            try {
                Charset.forName(charset);
            } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
                throw new EmailException("Charset invalid (" + getCharset() + ")");
            }
        }

        /* TO */
        for (String address : getAddressesTO()) {
            if (!Util.isValidMail(address)) {
                invalidAddresses.add(address);
            }
        }

        /* CC */
        for (String address : addressesCC) {
            if (!Util.isValidMail(address)) {
                invalidAddresses.add(address);
            }
        }

        /* BCC */
        for (String address : addressesBCC) {
            if (!Util.isValidMail(address)) {
                invalidAddresses.add(address);
            }
        }

        if (!CollectionUtil.isEmpty(invalidAddresses)) {
            throw new EmailException("Invalid Recipients: " + CollectionUtil.getCollectionAsString(invalidAddresses)
                    + ". Please add valid email addresses.");
        }
    }

    /**
     * Sends the email according to the given parameters.
     *
     * @return
     * @throws EmailException
     */
    public void send() throws EmailException {
        try {
            validateParams();

            String charset = getCharset();
            Session session = getSession();

            Multipart content = new MimeMultipart();

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, "text/html; charset=" + charset);
            content.addBodyPart(bodyPart);

            for (String attachmentsName : attachments.keySet()) {
                byte[] data = attachments.get(attachmentsName);

                MimeBodyPart attachmentPart = getAttachmentPart(attachmentsName, data);
                content.addBodyPart(attachmentPart);
            }

            for (String cid : attachmentsImage.keySet()) {
                byte[] data = attachmentsImage.get(cid);

                MimeBodyPart attachmentPart = getAttachmentImagePart(cid, data);
                content.addBodyPart(attachmentPart);
            }

            Message msg = new MimeMessage(session);
            msg.setSentDate(new Date());
            msg.setContent(content);

            String subject = MimeUtility.encodeText(this.subject, charset, null);
            msg.setSubject(subject);

            String remitterAddress = getRemitterAddress();
            msg.setFrom(new InternetAddress(remitterAddress));

            InternetAddress[] addressesTO = getInternetAdresses(this.addressesTO);
            msg.setRecipients(Message.RecipientType.TO, addressesTO);

            if (!CollectionUtil.isEmpty(addressesCC)) {
                InternetAddress[] addressesCC = getInternetAdresses(this.addressesCC);
                msg.setRecipients(Message.RecipientType.CC, addressesCC);
            }

            if (!CollectionUtil.isEmpty(addressesBCC)) {
                InternetAddress[] addressesBCC = getInternetAdresses(this.addressesBCC);
                msg.setRecipients(Message.RecipientType.BCC, addressesBCC);
            }

            Transport.send(msg);
        } catch (Exception e) {
            throw new EmailException(e.getMessage(), e);
        }
    }

    /**
     * Retorna a instância de {@link MimeBodyPart} conforme os parâmetros
     * informados.
     *
     * @param name
     * @param data
     * @return
     * @throws MessagingException
     */
    private MimeBodyPart getAttachmentPart(String name, byte[] data) throws MessagingException {
        MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, mimeTypes.getContentType(name));

        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(dataSource));
        part.setFileName(name);
        return part;
    }

    /**
     * Returns an instance of {@link MimeBodyPart} according to the given
     * parameters.
     *
     * @param cid
     * @param data
     * @return
     * @throws MessagingException
     */
    private MimeBodyPart getAttachmentImagePart(String cid, byte[] data) throws MessagingException {
        MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, mimeTypes.getContentType(cid));

        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(dataSource));
        part.setHeader("Content-ID", "<" + cid.trim() + ">");
        return part;
    }

    /**
     * Returns the instance of {@link Session} as per the email configuration
     * parameters.
     *
     * @return
     */
    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", HOST_NAME);
        properties.put("mail.smtp.port", Integer.parseInt(SMTP_PORT));
        properties.put("mail.smtp.auth", Boolean.TRUE.toString());
        properties.put("mail.smtp.starttls.enable", Boolean.TRUE.toString());

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, PASSWORD);
            }
        };
        return Session.getInstance(properties, auth);
    }

    /**
     * Returns the array of {@link InternetAddress} according to the list of
     * recipients.
     *
     * @param recipients
     * @return
     * @throws AddressException
     */
    private InternetAddress[] getInternetAdresses(final Set<String> recipients) throws AddressException {
        int index = 0;
        InternetAddress[] addresses = new InternetAddress[recipients.size()];

        for (String endereco : recipients) {
            addresses[index++] = new InternetAddress(endereco);
        }
        return addresses;
    }

}