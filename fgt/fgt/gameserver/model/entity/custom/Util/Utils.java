package fgt.gameserver.model.entity.custom.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import fgt.commons.logging.CLogger;
import fgt.gameserver.data.xml.ItemData;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.entity.custom.Util.task.SQLQueue;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.ExShowScreenMessage;
import fgt.gameserver.network.serverpackets.SystemMessage;

public class Utils
{
    private static String[][] timeTypeRu = { { "секунда", "минута", "час" }, { "секунды", "минуты", "часа" }, { "секунд", "минут", "часов" } };
    private static String[][] timeTypeEn = { { "second", "minute", "hour" }, { "seconds", "minuts", "hours" }, { "seconds", "minuts", "hours" } };

    public static int getItemCount(Player playable, int item_id)
    {
        return playable.getInventory().getItemByItemId(item_id) != null ? playable.getInventory().getItemByItemId(item_id).getCount() : 0;
    }

    public static boolean haveCountItem(Player player, int id, int count)
    {
        return getItemCount(player, id) >= count;
    }

    public static boolean haveItem(Player playable, int item_id, int count, boolean sendMessage)
    {
        long cnt = count - getItemCount(playable, item_id);
        if (cnt > 0L)
        {
            if (sendMessage)
                playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
            return false;
        }
        return true;
    }

    public static boolean haveItem(Player playable, int[] item, boolean sendMessage)
    {
        return haveItem(playable, item[0], item[1], sendMessage);
    }

    public static boolean haveItem(Player playable, int[][] items, boolean sendMessage)
    {
        for (int[] item : items)
        {
            if (!haveItem(playable, item, sendMessage))
                return false;
        }
        return true;
    }

    public static void removeItem(Player playable, int[][] items, String desc)
    {
        for (int[] item : items)
            removeItem(playable, item, desc);
    }

    public static void removeItem(Player player, int[] item, String desc)
    {
        removeItem(player, item[0], item[1], desc);
    }

    public static void removeItem(Player player, int id, int count, String desc)
    {
        player.destroyItemByItemId(desc, id, count, null, true);
    }

    public static void addItem(Player playable, int[][] items, String desc)
    {
        for (int[] item : items)
            playable.addItem(desc, item[0], item[1], null, true);
    }

    public static void createDebugFile(String name, String content, CLogger log)
    {
        try
        {
            File del = new File(name + ".txt");
            if (del.exists())
                del.delete();
            try (FileWriter file = new FileWriter(name + ".txt")) {
                file.write(content);
            }
            if (log != null)
                log.info("Debuf file " + name + " has been create.");
        }
        catch (IOException e)
        {
        }
    }

    public static String getPriceName(int[] price)
    {
        return getPriceName(price[0], price[1]);
    }

    public static String getPriceName(int itemId, int count)
    {
        return count + " " + ItemData.getInstance().getTemplate(itemId).getName();
    }

    public static String getClassName(Player player)
    {
        return player.getClass().getName();
    }

    public static void pause(long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (Exception e)
        {
        }
    }

    public static void sendMessage(String message, Player[] players)
    {
        sendMessage(message, 5000, players);
    }

    public static void sendMessage(String message, int time, Player[] players)
    {
        for (Player player : players)
            player.sendPacket(new ExShowScreenMessage(message, time));
    }

    public static void addItem(int charId, int itemId, int count)
    {
        Player result = World.getInstance().getPlayer(charId);
        if (result != null)
            result.addItem("PcUtils", itemId, count, null, true);
        else
            SQLQueue.getInstance().add(new AddToOffline(charId, itemId, count));
    }

    public static String getButton(String string, String bypass, boolean big)
    {
        String size = !big ? "width=100 height=25 back=\"L2UI_CH3.bigbutton_down\" fore=\"L2UI_CH3.bigbutton\"" : "width=157 height=25 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"";
        return "<button value=\"" + string + "\" action=\"bypass -h " + bypass + "\" " + size + " />";
    }

    public static String buttonCBUI94(String name, String bypass, int width, int height)
    {
        return "<button value=\"" + name + "\" action=\"bypass -h " + bypass + "\" width=" + width + " height=" + height + " back=\"sek.cbui94\" fore=\"sek.cbui92\">";
    }

    public static String timeSuffix(Player player, int time, int type)
    {
        int num = time;
        if (num > 100) num %= 100;
        if (num > 20) num %= 10;

        if (type < 0)
            type = 0;
        else if (type > 2)
            type = 2;
        switch (num)
        {
            case 1:
                num = 0;
                break;
            case 2:
            case 3:
            case 4:
                num = 1;
                break;
            default:
                num = 2;
        }

        String strTime = Integer.toString(time);
        try
        {
            strTime =strTime + " " + timeTypeEn[num][type];
        }
        catch (Exception e)
        {
        }
        return strTime;
    }

    public static boolean checkIp(String yourIp)
    {
        try
        {
            URL url = new URL("http://www.myip.ru/ru-RU/index.php");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while((line = reader.readLine()) != null)
            {
                Pattern p = Pattern.compile(".+>(\\d+\\.\\d+\\.\\d+\\.\\d+)<.+");
                Matcher m = p.matcher(line);
                if (m.matches())
                    return m.group(1).equals(yourIp);
            }

        }
        catch (Exception e)
        {
        }
        return false;
    }

    private static class AddToOffline implements SQLQuery
    {
        private int _charId;
        private int[] _item;
        public AddToOffline(int charId, int itemid, int count)
        {
            _charId = charId;
            _item = new int[] { itemid, count };
        }

        @Override
        public void execute(Connection con)
        {
            try
            {
                try (PreparedStatement stm = con.prepareStatement("INSERT INTO `character_items` VALUES (?,?,?,0)")) {
                    stm.setInt(1, _charId);
                    stm.setInt(2, _item[0]);
                    stm.setInt(3, _item[1]);
                    stm.execute();
                }
            }
            catch (SQLException e)
            {
            }
        }
    }
}
