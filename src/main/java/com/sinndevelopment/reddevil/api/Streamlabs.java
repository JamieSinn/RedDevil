package com.sinndevelopment.reddevil.api;

import com.sinndevelopment.reddevil.RedDevil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Streamlabs
{
    private static String getAccessToken() throws Exception
    {
        String url = "https://sinndevelopment.com/oauth/streamlabs/token.php?refresh_token=" + RedDevil.REFRESH_TOKEN;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        RedDevil.getLogger().info(jsonObject.toString());
        RedDevil.getLogger().info(response.toString());
        RedDevil.REFRESH_TOKEN = jsonObject.getString("refresh_token");
        return jsonObject.getString("access_token");
    }

    public static boolean sendBoo(String user) throws Exception
    {
        return sendStreamLabs("*"+user+"* says boo!",
                "http://i.imgur.com/eU9dBRP.png",
                "http://wat.sinnpi.com/dl/nootnoot.ogg");
    }

    public static boolean sendHAL(String user) throws Exception
    {
        return sendStreamLabs("*"+user+"* I'm sorry Libby, I'm afraid I can't do that...",
                "http://i.imgur.com/DiSyfXB.png",
                "http://wat.sinnpi.com/dl/dave.ogg");
    }

    public static boolean sendStreamLabs(String message, String image, String sound) throws Exception
    {
        String url = "https://sinndevelopment.com/oauth/streamlabs/alert.php";

        String urlParameters = "?access_token=" + getAccessToken() +
                "&message=" + message +
                "&image=" + image +
                "&sound=" + sound;
        URL obj = new URL(url+urlParameters);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        RedDevil.getLogger().info(jsonObject.toString());
        RedDevil.getLogger().info(response.toString());

        RedDevil.getLogger().info("Attempted Contacting Streamlabs... R:"+con.getResponseCode());
        ///////
        return con.getResponseCode() == 200;
    }
}
