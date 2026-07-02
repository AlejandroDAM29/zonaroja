const {onRequest} = require("firebase-functions/https");
const {initializeApp} = require("firebase-admin/app");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

exports.sendTestTopic = onRequest(async (req, res) => {
  try {
    const response = await getMessaging().send({
      topic: "zonaroja_general",
      data: {
        title: "Prueba Cloud Functions",
        body: "Mensaje data-only desde Functions",
        imageUrl: "https://capitalsq.com/wp-content/uploads/2024/12/OZ-VII-1200x792-1.jpg",
      },
      android: {
        priority: "high",
      },
    });

    res.status(200).send(`OK: ${response}`);
  } catch (error) {
    console.error(error);
    res.status(500).send(error.message);
  }
});
