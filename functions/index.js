/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions");
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

setGlobalOptions({maxInstances: 10});

exports.cambiarContrasena = functions.https.onCall(async (data, context) => {
  console.log("Data completa recibida:", data);
  console.log("Tipo de data:", typeof data);
  
 const payload = data.data || {};

  const email = typeof payload.email === "string" ? payload.email.trim() : "";
  const nuevaContra = typeof payload.nuevaContrasena === "string" ? payload.nuevaContrasena : "";
  
  console.log("Email:", email);
  console.log("NuevaContra:", nuevaContra);
   
  if (!email || !email.includes("@")) {
  return {success: false, error: "El correo es inválido o está vacío."};
}

  try {
    const user = await admin.auth().getUserByEmail(email);
    await admin.auth().updateUser(user.uid, {password: nuevaContra});
    return {success: true};
  } catch (error) {
    console.error("Error al cambiar contraseña:", error);
    return {success: false, error: error.message};
  }
});


// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.


// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
