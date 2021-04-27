package ch.easter_camp.discord_bot.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Stefan Hehlen
 */
public class DiscordBot extends ListenerAdapter {

    // Token used for logging the user in
    private final String token;

    /**
     * Default constructor for the bot
     *
     * @param token {@link String} bot token received from discord developer application
     * @throws LoginException thrown when token is invalid and user could not log in
     */
    public DiscordBot(String token) throws LoginException {
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
                .createDefault(token /* Intents */)
                .setMemberCachePolicy(MemberCachePolicy.ALL)            // Stores all users from the guild in cache
                .addEventListeners(this)                                // Used for the bot so he receives the events
                // Insert Disable Cache
                .setActivity(Activity.playing(""/* Add your activity for the bot */)) // Set activity seen when hovering over bot
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

            /*
             * Open private channel from joinedUser
             * queue with privateChannel
             * sendMessage in privateChannel with the text you'd like to set when a member joins the guild
             */

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

            /*
            case "": //Add command / text into "" to make it checked
                channel.sendMessage(
                    // Add answer text that should be sent
                ).queue();
                break;
            */

            default:
                // If no command is executed, do nothing IMPORTANT: do not insert anything here!
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
                // If no command is executed, do nothing IMPORTANT: do not insert anything here!
                break;
        }

    }
}