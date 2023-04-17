package com.example.mydacha2.supportclass;

public class MyMqttConnectOptions {
    private final String serverUri;
    private final String username;
    private String password;
    private static MyMqttConnectOptions myMqttConnectOptions;
    private String clientId;
    private String subscriptionTopic;

    private MyMqttConnectOptions(String serverURI,String username, String password) {
        this.serverUri = serverURI;
        this.username = username;
        this.password = password;
    }

    public static MyMqttConnectOptions getMqttConnectOptions(String serverURI,String username, String password) {
        if (myMqttConnectOptions == null) myMqttConnectOptions = new MyMqttConnectOptions(serverURI, username, password);
        return myMqttConnectOptions;
    }

    public String getServerUri() {
        return serverUri;
    }
/*
 *   public void setServerUri(String serverUri) {
 *      this.serverUri = serverUri;
 *   }
 *
 *   public String getClientId() {
 *      return clientId;
 * }
*/

    public String getUsername() {
        return username;
    }

/*
 *   public void setSubscriptionTopic(String subscriptionTopic) {
 *      this.subscriptionTopic = subscriptionTopic;
 * }
 *
 *
 *   public void setUsername(String username) {
 *      this.username = username;
 * }
 */

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSubscriptionTopic() {
        return this.subscriptionTopic;
    }
}
