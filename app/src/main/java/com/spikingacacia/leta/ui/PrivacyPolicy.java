package com.spikingacacia.leta.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.spikingacacia.leta.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivacyPolicy extends Preference
{
    private Preferences preferences;
    private Context context;
    public PrivacyPolicy(Context context)
    {
        super(context);
        setLayoutResource(R.layout.privacy_policy);
        this.context=context;
        preferences = new Preferences(context);
    }

    public PrivacyPolicy(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.privacy_policy);
        this.context=context;
        preferences = new Preferences(context);
    }
    public PrivacyPolicy(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.privacy_policy);
        this.context=context;
        preferences = new Preferences(context);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder view)
    {
        super.onBindViewHolder(view);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(makeSpannable(setText(), "<b>(.+?)</b>", "<b>", "</b>"));

    }
    String setText()
    {
        String text = "<b>Privacy and Data Protection Policy</b>\n" +
                "Last update: July 21, 2020\n" +
                "\n" +
                "<b>Introduction</b>\n" +
                " \n" +
                "This policy explains how we, Spiking acacia (hereinafter referred to as “Order”), use the personal information which you provide to us when using our service, including but not limited to our website and mobile applications (jointly referred as “Website”. Order may share the data of those users (“Users”) who register on the website or app (the “Platform”) and of those persons who contact Order using the forms available on its Platform with Spiking Acacia (‘the host company’) for the purpose of offering the services requested by Users through the Platform.\n" +
                "  \n" +
                "<b>Basic Information</b>\n" +
                "Identity\tOrder \n" +
                "Purpose\tManagement and provision of the services requested\n" +
                "Legitimisation\tCompliance with the contractual relationship, legitimate interest and User consent\n" +
                "Rights\tThe right to access, rectify and erase data, as well as other rights, as explained in the additional information.\n" +
                "Additional Information\tAdditional detailed information on Data Protection can be found in the following sections.\n" +
                "DPO Contact\tinfo@Order.spiking acacia.com\n" +
                "\n" +
                "<b>Controller</b>\n" +
                "Identity\t Order Application\n" +
                "Postal address\t P.O BOX 9640 – 00100 Nairobi\n" +
                "Contact \tinfo@Order.spiking acacia.com\n" +
                "\t\n" +
                "<b>Purpose</b>\n" +
                "<b>To use the Order Platform</b> \n" +
                "ORDER uses the data collected from Users to enable them to access and communicate with the ORDER platform and to provide the services requested by them through their account on the ORDER Platform, in accordance with the procedure described in the “Terms of Use”.\n" +
                "<b>To send communications</b>\n" +
                "ORDER uses Users’ personal data to communicate via e-mail and/or send them SMS messages relating to the operation of the service.\n" +
                "ORDER may send messages to the User’s mobile phone with information relating to the status of the order requested. When the order is completed, ORDER will send a summary/receipt of the order and price thereof to the User’s e-mail.\n" +
                "\n" +
                "<b>What information do we collect about you?</b>\n" +
                "•\t Registration Data: the information provided by Users when they create an account on the ORDER Platform: username and e-mail.\n" +
                "•\tUser Profile Information: the information added by Users on the Platform in order to be able to use the ORDER service; i.e. their mobile phone number and delivery address. Users can view and edit the personal data on their profile whenever they wish. ORDER does not store Users’ credit card details, but these are provided to licensed electronic payment service providers, who receive the data included directly and store it in order to facilitate the payment process for Users and to manage it on ORDER’s behalf. This information is under no circumstances stored on ORDER’s servers. Users may delete the details of the credit cards linked to their account at any time. This will trigger the service provider to delete the information, which will have to be re-entered or selected in order to place new orders through the Platform. Users may request such providers’ privacy policies at any time. \n" +
                "•\tAdditional information that Users wish to share: any information that a User could supply to ORDER for other purposes. Examples include a photograph of the User or the billing address in the case of Users who have asked to receive invoices from ORDER.\n" +
                "•\tInformation about previous communications with us: ORDER will have access to the information supplied by Users for the resolution of any queries or complaints about the use of the platform, whether through the contact form, by e-mail or by phone through the customer service.\n" +
                "•\tInformation on accidents involving any of the parties involved in the provision of services through the Platform for the purpose of making insurance claims or carrying out any other actions with the insurance companies contracted by ORDER.\n" +
                "•\tTranscription and recording of conversations held between the USER and ORDER for the processing of incidents, queries or any other consultations that may be made.\n" +
                "\n" +
                "We collect this information exclusively to carry out the functions offered on the Website and to provide you with offers and information about Order and other services we think you may be interested in. We might collect this personal information through: online food ordering; entry into competitions; subscribing to our newsletter; creating a user account; sending 'contact us' messages or other correspondence through the Website; or through advertising, research and direct marketing. We do not collect sensitive information about you.  \n" +
                "\n" +
                "<b>Cookies</b>\n" +
                " \n" +
                "Some of the information collected will not personally identify you but will instead track your use of the Website so that we can better understand how the Website is used by customers and in turn enhance and improve your experience in ordering food. We may obtain this information by use of cookies. Cookies are a small data file transferred to your device that recognizes and identifies your device and allows your device to 'remember' information from the Website for future use. Cookies do not contain any information that personally identifies you and we do not use cookies in order to obtain such information - your personal information can only be obtained by Order if you actively provide it to us. We may collect technical information from your mobile device or your use of our services through a mobile device, for example, location data and certain characteristics of, and performance data about your device, carrier/operating system including device and connection type, IP address, mobile payment methods, interaction with other retail technology such as use of NFC Tags, QR Codes or use of mobile vouchers. Your device and/or the web browser should allow you to refuse cookies if you wish by changing the settings. To find out more about cookies, including how to see what cookies have been set and how to manage and delete them, visit www.allaboutcookies.org.  \n" +
                "\n" +
                "<b>Storage and security of your personal information</b>\n" +
                "\n" +
                "Order will use all reasonable endeavors to maintain the security of your personal information and to protect your personal information from misuse, interference and loss and against unauthorized access, modification or disclosure. Order will destroy any personal information it holds about you which it no longer requires under the terms of this Privacy Policy. Where you have chosen a password to access certain services of the Website, you are responsible for keeping your password confidential. We recommend not sharing your password with anyone. Due to the nature of the internet, Order does not provide any guarantee or warranty regarding the security of your personal information during transmission to us or storage by us and you acknowledge that you disclose your personal information to us at your own risk. Please contact us immediately if you become aware or have reason to believe there has been any unauthorized use of your personal information in connection with the Website.  \n" +
                "\n" +
                "<b>How will we use the information we collect from you?</b>\n" +
                " \n" +
                "You consent to Order collecting and using your personal information for processing and delivering the order/s placed on the Website and any other service provided through the Website. In particular you expressly consent that Order may disclose your personal information, including your name, email address, physical address and telephone number to riders delivering your order, either employed by Order or by third parties providing delivery services to Order. You consent to Order using your personal information for advertising and direct marketing purposes in order to inform you about the Website and the goods and services it provides, or other matters that it believes will be of interest to you. Order may disclose your personal information, and you consent to the disclosure of your personal information, to:\n" +
                "other entities within Spiking Acacia; and\n" +
                "third parties engaged by Order or other members of the Order group to perform functions or provide products and services on our behalf such as processing payments, mail outs, debt collection, research, statistical information, marketing and direct or indirect advertising.\n" +
                "If you would like to unsubscribe from receiving direct marketing communications from Order or do not want us to share your personal information with other parties, please e-mail us at info@Order.spikingacacia.com follow the steps to unsubscribe which are presented in every communication you receive from us, requesting your personal information be removed from our mailing list. We will use reasonable endeavors to comply with your request within a reasonable period of receipt of your request. Please note that the withdrawal of any authorizations for processing of your personal information by third parties may result in us not being able to provide you with any services. Order reserves the right to disclose your personal information if it is required or authorized to do so by law, or, if it is reasonably necessary in its opinion to protect the rights or property of Order or any third party, or to avoid injury to any person. If the Order business is sold or merges with another entity then some or all of your personal information may be passed to a third party.  \n" +
                "\n" +
                "<b>Access to your information</b>\n" +
                " \n" +
                "Order will use all reasonable endeavors to keep personal information it holds accurate, complete, up-to-date, relevant and not misleading. Please contact us if you would like to access the personal information Order holds about you. We will use reasonable endeavors to provide a complete list of your personal information within a reasonable period of receipt of your request. Please email us if you would like to receive a copy of this information – info@Order.spikingacacia.com. Order reserves the right to charge a nominal fee for the processing of this request in accordance with local legislation. You may contact us to correct any of your personal information that is inaccurate, incomplete or out-of-date, or to request that your personal information be deleted. We will use reasonable endeavors to correct or delete your personal information as requested within a reasonable period of receipt of your request. Deletion of your account with Order will not automatically delete the personal information held about you. If you would like Order to delete all personal information together with the deletion of your account, please follow the steps mentioned-above. Please note that the deletion of your personal information from our database will result in us not being able to provide you with any services. If applicable, any legal requirement on us to maintain certain records of your personal information shall prevail over any of your requests. We may require you to prove your identity before providing you with copies of your personal information.  \n" +
                "<b>To detect and investigate fraud and possible criminal offences</b>\n" +
                "ORDER also uses the information to research and analyze how to improve the services it provides to Users, as well to develop and improve the features of the service it offers. Internally, ORDER uses the information for statistical purposes in order to analyze User behavior and trends, to understand how Users use the ORDER Platform and to manage and improve the services offered, including the possibility of adding new, different services to the Platform.\n" +
                "ORDER may monitor all actions that could result in fraud or in the commission of a criminal offence relating to the means of payment employed by users. ORDER may ask users for a copy of their ID card as well as for certain information on the credit card used to place the order. In any event, all data will be processed by ORDER for the sole purpose of fulfilling its fraud prevention and monitoring functions, and it shall be stored for as long as its relationship with the user concerned remains in force, and even after this time until the user’s right to make claims or take legal action relating to payment for the products or services ordered through ORDER has expired. The data relating to the credit card used will be retained until the incident has been resolved and for 120 days thereafter. If any irregularities in its use that could be considered illegal activities are detected, ORDER reserves the right to retain the data provided and to share it with the competent authorities in order to carry out the relevant investigation. ORDER may share the data with the authorities based on the legal obligation to prosecute conducts that are contrary to the applicable law.\n" +
                "<b>Changes to our Privacy Policy</b>\n" +
                " \n" +
                "Order reserves the right to alter all or any part of this Privacy Policy. Any changes thereto will be notified via the Website and, where appropriate, through e-mail notification.\n" +
                " \n" +
                "<b>Other Websites</b>\n" +
                " \n" +
                "Our Website may have links to other websites. This privacy policy only applies to the Website. You should therefore read the privacy policies of the other websites when you are using those sites.  \n" +
                "\n" +
                "<b>Contact</b>\n" +
                " \n" +
                "All comments, queries and requests relating to our use of your information are welcomed and should be addressed to:  info@Order.spiking acacia.com\n";
        return text;
    }
    private SpannableStringBuilder makeSpannable(String text, String regex, String startTag, String endTag) {

        StringBuffer sb = new StringBuffer();
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sb.setLength(0);
            String group = matcher.group();
            String spanText = group.substring(startTag.length(), group.length() - endTag.length());
            matcher.appendReplacement(sb, spanText);

            spannable.append(sb.toString());
            int start = spannable.length() - spanText.length();

            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        sb.setLength(0);
        matcher.appendTail(sb);
        spannable.append(sb.toString());
        return spannable;
    }
}
