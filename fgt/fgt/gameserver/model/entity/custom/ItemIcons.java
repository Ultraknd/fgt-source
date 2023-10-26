package fgt.gameserver.model.entity.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import fgt.commons.logging.CLogger;
import fgt.commons.pool.ConnectionPool;

public class ItemIcons {

    private static ItemIcons _instance = null;
    protected static final CLogger _log = new CLogger(ItemIcons.class.getName());
    HashMap<Integer, String> _items;

    public static ItemIcons getInstance() {
        if (_instance == null) {
            _instance = new ItemIcons();
        }
        return _instance;
    }

    public String getIcon(int itemId) {
        return _items.containsKey(itemId) ? (String) _items.get(itemId) : "";
    }

    public ItemIcons()
    {
        initScript();
    }

    private void initScript() {
        _items = new HashMap<>();
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT itemId, icon FROM `item_icons`");
            ResultSet rset = statement.executeQuery();

            int count = 0;
            while (rset.next()) {
                int itemId = rset.getInt("itemId");
                String icon = rset.getString("icon");
                _items.put(itemId, icon);
                count++;
            }
            _log.info("Загружено " + count + " иконок итемов.");
        } catch (Exception e) {
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
