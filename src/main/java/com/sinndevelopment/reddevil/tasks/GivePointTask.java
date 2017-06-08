package com.sinndevelopment.reddevil.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinndevelopment.reddevil.RedDevil;
import com.sinndevelopment.reddevil.bot.ChatBot;
import com.sinndevelopment.reddevil.data.Viewer;
import com.sinndevelopment.reddevil.data.YAMLViewerHandler;
import com.sinndevelopment.reddevil.util.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class GivePointTask extends TimerTask
{
    private String[] blockedPoints = {"nightbot", Config.USERNAME, "null"};
    private ChatBot bot;

    public GivePointTask(ChatBot bot)
    {
        this.bot = bot;
    }

    public static Map<String, Object> getChatters()
    {
        Map<String, Object> ret = null;
        try
        {
            InputStream is = new URL("http://tmi.twitch.tv/group/user/"+ Config.DEFAULT_CHANNEL+"/chatters").openConnection().getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                result.append(line);
            }
            Gson gson = new Gson();

            ret = gson.fromJson(result.toString(), new TypeToken<Map<String, Object>>(){}.getType());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ret;
    }

    public void run()
    {
        Map<String, List<String>> json = (Map<String, List<String>>) getChatters().get("chatters");
        List<String> mods = json.get("moderators");
        List<String> viewers = json.get("viewers");

        viewers.addAll(mods);

        bot.setModerators(mods);
        bot.setViewers(viewers);

        for(String s : viewers)
        {
            boolean blocked = false;
            for(String b : blockedPoints)
            {
                if(s.equals(b))
                    blocked = true;
            }
            if(!blocked)
            {
                Viewer v = YAMLViewerHandler.getViewer(s);
                if(v.isSubscriber()) v.addPoint();

                v.addPoint();
                YAMLViewerHandler.saveViewer(v);
                RedDevil.getLogger().info("Added a point to " + v.getUsername());
            }
        }
    }
}
