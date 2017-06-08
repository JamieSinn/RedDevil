package com.sinndevelopment.reddevil.commands.cmds;

import com.sinndevelopment.reddevil.commands.ChatCommand;
import com.sinndevelopment.reddevil.commands.PermissionsLevel;

import java.util.List;

public class CommandPrint extends ChatCommand
{
    public CommandPrint()
    {
        super("print", PermissionsLevel.MODERATOR);
    }

    @Override
    protected void onCommand(String channel, String sender, String login, String hostname, List<String> args)
    {
        StringBuilder sb = new StringBuilder();
        for(String s : args)
        {
            if(s.equals("print") || s.equals(" ")) continue;

            sb.append(s).append(" ");
        }
        bot.sendChannelMessage(sb.toString());
    }
}
