import com.heartdiet.inference.HeartDietGamificationInference;
import com.heartdiet.inference.HeartDietInference;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HeaRTDietGamification {

    private static final String EXCHANGE_NAME = "front-gamification";
    private static final String BACKEND_EXCHANGE_NAME = "inference";
    private static final String BACKEND_ROUTING_KEY = "results";
    private static final String FRONTEND_ROUTING_KEY = "decisions";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, FRONTEND_ROUTING_KEY);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                JSONParser parser = new JSONParser();
                JSONObject jsonMessage = (JSONObject) parser.parse(message);

                // now do all the reasoning stuff
                HeartDietGamificationInference service = new HeartDietGamificationInference("src/models/heart-diet-gamification.hmr");
                JSONObject results = service.call(jsonMessage);

                // take reasoning results and send it to backend
                channel.basicPublish(BACKEND_EXCHANGE_NAME, BACKEND_ROUTING_KEY, null, results.toJSONString().getBytes("UTF-8"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
