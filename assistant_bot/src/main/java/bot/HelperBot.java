package bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.request.RouterOptions;
import reactor.core.publisher.Mono;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.UserUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.MessageReference;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.reaction.ReactionEmoji;

public class HelperBot {

	private static final String log = "NzM5ODQ1NDM0MTI5Nzc2NzYx.XygYwQ.Z5EXytzXwj4X2gIXUpZJ8GYvQ-0";
	private static String value = null;
	private static boolean lastMessage = false;
	
	public static void main(String[] args) {
		HelperBot bot = new HelperBot();
		GatewayDiscordClient client = DiscordClientBuilder.create(log).build().login().block();
		
		client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
			User self = event.getSelf();
			System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
		});
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
        .map(MessageCreateEvent::getMessage)
        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
        .filter(message -> {
        	value = message.getContent();
        	lastMessage = true;
        	return bot.checkMessage(value);
        })
        .flatMap(Message::getChannel)
        .flatMap(channel -> {
        	return channel.createMessage("-play " + value);
        }).subscribe();
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
		.map(MessageCreateEvent::getMessage)
		.flatMap(message -> {
			String content = message.getContent();
			System.out.println(content);
			
			if (lastMessage && content.startsWith("-play ")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				message.delete();
				System.out.println("deleted" + content);
				lastMessage = false;
				return message.delete();
			}
			return Mono.empty();
		})
		.subscribe();		
		
		client.getEventDispatcher().on(PresenceUpdateEvent.class)
		.subscribe((event) -> {
			Presence presence = event.getCurrent();
			presence.
			System.out.println(event.getCurrent());
		});
		
		client.onDisconnect().block();
	}
	
	public boolean checkMessage(String message) {
		if (message.startsWith("-")) {
			return false;
		}
		return true;
	}
	
}
