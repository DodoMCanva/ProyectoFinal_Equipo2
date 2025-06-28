package modulos

import android.os.StrictMode
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(
    private val usuario: String,
    private val password: String
) {

    fun enviarCorreo(destino: String, asunto: String, mensaje: String) {
        // Esto no es recomendado, pero para evitar NetworkOnMainThreadException en ejemplo pequeño:
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(usuario, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(usuario))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino))
                setSubject(asunto)
                setText(mensaje)
            }
            Transport.send(message)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}
