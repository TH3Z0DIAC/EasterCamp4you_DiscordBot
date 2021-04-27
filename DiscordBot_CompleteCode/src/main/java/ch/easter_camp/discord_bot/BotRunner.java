package ch.easter_camp.discord_bot;

import ch.easter_camp.discord_bot.bot.Bot;

import javax.security.auth.login.LoginException;

import static ch.easter_camp.discord_bot.utils.Utils.BOT_TOKEN;

/**
 * @author Stefan Hehlen
 */
public class BotRunner {

    public static void main(String[] args) {

        try {
            new Bot(BOT_TOKEN);
        } catch (LoginException exception) {
            exception.printStackTrace();
        }

    }

}
