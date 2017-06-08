package com.sinndevelopment.reddevil.bot;

import com.sinndevelopment.reddevil.commands.ChatCommand;
import com.sinndevelopment.reddevil.tasks.GivePointTask;
import com.sinndevelopment.reddevil.util.Config;
import org.jibble.pircbot.PircBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatBot extends PircBot
{
    private static ChatBot instance;

    public ChatBot()
    {
        instance = this;
    }

    public static ChatBot getInstance()
    {
        return instance;
    }

    private List<String> moderators = new ArrayList<>();
    private List<String> viewers = new ArrayList<>();
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    private List<ChatCommand> commands = new ArrayList<>();
    private String helpString = "";

    private HashMap<String, Long> cooldown = new HashMap<>();

    public ChatBot(String name, String login)
    {
        this.setName(name);
        this.setLogin(login);
        this.setVersion("1.0");
        this.setVerbose(true);
        instance = this;

        ses.scheduleAtFixedRate(new GivePointTask(this), 3000, 60 * 1000, TimeUnit.MILLISECONDS);

        for (ChatCommand c : commands)
        {
            helpString += c.getPrefix() + c.getName() + ", ";
        }
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        if (message.startsWith("_help"))
        {
            sendChannelMessage("@" + sender + " my commands are: " + helpString);
        }
        ChatCommand command = isCommand(message);
        if(command != null)
        {
            if (isCooldown(sender))
                return;

            command.onMessage(channel, sender, login, hostname, message.substring(
                    command.getPrefix().length() + command.getName().length()));
            setCooldown(sender);
        }
    }

    private ChatCommand isCommand(String chat)
    {
        for (ChatCommand command : commands)
        {
            if (chat.startsWith(command.getFullCommand())
                    || command.isAlias(chat))
                return command;
        }
        return null;
    }

    public List<String> getModerators()
    {
        return moderators;
    }

    public void setModerators(List<String> moderators)
    {
        this.moderators = moderators;
    }

    public List<String> getViewers()
    {
        return viewers;
    }

    public void setViewers(List<String> viewers)
    {
        this.viewers = viewers;
    }

    public void sendChannelMessage(String channel, String message)
    {
        this.sendMessage(channel, message);
    }

    public void sendChannelMessage(String message)
    {
        sendChannelMessage(Config.DEFAULT_CHANNEL, message);
    }

    public void reload()
    {
        try
        {
            reconnect();
            joinChannel(Config.DEFAULT_CHANNEL);
        }
        catch (Exception e)
        {
            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void onDisconnect()
    {
        while (!isConnected())
        {
            reload();
        }
    }

    public void setCooldown(String user)
    {
        cooldown.put(user, System.currentTimeMillis());
    }

    public long getCooldown(String user)
    {
        if (cooldown.containsKey(user))
            return cooldown.get(user) + 5 * 1000;
        return System.currentTimeMillis();
    }

    public boolean isCooldown(String user)
    {
        if (cooldown.containsKey(user))
        {
            long oldTime = getCooldown(user);
            if (System.currentTimeMillis() < oldTime)
            {
                return true;
            }
        }
        return false;
    }

    public void sendViewerMessage(String name, String mess)
    {
        sendChannelMessage("@" + name + " " + mess);
    }

}
