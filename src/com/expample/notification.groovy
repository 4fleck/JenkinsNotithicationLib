package com.expample

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.net.URL
import java.net.HttpURLConnection

class TelegramNotification {
    def botToken
    def chatId


    TelegramNotification(String botToken, String chatId) {
        this.botToken = botToken
        this.chatId = chatId
    }

    def successfulMessage(message){
        postMessage("\u2705 $message")
    }
    def errorMessage(message){ 
        postMessage("\u274c $message")
    }
    def logMessage(message){
        postMessage( "\u2712 $message")
    }
    def mobilemessage(message) {
        message = "Mobile $message"
        return message
    }

    private void postMessage(message) {
        def url = new URL("https://api.telegram.org/bot${botToken}/sendMessage?chat_id=${chatId}")
        def connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        def requestBody = JsonOutput.toJson([
            text: message
        ])

        def outputStream = connection.outputStream
        outputStream.write(requestBody.getBytes("UTF-8"))
        outputStream.flush()
        outputStream.close()

        def responseCode = connection.responseCode
        def responseMessage = connection.responseMessage

        if (responseCode == HttpURLConnection.HTTP_OK) {
            def inputStream = connection.inputStream
            def responseBody = inputStream.text
            inputStream.close()

            println "Response: $responseCode $responseMessage"
            println "Body: $responseBody"
        } else {
            println "Error: $responseCode $responseMessage"
        }

        connection.disconnect()
    }
}
