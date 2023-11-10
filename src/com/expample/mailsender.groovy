package com.expample
@Grab('javax.mail:javax.mail-api:1.6.2')
@Grab('com.sun.mail:javax.mail:1.6.2')
@Grab('org.codehaus.groovy:groovy-json:3.0.9')

import javax.mail.*
import javax.mail.internet.*
import groovy.json.JsonSlurper


class SendMailer {
    def configFile
    def recipientsFile
    def username
    def password
    def host
    def port
    def from
    def to
    def subject
    def config

    SendMailer(String configFile, String recipientsFile){
        this.configFile = configFile
        this.recipientsFile = recipientsFile
        loadConfig()
       
    }
    @NonCPS
    private void loadConfig() {
        def slurper = new JsonSlurper()
        def config = slurper.parse(new File(configFile))
        def recipients = slurper.parse(new File(recipientsFile))
        this.username = config.username
        this.password = config.password
        this.host = config.host
        this.port = config.port
        this.from = config.from
        this.to = recipients.to.join(", ")
        this.subject = config.subject
    }

    def successfulMessage(mailMessage){
        this.subject = "Successful build"
        sendMail("Successful $mailMessage")
    }

    def errorMessage(mailMessage){
        sendMail("Error: $mailMessage")
    }

    private void sendMail(mailMessage){
        try {
            // Создание свойств для подключения к SMTP серверу
            Properties props = new Properties()
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.host", host)
            props.put("mail.smtp.port", port)

            // Создание аутентификатора
            Authenticator auth = new Authenticator() {
                @NonCPS
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password)
                }
            }

            // Создание сессии
            Session session = Session.getInstance(props, auth)

            // Создание объекта сообщения
            MimeMessage message = new MimeMessage(session)
            message.setFrom(new InternetAddress(from))

            // Добавление адресатов
            def recipients = to.split(",") // Разделение адресатов по запятой
            recipients.each { recipient ->
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()))
            }

            message.setSubject(subject)
            message.setText(mailMessage)

            // Отправка сообщения
            Transport.send(message)

            println("Successful sending of mail")
        } catch (MessagingException e) {
            println("Error sending of mail: ${e.message}")
        }
    }
}
