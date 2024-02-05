// package com.expample
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
    def file_path

    SendMailer(String configFile, String privateToken,String file_path){
        this.configFile = configFile
        this.file_path = file_path

        loadConfig(privateToken,file_path)

       
    }
    @NonCPS
    private void loadConfig(privateToken,file_path) {
        getRecipients(privateToken,file_path)
        def slurper = new JsonSlurper()
        def config = slurper.parse(new File(configFile))
        def recipients = recipientsFile
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
        this.subject = "Error build"
        sendMail("Error: $mailMessage")
    }
    @NonCPS
    private void getRecipients(privateToken,file_path){
        def project_id = '101'
        def branch_name = 'master'
      
        def url = new URL('http://git.ru/api/v4/projects/' + project_id + '/repository/files/' + file_path + '/raw?ref=' + branch_name)

        def connection = url.openConnection()
        connection.setRequestProperty('PRIVATE-TOKEN', privateToken)
        def responseCode = connection.responseCode

        if (responseCode == 200) {
            def json_data = new JsonSlurper().parseText(connection.content.text)
            recipientsFile = json_data
            // println(recipientsFile)
        } else {
            println("Error get  ${responseCode}")
        }

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
