package pl.javastart.bootcamp.domain.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SlackService {

    private static final Logger logger = LoggerFactory.getLogger(SlackService.class);

    public void sendSlackNotification(String text, String channelId, String botToken) {
        if (StringUtils.isEmpty(channelId)) {
            logger.debug("No channelId for message: " + text);
            return;
        }

        if (StringUtils.isEmpty(botToken)) {
            logger.debug("No botToken for message: " + text);
            return;
        }

        try {
            Slack slack = Slack.getInstance();

            ChatPostMessageResponse response = slack.methods()
                    .chatPostMessage(r -> r.token(botToken)
                            .channel(channelId)
                            .text(text)
                    );
            if (!response.isOk()) {
                logger.warn("Failed to send Slack message. Error: " + response.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Failed to send Slack message.", e);
        }
    }

}
