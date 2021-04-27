package ch.easter_camp.discord_bot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.easter_camp.discord_bot.utils.Utils.*;

/**
 * @author Stefan Hehlen
 */
public class Bot extends ListenerAdapter {

    // Token used for logging the user in
    private final String token;

    /**
     * Default constructor for the bot
     *
     * @param token {@link String} bot token received from discord developer application
     * @throws LoginException thrown when token is invalid and user could not log in
     */
    public Bot(String token) throws LoginException {
        this.token = token;
        initialize();
    }

    /**
     * Method initializing the bot on creation
     *
     * @throws LoginException thrown when bot token is invalid
     */
    private void initialize() throws LoginException {
        JDABuilder
                .createDefault(token,
                        GatewayIntent.GUILD_MEMBERS,                    // Needed for the bot to see when users join the guild
                        GatewayIntent.DIRECT_MESSAGES,                  // Needed for the bot to send direct messages to guild members
                        GatewayIntent.GUILD_MESSAGES)                   // Needed for the bot to see messages sent in guild chats
                .setMemberCachePolicy(MemberCachePolicy.ALL)            // Stores all users from the guild in cache
                .addEventListeners(this)                                // Used for the bot so he receives the events
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)   // Disables emote and voice state caches since they are not needed in this case
                .setActivity(Activity.playing("Type !help"))            // Set activity seen when hovering over bot
                .build();                                               // Build bot
    }

    /**
     * Method executed when a new member joins the guild
     *
     * @param event {@link GuildMemberJoinEvent} sent by JDA when a new person joins the Server
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        // Get api/interface of discord
        JDA api = event.getJDA();
        // get newly joined user from api, returns null when user is invisible for bot
        User joinedUser = api.getUserById(event.getMember().getId());

        // checks if user is visible to bot
        if (null != joinedUser) {
            joinedUser
                    .openPrivateChannel()                   // open chat with newly joined user
                    .queue((privateChannel ->               // send request to api
                            privateChannel.sendMessage(     // request body -> send welcome message
                                    String.format(WELCOME_MESSAGE, joinedUser.getName(), event.getGuild().getName())  // Formatting welcome message with variable strings
                            )
                                    .queue()));
        }


    }

    /**
     * Method handling input/text received from a user in the private chat
     *
     * @param event {@link PrivateMessageReceivedEvent} Event sent by JDA when a private message is received
     */
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {

        // Received message from user, chars are set to lowercase
        String receivedMessage = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT);
        // Channel in which the message was received
        MessageChannel channel = event.getChannel();

        switch (receivedMessage) {
            case "!hey":
                channel.sendMessage(
                        String.format(HEY_MESSAGE, event.getAuthor().getAsMention())
                ).queue();
                break;
            case "!ping":
                channel.sendMessage(PONG).queue();
                break;
            case "!help":
                channel.sendMessage(HELP_MESSAGE).queue();
                break;
            default:
                // If no command is executed, do nothing
                break;
        }

    }

    /**
     * Method handling the input/text sent into a guild channel
     *
     * @param event {@link GuildMessageReceivedEvent} Event sent by JDA when a guild message is received in a channel
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        // Roles loaded from Guild
        List<Role> roles = Objects.requireNonNull(event.getMember()).getGuild().getRoles();
        // Command received, every char is set to a lowercase letter for switch
        String command = event.getMessage().getContentRaw().toLowerCase(Locale.ROOT);

        // Role that should be added
        Role roleToAdd;
        // Member/Author of message, receiver of role
        Member roleReceiver = event.getMember();
        // Receive Guild from author (is needed to add the role)
        Guild currentGuild = roleReceiver.getGuild();

        // Checking which command was executed
        switch (command) {
            case "!dev":
                // Getting role from List
                roleToAdd = roles.stream()
                        .filter(role -> role.getName().equalsIgnoreCase("developer"))   // Removing all roles not matching the developer role
                        .collect(Collectors.toList())   // collect result and convert it to a list
                        .get(0);    // only one member should in list, this one is taken out here
                currentGuild.addRoleToMember(roleReceiver, roleToAdd).queue();
                break;
            case "!stud":
                // Getting role from List
                roleToAdd = roles.stream()
                        .filter(role -> role.getName().equalsIgnoreCase("student"))     // Removing all roles not matching the student role
                        .collect(Collectors.toList())   // collect result and convert it to a list
                        .get(0);    // only one member should in list, this one is taken out here
                currentGuild.addRoleToMember(roleReceiver, roleToAdd).queue();
                break;
            default:
                // If no command is executed, do nothing
                break;
        }

    }
}
