package soon.capstone.infrastructure.openvidu.config;

import io.livekit.server.WebhookReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenViduWebhookConfig {

    @Bean
    public WebhookReceiver webhookReceiver(
        @Value("${livekit.api.key}") String apiKey,
        @Value("${livekit.api.secret}") String apiSecret
    ) {
        return new WebhookReceiver(apiKey, apiSecret);
    }

}