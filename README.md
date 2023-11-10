# jenksLib
подключение и использование библиотеки в самом pipeline :

```@Library('jenkinsBotLib')
import com.expample.notification
import com.expample.mailsender

def bot = withCredentials([usernamePassword(
                credentialsId: 'tgbot', 
                passwordVariable: 'botToken', 
                usernameVariable: 'chatId'
                )]) {
                 new notification(botToken,chatId)
            }


prefix = "ProjectName($BUILD_NUMBER)"
def sendSusseful =  {message -> bot.successfulMessage("${prefix} ${message}")}
def sendError  = {message -> bot.errorMessage("${prefix} ${message}")}
def sendLog =  {message ->bot.logMessage("${prefix} ${message}")}

def sender = {msg -> def mail =  withCredentials([file(credentialsId: 'configFile', variable: 'configFile')]){
                new mailsender(configFile,'/var/jenkins_home/workspace/test/confmailer/recipientsmail.json')
            }
        def sendMailSuccessful = {message -> mail.successfulMessage("${prefix} ${message}")}
        def sendMailError  = {message -> mail.errorMessage("${prefix} ${message}")}    
        
        return [sendMailSuccessful:sendMailSuccessful , sendMailError:sendMailError ]
}
```


вызов функций : 
```
//telegarm
 sendSusseful("успешное выполнение2")
 //mail
sender().sendMailSuccessful("sdfsdfd")
```
