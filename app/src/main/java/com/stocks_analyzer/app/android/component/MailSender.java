package com.stocks_analyzer.app.android.component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * メール送信に関するクラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class MailSender {

    /**
     * メール送信エラー例外クラス.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public class SenderException extends Exception {
        public SenderException(Exception e) {
            super(e);
        }
        private static final long serialVersionUID = 1L;
    };

    /** 送信者/別名リスト */
    private Hashtable<String, String> listTo = new Hashtable<String, String>();
    /** CC/別名リスト */
    private Hashtable<String, String> listCC = new Hashtable<String, String>();
    /** BCC/別名リスト */
    private Hashtable<String, String> listBCC = new Hashtable<String, String>();
    /** 送信者/別名 */
    private Hashtable<String, String> pearFrom = new Hashtable<String, String>(1);
    /** 件名 */
    private String _Subject = "Subject";
    /** 本文 */
    private String _Body = "Body";
    /** 添付ファイル */
    private List<String> listFilePath = new ArrayList<String>();

    /** エンコード 定義*/
    private final String Encode_ISO2022 = "ISO-2022-JP";
    /** エンコード */
    private String _AddressAliasEncode = Encode_ISO2022;
    /** エンコード */
    private String _SubjectEncode = Encode_ISO2022;
    /** エンコード */
    private String _TextEncode = Encode_ISO2022;

    /** デバッグ */
    private boolean _Dubug = false;
    /** SMTPサーバ */
    private String _SMTP = "localhost";
    /** ポート */
    private int _Port = 25;
    /** SSL */
    private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    /** 認証 */
    private boolean _Auth = false;
    /** 認証ID */
    private String _AuthUserName = "";
    /** 認証パスワード */
    private String _AuthPassWord = "";

    /**
     * 発信者をセットする.
     *
     * @param sFrom 発信者アドレス
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void setFromAddress(String sFrom) {
        pearFrom.clear();
        pearFrom.put(sFrom, "");
    }

    /**
     * 発信者をセットする.
     *
     * @param sFrom 送信者アドレス
     * @param sAlias 送信者別名
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void setFromAddress(String sFrom, String sAlias) {
        pearFrom.clear();
        pearFrom.put(sFrom, sAlias);
    }

    /**
     * 送信者を追加する.
     *
     * @param sTo 送信者アドレス
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addToAddress(String sTo) {
        listTo.put(sTo, "");
    }

    /**
     * 送信者を追加する.
     *
     * @param sTo 送信者アドレス
     * @param sAlias 送信者別名
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addToAddress(String sTo, String sAlias) {
        listTo.put(sTo, sAlias);
    }

    /**
     * CCを追加する.
     *
     * @param sCC CCアドレス
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addCCAddress(String sCC) {
        listCC.put(sCC, "");
    }

    /**
     * CCを追加する.
     *
     * @param sCC CCアドレス
     * @param sAlias CC別名
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addCCAddress(String sCC, String sAlias) {
        listCC.put(sCC, sAlias);
    }

    /**
     * BCCを追加する.
     *
     * @param sBCC BCCアドレス
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addBCCAddress(String sBCC) {
        listBCC.put(sBCC, "");
    }

    /**
     * BCCを追加する.
     *
     * @param sBCC BCCアドレス
     * @param sAlias BCC別名
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addBCCAddress(String sBCC, String sAlias) {
        listBCC.put(sBCC, sAlias);
    }

    /**
     * 件名をセットする.
     *
     * @param sSubject 件名
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void setSubject(String sSubject) {
        this._Subject = sSubject;

    }

    /**
     * 本文を追加する.
     *
     * @param sBody 本文
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void setBody(String sBody) {
        this._Body = sBody;
    }

    /**
     * 添付ファイルpathリスト.
     *
     * @param sFilePath
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void addAttachment(String sFilePath) {
        listFilePath.add(sFilePath);
    }

    /**
     * コンストラクタ.
     * <p>デフォルト値
     * <p>SMTP：localhost
     * <p>Port：25
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public MailSender(){}

    /**
     * コンストラクタ.
     * <p>デフォルト値
     * <p>Port：25
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public MailSender(String sSMTP) {
        this._SMTP = sSMTP;
    }

    /**
     * コンストラクタ.
     *
     * @param sSMTP SMTPサーバ
     * @param iPort ポート
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public MailSender(String sSMTP, int iPort) {
        this._SMTP = sSMTP;
        this._Port = iPort;
    }

    /**
     * コンストラクタ.
     *
     * @param sSMTP SMTP
     * @param iPort ポート
     * @param sUserName 認証ID
     * @param sPassWord 認証パスワード
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public MailSender(String sSMTP, int iPort, String sUserName, String sPassWord) {
        this._SMTP = sSMTP;
        this._Port = iPort;

        this._Auth = true;
        this._AuthUserName = sUserName;
        this._AuthPassWord = sPassWord;
    }

    /**
     * 送信定義生成.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private Session getInitSession() {
        Properties oProps = new Properties();
        oProps.put("mail.smtp.host", _SMTP);
        oProps.put("mail.smtp.port", String.valueOf(_Port));
        oProps.put("mail.smtp.auth", String.valueOf(_Auth));
        oProps.put("mail.smtp.debug", String.valueOf(_Dubug));

        Session oSession = null;
        if (_Auth == true) {
            oProps.put("mail.smtp.socketFactory.port", String.valueOf(_Port));
            oProps.put("mail.smtp.socketFactory.class", SSL_FACTORY);
            oProps.put("mail.smtp.socketFactory.fallback", String.valueOf(false));

            oSession = Session.getDefaultInstance(oProps, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return (new PasswordAuthentication(_AuthUserName, _AuthPassWord));
                }
            });
        } else {
            oSession = Session.getDefaultInstance(oProps);
        }
        oSession.setDebug(_Dubug);

        return (oSession);
    }

    /**
     * メールアドレス定義.
     *
     * @param sAddress メールアドレス
     * @param sAlias 別名
     * @return メールアドレスオブジェクト
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private InternetAddress setAddress(String sAddress, String sAlias) throws AddressException {
        try {
            return (new InternetAddress(sAddress, sAlias, _AddressAliasEncode));
        } catch (UnsupportedEncodingException e) {
            return (new InternetAddress(sAddress));
        }
    }

    /**
     * 発信者メールアドレス定義.
     *
     * @return メールアドレスオブジェクト
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private Address getFromAddress() throws AddressException {
        Enumeration<String> Keys = pearFrom.keys();
        if (Keys.hasMoreElements()) {
            String sAddress = String.valueOf(Keys.nextElement());
            String sAlias = String.valueOf(pearFrom.get(sAddress));
            return (setAddress(sAddress, sAlias));
        } else {
            return (new InternetAddress());
        }

    }

    /**
     * 送信者メールアドレス定義リスト.
     *
     * @return メールアドレスオブジェクト
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private Address[] getToAddressList() throws AddressException {
        InternetAddress[] listAddress = new InternetAddress[listTo.size()];
        Enumeration<String> list = listTo.keys();

        int iIndex = 0;
        while (list.hasMoreElements()) {
            String sAddress = String.valueOf(list.nextElement());
            String sAlias = String.valueOf(listTo.get(sAddress));
            listAddress[iIndex] = setAddress(sAddress, sAlias);
            iIndex++;
        }

        return (listAddress);
    }

    /**
     * CCメールアドレス定義リスト.
     *
     * @return メールアドレスオブジェクト
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private Address[] getCCAddressList() throws AddressException {
        InternetAddress[] listAddress = new InternetAddress[listCC.size()];
        Enumeration<String> list = listCC.keys();

        int iIndex = 0;
        while (list.hasMoreElements()) {
            String sAddress = String.valueOf(list.nextElement());
            String sAlias = String.valueOf(listCC.get(sAddress));
            listAddress[iIndex] = setAddress(sAddress, sAlias);
            iIndex++;
        }

        return (listAddress);
    }

    /**
     * BCCメールアドレス定義リスト.
     *
     * @return メールアドレスオブジェクト
     * @throws AddressException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private Address[] getBCCAddressList() throws AddressException {
        InternetAddress[] listAddress = new InternetAddress[listBCC.size()];
        Enumeration<String> list = listBCC.keys();

        int iIndex = 0;
        while (list.hasMoreElements()) {
            String sAddress = String.valueOf(list.nextElement());
            String sAlias = String.valueOf(listBCC.get(sAddress));
            listAddress[iIndex] = setAddress(sAddress, sAlias);
            iIndex++;
        }

        return (listAddress);
    }

    /**
     * メール送信する.
     *
     * @throws MessagingException
     * @throws AddressException
     * @throws SenderException
     * @throws IOException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void sendMail() throws AddressException, MessagingException, SenderException, IOException {
        //送信設定
        MimeMessage mimeMsg = new MimeMessage(getInitSession());
        mimeMsg.setHeader("charset", _TextEncode);
        mimeMsg.addHeader("Content-Transfer-Encoding", "8bit");

        mimeMsg.setFrom(getFromAddress());
        mimeMsg.setRecipients(Message.RecipientType.TO, getToAddressList());
        mimeMsg.setRecipients(Message.RecipientType.CC, getCCAddressList());
        mimeMsg.setRecipients(Message.RecipientType.BCC, getBCCAddressList());

        //送信件名
        String encodeSubject = MimeUtility.encodeText(_Subject, _SubjectEncode, "B");
        mimeMsg.setSubject(encodeSubject, _SubjectEncode);
        mimeMsg.setSentDate(new Date());

        //本文
        String encodeText = MimeUtility.encodeText(_Body, _TextEncode, "B");
        mimeMsg.setContent(encodeText, "text/plain; charset=ISO-2022-JP");
        mimeMsg.setText(new String(_Body.getBytes(_TextEncode)), _TextEncode);

        //送信処理
        sendMail(mimeMsg);
    }

    /**
     * メール送信する.
     *
     * @param msg メッセージオブジェクト
     * @throws SenderException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private void sendMail(MimeMessage msg) throws SenderException {
        try {
            Transport.send(msg);
        } catch (Exception e) {
            throw (new SenderException(e));
        }
    }
}