package fgt.commons.network;

import fgt.commons.pool.ConnectionPool;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.GameClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Angel
 * 16.07.2017
 */
public class MacTest
{
    private static Logger _log = Logger.getLogger(MacTest.class.getName());

    private String _mac;
    private String _ip;

    private String mac;
    private String host;

    public final Map<String, String> macs = new ConcurrentHashMap<>();

    public static MacTest getInstance()
    {
        return MacTest.SingletonHolder._instance;
    }

    public static void MacTest(String[] args)
    {

        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++)
            {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println(sb.toString());
        }

        catch (UnknownHostException | SocketException e)
        {
            e.printStackTrace();
        }
    }

    public String getMac (GameClient client, boolean b)
    {
        InetAddress ip = client.getConnection().getInetAddress();

        try {
            //_log.info("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            final byte[] mac = network.getHardwareAddress();

            //_log.info("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++)
            {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            _mac = sb.toString();
            if (b)
            {
                _log.info("Current IP address : " + ip.getHostAddress());
                _log.info("Current MAC address : " + sb.toString());
            }
        }
        catch (SocketException e){}
        return _mac;
    }

    public final String getMac (GameClient client) throws UnknownHostException
    {
        final InetAddress ip = client.getConnection().getInetAddress().getLocalHost();

        try {

            //System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            final byte[] mac = network.getHardwareAddress();

            //_log.info("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++)
            {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return _mac = sb.toString();
        }

        catch (SocketException e) {}
        return _mac;
    }

    private void getMAC(GameClient client)
    {
        getMac(client, false);
    }

    private void getMAC(Player player)
    {
        getMac(player.getClient(), false);
    }

    public void saveMac(Player player)
    {

        try (Connection con = ConnectionPool.getConnection())
        {
            PreparedStatement statement = con.prepareStatement("INSERT INTO characters_macs (account_name,objectId,name,mac,host) VALUES (?,?,?,?,?)");

            statement.setString(1, player.getAccountName());
            statement.setInt(2, player.getObjectId());
            statement.setString(3, player.getName());
            statement.setString(4, getMac(player.getClient()));
            statement.setString(5, player.getClient().getConnection().getInetAddress().getHostAddress());

            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Не возможно узнать MAC-адрес игрока " + player.getName() + "!");
        }
    }

    public void delMac(Player player)
    {

        try (Connection con = ConnectionPool.getConnection())
        {
            PreparedStatement statement = con.prepareStatement("DELETE FROM characters_macs WHERE objectId=? AND mac=?");

            statement.setInt(1, player.getObjectId());
            statement.setString(2, getMac(player.getClient()));

            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Ошибка при удалении MAC-адреса икрока " + player.getName() + "!");
        }
    }

    public void delMacs()
    {

        try (Connection con = ConnectionPool.getConnection())
        {
            PreparedStatement statement = con.prepareStatement("TRUNCATE TABLE characters_macs");
            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Ошибка при удалении таблицы characters_macs!");
        }
    }

    public void banMac(Player player)
    {
        try (Connection con = ConnectionPool.getConnection())
        {
            PreparedStatement statement = con.prepareStatement("INSERT INTO banned_macs (account_name,objectId,name,mac,host) VALUES (?,?,?,?,?)");

            statement.setString(1, player.getAccountName());
            statement.setInt(2, player.getObjectId());
            statement.setString(3, player.getName());
            statement.setString(4, getMac(player.getClient()));
            statement.setString(5, player.getClient().getConnection().getInetAddress().getHostAddress());

            statement.execute();
            statement.close();
        }
        catch (Exception e)
        {
            _log.warning("Ошибка бана игрока  " + player.getName() + " по MAC-адресу!");
        }
    }

    public boolean testMAC (Player player)
    {
        String selectTableSQL = "SELECT mac from macs";
        //String selectTableSQL = "SELECT mac AND host from macs";

        try (Connection con = ConnectionPool.getConnection())
        {
            Statement statement = con.createStatement();

            // выбираем данные с БД
            ResultSet rs = statement.executeQuery(selectTableSQL);

            // И если что то было получено то цикл while сработает
            while (rs.next())
            {
                mac = rs.getString("mac");
                //host = rs.getString("host");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return true;
    }

    public boolean testHost (Player player)
    {
        String selectTableSQL = "SELECT host from macs";
        //String selectTableSQL = "SELECT mac AND host from macs";

        try (Connection con = ConnectionPool.getConnection())
        {
            Statement statement = con.createStatement();

            // выбираем данные с БД
            ResultSet rs = statement.executeQuery(selectTableSQL);

            // И если что то было получено то цикл while сработает
            while (rs.next())
            {
                //mac = rs.getString("mac");
                host = rs.getString("host");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return true;
    }

    private static class SingletonHolder
    {
        protected static final MacTest _instance;

        static
        {
            try
            {
                _instance = new MacTest();
            }
            catch (Exception e)
            {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}
