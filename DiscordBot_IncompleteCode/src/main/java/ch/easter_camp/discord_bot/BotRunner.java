package ch.easter_camp.discord_bot;

import ch.easter_camp.discord_bot.bot.DiscordBot;

import javax.security.auth.login.LoginException;

import static ch.easter_camp.discord_bot.utils.Utils.BOT_TOKEN;

/**
 * @author Stefan Hehlen
 */
public class BotRunner {

    public static void main(String[] args) {
        try {
            new DiscordBot(BOT_TOKEN);
        } catch (LoginException exception) {
            exception.printStackTrace();
        }
    }

}