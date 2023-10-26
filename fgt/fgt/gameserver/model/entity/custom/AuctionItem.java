package fgt.gameserver.model.entity.custom;


import fgt.Config;
import fgt.gameserver.data.sql.PlayerInfoTable;
import fgt.gameserver.data.xml.ItemData;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.scripting.script.custom.Auction;

public class AuctionItem
{
    public final int trader_objId;
    public final int id_price;
    public final int price;
    public final long addTime;
    private final int objId;
    private final ItemInstance item;
    public boolean alreadySell = false;

    public AuctionItem(int trader_objId, int id_price, int price, long addTime, ItemInstance item)
    {
        this.trader_objId = trader_objId;
        this.id_price = id_price;
        this.price = price;
        this.addTime = addTime;
        this.objId = item.getObjectId();
        this.item = item;
    }

    public AuctionItem(int trader_objId, int id_price, int price, long addTime, int objId, boolean alreadySell)
    {
        this.trader_objId = trader_objId;
        this.id_price = id_price;
        this.price = price;
        this.addTime = addTime;
        this.alreadySell = alreadySell;
        this.objId = objId;
        this.item = AuctionInventory.getInstance().getItemByObjectId(objId);
        if (this.item == null && !alreadySell)
            System.out.println(new StringBuilder().append("Auction: item in Auction, with trader_objid = ").append(trader_objId).append(" is null.").toString());
    }

    public String getItemInfo(boolean head, Player player)
    {
        String str = new StringBuilder().append("<table width=300><tr><td width=32><img src=\"").append(ItemIcons.getInstance().getIcon(this.item.getItemId())).append("\" width=32 height=32 align=left></td>").append("<td width=250>").append("<table width=250>").toString();

        String itemName = new StringBuilder().append(head ? new StringBuilder().append("<a action=\"bypass -h Quest Auction show ").append(this.item.getObjectId()).append("\">").append(ItemData.getInstance().getTemplate(this.item.getItemId()).getName()).append("</a>").toString() : ItemData.getInstance().getTemplate(this.item.getItemId()).getName()).append(" ").toString();
        String count = this.item.getCount() > 1 ? new StringBuilder().append("<font color=603ca9>").append("Количество:").append("</font>").append("<font color=LEVEL> ").append(this.item.getCount()).append("</font>").toString() : "1";
        String enchant = this.item.getEnchantLevel() > 0 ? new StringBuilder().append("<font color=603ca9>").append("Заточка:").append("</font>").append("<font color=LEVEL> +").append(this.item.getEnchantLevel()).append("</font>").toString() : "";
        String name = new StringBuilder().append("<font color=603ca9>").append("Продавец: ").append("</font>").append("<font color=LEVEL>").append(PlayerInfoTable.getInstance().getPlayerName(Integer.valueOf(this.trader_objId))).append("</font>").toString();
        str = new StringBuilder().append(str).append("<tr><td>").append(itemName).append("</td></tr>").toString();        
	str = new StringBuilder().append(str).append("<tr><td>").append(enchant).append("</td></tr>").toString();
        str = new StringBuilder().append(str).append("<tr><td>").append(count).append("</td></tr>").toString();
	str = new StringBuilder().append(str).append("<tr><td>").append(name).append("</td></tr>").toString();
        if (head)
            str = new StringBuilder().append(str).append("<tr><td><font color=603ca9>").append("Цена:").append("</font> <font color=LEVEL>").append(getPrice()).append("</font></td></tr>").toString();
        str = new StringBuilder().append(str).append(Auction.getAugment(this.item, player)).toString();
        str = new StringBuilder().append(str).append("</table></td></tr></table>").toString();

        return str;
    }

    public String getAcceptPage(boolean me, Player player)
    {
        String str = getItemInfo(false, player);
        str = new StringBuilder().append(str).append(getTextForAcceptPage(me, player)).toString();
        str = new StringBuilder().append(str).append(getButtonsForAcceptPage(me, player)).toString();
        return str;
    }

    public String getTextForAcceptPage(boolean me, Player player)
    {
        String str = "<table width=270><tr><td width=270>";
        if (!me)
            str = new StringBuilder().append(str).append("Вы уверены, что хотите купить этот предмет по цене ").append("<font color=LEVEL>").append(getPrice()).append("</font>?").toString();
        else
            str = new StringBuilder().append(str).append("Это ваш предмет, в данный момент в продаже по цене ").append("<font color=LEVEL>").append(getPrice()).append("</font>.\"").append("Хотите снять его с продажи?").toString();
        str = new StringBuilder().append(str).append("</td></tr></table>").toString();
        return str;
    }

    public String getButtonsForAcceptPage(boolean me, Player player)
    {
        String str = "<table width=290><tr><td width=270>";
        str = new StringBuilder().append(str).append("<table width=290><tr>").toString();
        if (!me)
            str = new StringBuilder().append(str).append("<td align=center><button value=\"").append("Купить").append("\" action=\"bypass -h Quest Auction accept_buy ").append(this.item.getObjectId()).append("\" width=135 height=24 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"></td>").toString();
        else
            str = new StringBuilder().append(str).append("<td align=center><button value=\"").append("Забрать").append("\" action=\"bypass -h Quest Auction accept_buy ").append(this.item.getObjectId()).append("\" width=135 height=24 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"></td>").toString();
        str = new StringBuilder().append(str).append("<td align=center><button value=\"").append("Назад").append("\" action=\"bypass -h Quest Auction page 1 0\" width=135 height=24 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"></td>").toString();
        str = new StringBuilder().append(str).append("</tr></table>").toString();
        return str;
    }

    public String getPrice()
    {
        return new StringBuilder().append(this.price).append(" ").append(ItemData.getInstance().getTemplate(this.id_price).getName()).toString();
    }

    public boolean isOverdue()
    {
        return getDeleteTime() < System.currentTimeMillis();
    }

    public long getDeleteTime()
    {
        return this.addTime + 86400000 * Config.AUCTION_COUNT_DAY_FOR_DELETE_ITEM;
    }

    public int getObjectId()
    {
        return this.objId;
    }

    public int getId()
    {
        return this.item == null ? 0 : this.item.getItemId();
    }
}
