package com.example.lifeshare;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class FcmMessageSender {
    static String posi;

    public static void sendFcmMessage(FcmMessage fcmMessage) {
   //     FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(fcmMessage.getTo())
    //          .setData(fcmMessage.getData())
       //         .build());

        posi= fcmMessage.getPostId();

        // If needed, add additional logic for sending FCM messages
        String serverKey="AAAABmLLg5s:APA91bE6wY99TNYi787VOC0qNUmPQbdfoYkM0wDOBYzICFnRkPizhp6tejN4YiWQNOkSOkuoxRj5mkncRpBvG3oheCm50Zaz6NoOf2HBc9bCXmomIMTjIE9Dv7-YRsn13YWr-Q2MDoMN";

    //   sendNotification("AAAABmLLg5s:APA91bE6wY99TNYi787VOC0qNUmPQbdfoYkM0wDOBYzICFnRkPizhp6tejN4YiWQNOkSOkuoxRj5mkncRpBvG3oheCm50Zaz6NoOf2HBc9bCXmomIMTjIE9Dv7-YRsn13YWr-Q2MDoMN",
          //     fcmMessage.getTo(), "Test head","teast body");
       //This one shown in background


        sendNotification(serverKey, fcmMessage.getTo(), "Blood Request", "An user in your location requires blood. Check the post for details.", fcmMessage.getPostId());

    }

    public static class FcmMessage {
        private String to;
        private Map<String, String> data;

        public FcmMessage(String to, Map<String, String> data) {
            this.to = to;
            this.data = data;
        }

        public String getTo() {
            return to;
        }

        public Map<String, String> getData() {
            return data;
        }
        public String getPostId()
        {
            return data.get("postId");
        }

        // Helper method to convert FcmMessage to JSON string
        public String toJson() {
            return new Gson().toJson(this);
        }
    }

//    public static void sendNotification(String serverKey, String targetToken, String title, String body) {
//        try {
//            // Set the FCM endpoint
//            URL url = new URL("https://fcm.googleapis.com/v1/projects/lifeshare-e7553/messages:send");
//
//            // Open a connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set the request method to POST
//            connection.setRequestMethod("POST");
//
//            // Set the request headers
//            connection.setRequestProperty("Authorization", "Bearer " + serverKey);
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable input/output streams
//            connection.setDoOutput(true);
//
//            // Build the notification message
//            String notificationMessage = String.format(
//                    "{\"message\": {\"token\": \"%s\", \"notification\": {\"title\": \"%s\", \"body\": \"%s\"}}}",
//                    targetToken, title, body
//            );
//
//            // Send the message
//            try (OutputStream os = connection.getOutputStream()) {
//                byte[] input = notificationMessage.getBytes("utf-8");
//                os.write(input, 0, input.length);
//            }
//
//            // Get the response
//            try (Scanner scanner = new Scanner(connection.getInputStream())) {
//                String response = scanner.useDelimiter("\\A").next();
//                Log.d("FCM Response", response);
//            }
//
//            // Close the connection
//            connection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }









    public static void sendNotification(String serverKey, String targetToken, String title, String body) {
        try {
            // Set the FCM endpoint
            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Authorization", "key=" + serverKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            connection.setDoOutput(true);



            // Build the notification message
//            String notificationMessage = String.format(
//                    "{\"to\": \"%s\", \"notification\": {\"title\": \"%s\", \"body\": \"%s\"}}",
//                    targetToken, title, body
//            );

            String notificationMessage = String.format(
                    "{" +
                            "   \"to\": \"%s\", " +
                            "   \"notification\": {" +
                            "       \"title\": \"%s\", " +
                            "       \"body\": \"%s\"" +
                            "   }," +
                            "   \"data\": {" +
                            "       \"postId\": \"%s\"" +
                            "   }" +
                            "}",
                    targetToken, title, body, posi
            );



//            String notificationMessage = String.format(
//                    "{" +
//                            "   \"to\": \"%s\", " +
//                            "   \"data\": {" +
//                            "       \"title\": \"%s\", " +
//                            "       \"body\": \"%s\", " +
//                            "       \"postId\": \"%s\"" +
//                            "   }" +
//                            "}",
//                    targetToken, title, body, posi
//            );



            // Send the message
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = notificationMessage.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println("FCM Response: " + response);
                Log.d("response new: ", response);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static void sendNotification(String serverKey, String targetToken, String title, String body, String postId) {
//        try {
//            // Set the FCM endpoint
//            URL url = new URL("https://fcm.googleapis.com/fcm/send");
//
//            // Open a connection
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            // Set the request method to POST
//            connection.setRequestMethod("POST");
//
//            // Set the request headers
//            connection.setRequestProperty("Authorization", "key=" + serverKey);
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            // Enable input/output streams
//            connection.setDoOutput(true);
//
//            // Build the notification message with additional data (postId)
//            String notificationMessage = String.format(
//                    "{" +
//                            "   \"to\": \"%s\", " +
//                            "   \"data\": {" +
//                            "       \"title\": \"%s\", " +
//                            "       \"body\": \"%s\", " +
//                            "       \"postId\": \"%s\"" +
//                            "   }" +
//                            "}",
//                    targetToken, title, body, postId
//            );
//
//            // Send the message
//            try (OutputStream os = connection.getOutputStream()) {
//                byte[] input = notificationMessage.getBytes("utf-8");
//                os.write(input, 0, input.length);
//            }
//
//            // Get the response
//            try (Scanner scanner = new Scanner(connection.getInputStream())) {
//                String response = scanner.useDelimiter("\\A").next();
//                System.out.println("FCM Response: " + response);
//                Log.d("response new: ", response);
//            }
//
//            // Close the connection
//            connection.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void sendNotification(String serverKey, String targetToken, String title, String body, String postId) {
        try {
            // Set the FCM endpoint
            URL url = new URL("https://fcm.googleapis.com/fcm/send");

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Authorization", "key=" + serverKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Build the notification message with additional data (postId)
//            String notificationMessage = String.format(
//                    "{" +
//                            "   \"to\": \"%s\", " +
//                            "   \"data\": {" +
//                            "       \"title\": \"%s\", " +
//                            "       \"body\": \"%s\", " +
//                            "       \"postId\": \"%s\"" +
//                            "   }" +
//                            "}",
//                    targetToken, title, body, postId
//            );

            Log.d("Sending PostId:",postId );
//            String notificationMessage = String.format(
//                    "{" +
//                            "   \"to\": \"%s\", " +
//                            "   \"notification\": {" +
//                            "       \"title\": \"%s\", " +
//                            "       \"body\": \"%s\"" +
//                            "   }," +
//                            "   \"data\": {" +
//                            "       \"postId\": \"%s\"" +
//                            "   }" +
//                            "}",
//                    targetToken, title, body, postId
//            );

            String notificationMessage = String.format(
                    "{" +
                            "   \"to\": \"%s\", " +
                            "   \"notification\": {" +
                            "       \"title\": \"%s\", " +
                            "       \"body\": \"%s\", " +
                            "       \"click_action\": \"default_action\"" + // Add click action here
                            "   }," +
                            "   \"data\": {" +
                            "       \"postId\": \"%s\"" +
                            "   }" +
                            "}",
                    targetToken, title, body, postId
            );


            // Send the message
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = notificationMessage.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Notification sent successfully.");
            } else {
                System.out.println("Failed to send notification. Response code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
