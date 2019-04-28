import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReceiveLogsTopic {

    private static final String EXCHANGE_NAME = "front";
    private static final String BACKEND_ROUTING_KEY = "results";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "decisions");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(message);
                System.out.println(json);
            } catch(ParseException e) {
                e.printStackTrace();
            }

            // now do all the reasoning stuff
            // ...

            // take reasoning results and send it to backend
            // ...

            channel.basicPublish(EXCHANGE_NAME, BACKEND_ROUTING_KEY, null, message.getBytes("UTF-8"));

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
