package fgt.gameserver.scripting.script.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import fgt.commons.lang.DbUtils;
import fgt.commons.logging.CLogger;
import fgt.commons.pool.ConnectionPool;
import fgt.commons.pool.ThreadPool;
import fgt.gameserver.data.cache.HtmCache;
import fgt.gameserver.data.xml.ItemData;
import fgt.gameserver.data.xml.NpcData;
import fgt.gameserver.enums.items.CrystalType;
import fgt.gameserver.enums.items.EtcItemType;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;

import fgt.Config;
import fgt.gameserver.*;
import fgt.gameserver.model.*;
import fgt.gameserver.model.entity.custom.AuctionInventory;
import fgt.gameserver.model.entity.custom.AuctionItem;
import fgt.gameserver.model.entity.custom.AuctionLoggerUtils;
import fgt.gameserver.model.entity.custom.ItemIcons;
import fgt.gameserver.model.entity.custom.Util.IOnLogin;
import fgt.gameserver.model.entity.custom.Util.Utils;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.model.itemcontainer.ItemContainer;
import fgt.gameserver.model.location.Location;
import fgt.gameserver.model.spawn.Spawn;
import fgt.gameserver.scripting.Quest;
import fgt.gameserver.scripting.QuestState;
import fgt.gameserver.network.serverpackets.InventoryUpdate;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.skills.L2Skill;

public class Auction extends Quest implements IOnLogin
{
    protected static final CLogger _logAuction = new CLogger(Auction.class.getName());
    protected static final CLogger _log = new CLogger(Auction.class.getName());
    public static String qn = "Auction";
    public static Npc AUCTIONER_INSTANCE;
    private static Auction _instance = null;
    private ConcurrentHashMap<Integer, AuctionItem> _products = new ConcurrentHashMap<>();

    public static Auction getInstance()
    {
        if (_instance == null)
            _instance = new Auction();
        return _instance;
    }

    public Auction()
    {
        super(-1, "Auction");
        initScript();
    }

    @Override
    public String onFirstTalk(Npc npc, Player player)
    {
        return onTalk(npc, player);
    }

   @Override
    public void intoTheGame(Player player)
    {
        try
        {
            for (AuctionItem aItem : _products.values())
                if (aItem.trader_objId == player.getObjectId() && aItem.alreadySell)
                    sendPayment(player, aItem);
                else if (aItem.isOverdue())
                {
                    player.sendMessage("Один из ваших товаров на Аукционе не был продан за выделенный срок.");
                    removeFromSale(player, aItem);
                }
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public String onTalk(Npc npc, Player talker)
    {
        if (talker.getQuestList().getQuestState("Auction") == null)
            newQuestState(talker);
        return showHeadPage(talker, 1, 0);
    }

    @Override
    public String onEvent(String event, QuestState qs) {
        Player player = qs.getPlayer();
        int page = 1;
        int type = 0;
        if (event.startsWith("page"))
        {
            try
            {
                String[] temp = event.split(" ");
                page = Integer.parseInt(temp[1]);
                type = Integer.parseInt(temp[2]);
            }
            catch (Exception e)
            {
                player.sendMessage("Такой страницы не существует.");
            }
        }
        else if (event.startsWith("show"))
        {
            try
            {
                AuctionItem item = _products.get(Integer.valueOf(Integer.parseInt(event.split(" ")[1])));
                if (item != null)
                    return showItem(player, item);
                player.sendMessage("Такой предмет на аукционе отсутствует");
            }
            catch (Exception e)
            {
                player.sendMessage("Такой предмет на аукционе отсутствует");
            }
        }
        else if (event.startsWith("accept_buy"))
        {
            try
            {
                AuctionItem item = _products.get(Integer.valueOf(Integer.parseInt(event.split(" ")[1])));
                if (item != null)
                {
                    if (!item.alreadySell)
                    {
                        if (player.getObjectId() != item.trader_objId)
                            buyItem(player, item);
                        else
                            removeFromSale(player, item);
                    }
                    else
                        player.sendMessage("Предмет уже был продан.");
                }
                else
                    player.sendMessage("Такой предмет на аукционе отсутствует.");
            }
            catch (Exception e)
            {
                player.sendMessage("Такой предмет на аукционе отсутствует.");
            }
        }
        else
        {
            if (event.startsWith("create_product"))
            {
                try
                {
                    page = Integer.parseInt(event.split(" ")[1]);
                }
                catch (Exception e)
                {
                    player.sendMessage("Такой страницы не существует.");
                }
                return showCreateProductPage(player, page);
            }
            if (event.startsWith("chose "))
            {
                try
                {
                    int objIdItem = Integer.parseInt(event.split(" ")[1]);
                    ItemInstance item = player.getInventory().getItemByObjectId(objIdItem);
                    if (!qualItem(item))
                    {
                        player.sendMessage("В вашем инвентаре нет этого предмета.");
                        return showCreateProductPage(player, 1);
                    }
                    return showChoseProductPage(player, item);
                }
                catch (Exception e)
                {
                    player.sendMessage("Неверные данные.");
                }
            }
            else if (event.startsWith("chose_accept"))
            {
                try
                {
                    String[] temp = event.split(" ", 4);
                    int objIdItem = Integer.parseInt(event.split(" ")[1]);
                    int price_id = getRewardId(temp[3]);
                    int price = Integer.parseInt(event.split(" ")[2]);
                    ItemInstance item = player.getInventory().getItemByObjectId(objIdItem);
                    int auction_priceCount;
                    int auction_priceId;
                    if (Config.AUCTION_PERCENTAGE)
                    {
                        auction_priceId = price_id;
                        auction_priceCount = price / 100 * Config.AUCTION_GET_PERCENT;
                    }
                    else
                    {
                        auction_priceId = item.isAugmented() ? Config.AUCTION_AUGMENT_PRICE[0] : Config.AUCTION_PRICE[0];
                        auction_priceCount = item.isAugmented() ? Config.AUCTION_AUGMENT_PRICE[1] : Config.AUCTION_PRICE[1];
                    }

                    if (!qualItem(item))
                    {
                        player.sendMessage("В вашем инвентаре нет этого предмета.");
                        return showCreateProductPage(player, 1);
                    }
                    if (!Utils.haveCountItem(player, auction_priceId, auction_priceCount))
                    {
                        player.sendMessage("У вас не хватает средств для выставления товара на продажу!");
                        return showCreateProductPage(player, 1);
                    }
                    if (price <= 0)
                    {
                        player.sendMessage("Цена должна быть больше нуля.");
                        return showCreateProductPage(player, 1);
                    }
                    choseAccept(player, item, price_id, price);
                }
                catch (Exception i)
                {
                    player.sendMessage("Неверные данные.");
                }
            }
            else if (event.startsWith("my_products"))
            {
                page = 1;
                type = 3;
            }
        }
        return showHeadPage(player, page, type);
    }

    public final void initScript()
    {
        _instance = this;

        //Config.readConfig();
        if (!Config.AUCTION_ENABLE)
            return;
        NpcTemplate template = NpcData.getInstance().getTemplate(Config.AUCTION_NPC_ID);
        if (template != null) {
            template.setName(Config.AUCTION_NPC_NAME);
            template.setTitle(Config.AUCTION_NPC_TITLE);
            for (Location loc : Config.AUCTION_NPC_SPAWN_LOC) {
                try {
                    Spawn spawn = new Spawn(template);
                    spawn.setLoc(loc.getX(), loc.getY(), loc.getZ(), 0);
                    spawn.doSpawn(true);
                } catch (SecurityException | ClassNotFoundException | NoSuchMethodException e1) {
                    _log.warn("Could not spawn Npc " + Config.AUCTION_NPC_ID);
                }
            }
            _log.info("Аукцион-менеджер загружен...");
            _log.info("Аукцион-менеджер заспавнено " + Config.AUCTION_NPC_SPAWN_LOC.size() + " менеджеров");
        }
        AUCTIONER_INSTANCE = World.getInstance().getNpcById(Config.AUCTION_NPC_ID);
        AuctionInventory.getInstance().restore();
        restoreAuction();
        ThreadPool.scheduleAtFixedRate(new checkRefaund(), 300000L, 60000L);
        Shutdown.getInstance().registerShutdownHandler(new Runnable()
        {
            @Override
            public void run()
            { AuctionInventory.getInstance().updateDatabase(); }
        } );
    }

    public void buyItem(Player player, AuctionItem item)
    {
        if (!Utils.haveCountItem(player, item.id_price, item.price))
        {
            if(item.id_price == 57)
                player.sendMessage("У вас недостаточно аден для оплаты товара.");
            else
                player.sendMessage("У вас недостаточно средств для оплаты товара.");
            return;
        }
        try
        {
            player.destroyItemByItemId("Auction buy item.", item.id_price, item.price, null, false);
            //player.destroyItemWithoutTrace("Auction buy item.", item.id_price, item.price, null, false);
            transferItem(AuctionInventory.getInstance(), player.getInventory(), item.getObjectId(), player, "buy from auction");
            sendPayment(item);
        }
        catch (Exception e)
        {
            LOGGER.warn("Error in Auction create Item.");
        }
    }

    public void removeFromSale(Player target, AuctionItem aItem)
    {
        ItemInstance item = transferItem(AuctionInventory.getInstance(), target.getInventory(), aItem.getObjectId(), target, "remove from sale");
        if (item == null)
            LOGGER.error(new StringBuilder().append("Auction item objID - ").append(aItem.getObjectId()).append(" is null, seller: ").append(aItem.trader_objId).toString());
        else if (Config.AUCTION_LOG)
            LOGGER.info(AuctionLoggerUtils.getRemoveItemMessage(aItem, item, target));
        removeItem(aItem);
    }

    public void sendPayment(AuctionItem item)
    {
        Player player = World.getInstance().getPlayer(item.trader_objId);
        if (player == null)
            setSendPayment(item, true);
        else
            sendPayment(player, item);
    }

    public void sendPayment(Player player, AuctionItem item)
    {
        player.sendMessage("Один из ваших товаров на Аукционе был продан.");
        if (Config.AUCTION_LOG)
            LOGGER.info(AuctionLoggerUtils.getRemoveItemMessageAlreadySell(item));
        player.addItem("Auction payment.", item.id_price, item.price, null, true);
        removeItem(item);
    }

    private void refundItem(AuctionItem item)
    {
        Player player = World.getInstance().getPlayer(item.trader_objId);
        if (player != null)
            removeFromSale(player, item);
        else
            item.alreadySell = true;
    }

    public String showHeadPage(Player player, int page, int type)
    {
        String html = HtmCache.getInstance().getHtm("data/html/mods/CaracterAuction/Auction.htm");
        html = html.replace("%products%", getPage(player, page, type));
        html = html.replace("%pages%", getPages(player, page, type));
        return html;
    }

    public String showItem(Player player, AuctionItem item)
    {
        String html = HtmCache.getInstance().getHtm("data/html/mods/CaracterAuction/ShowItemInfo.htm");
        html = html.replace("%information%", item.getAcceptPage(player.getObjectId() == item.trader_objId, player));
        return html;
    }

    public String showCreateProductPage(Player player, int page)
    {
        String html = HtmCache.getInstance().getHtm("data/html/mods/CaracterAuction/CreateProduct.htm");
        html = html.replace("%page%", getPageAddProduct(player, page));
        return html;
    }

    public String showChoseProductPage(Player player, ItemInstance item)
    {
        String html = HtmCache.getInstance().getHtm("data/html/mods/CaracterAuction/ChoseProduct.htm");
        html = html.replace("%page%", getChoseAddProduct(item, player));
        return html;
    }

    public String getPage(Player player, int page, int type)
    {
        String str = "";
        try
        {
            if (_products.size() <= Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE)
                page = 1;
            List temp = new ArrayList();

            for (AuctionItem item : _products.values())
            {
                if ((item != null) && (checkItem(player, item, type)))
                    temp.add(item);
            }
            for (int i = page * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE - Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE; (i < page * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE) && (i < temp.size()); i++)
            {
                AuctionItem item = (AuctionItem)temp.get(i);
                str = new StringBuilder().append(str).append(item.getItemInfo(true, player)).toString();
            }
            if (str.isEmpty())
                str = ("Товары отсутствуют.").toString();
        }
        catch (Exception e)
        {
        }
        return str;
    }

    public String getPages(Player player, int page, int type)
    {
        String str = "<table width=270><tr><td align=center><table><tr>";
        int count = 0;
        for (AuctionItem item : _products.values())
        {
            if(checkItem(player, item, type))
                count++;
        }
        int maxPage = (int)Math.ceil(count / Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE);
        if(count > 0 && maxPage > 0)
        {
            if(count / maxPage != Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE)
                maxPage += 1;
            else if(count - (maxPage * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE) > 0)
                maxPage += 1;
        }
        int start = (maxPage > 6 && page - 3 > 0) ? page - 3 : 1;
        for(int i = start; (i <= maxPage && i <= page + 3); i++)
        {
            if(i != page)
            {
                if(i < page + 3)
                    str = new StringBuilder().append(str).append("<td width=15 align=center><a action=\"bypass -h Quest Auction page ").append(i).append(" ").append(type).append("\">").append(i).append("</a></td>").toString();
                else
                {
                    if(i >= maxPage)
                        continue;
                    if(i < maxPage - 1)
                        str = new StringBuilder().append(str).append("<td width=15 align=center><a action=\"bypass -h Quest Auction page ").append(maxPage - 1).append(" ").append(type).append("\">").append(maxPage - 1).append("</a></td>").toString();
                    else
                        str = new StringBuilder().append(str).append("<td width=15 align=center><a action=\"bypass -h Quest Auction page ").append(maxPage).append(" ").append(type).append("\">").append(maxPage).append("</a></td>").toString();
                }
            }
            else
                str = new StringBuilder().append(str).append("<td width=15 align=center>").append(i).append("</td>").toString();
        }
        str = new StringBuilder().append(str).append("</tr></table></td></tr></table>").toString();
        return str;
    }

    public String getPageAddProduct(Player player, int page)
    {
        String str = "";
        try
        {
            List temp = new ArrayList();
            for (ItemInstance item : player.getInventory().getItems())
            {
                if(qualItem(item))
                    temp.add(item);
            }
            if (temp.size() <= Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE)
                page = 1;
            for (int i = page * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE - Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE; i < page * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE; i++)
            {
                if (i >= temp.size())
                    break;
                ItemInstance item = (ItemInstance)temp.get(i);
                if (item != null)
                    str = new StringBuilder().append(str).append(getItemInfoForAddProduct(player, item, true)).toString();
            }
            str = new StringBuilder().append(str).append(getPagesForAddProduct(temp.size(), page)).toString();
            if (str.isEmpty())
                str = ("Товары отсутствуют.").toString();
        }
        catch (Exception e)
        {
        }
        return str;
    }

    public String getChoseAddProduct(ItemInstance item, Player player)
    {
        String str = "";
        str = new StringBuilder().append(str).append("<table width=250><tr>").toString();
        str = new StringBuilder().append(str).append("<td width=40 align=right><img src=\"").append(ItemIcons.getInstance().getIcon(item.getItemId())).append("\" width=32 height=32 align=right></td>").toString();
        str = new StringBuilder().append(str).append("<td width=230><table width=230><tr><td> ").append(item.getName()).append("</td></tr>").toString();        
        //str = new StringBuilder().append(str).append("<td width=230><table width=230><tr><td> ").append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("<font color=LEVEL> +").append(item.getEnchantLevel()).append("</font>").toString() : "").append("</td></tr>").toString();
	//str = new StringBuilder().append(str).append("<td width=230><table width=230><tr><td> ").append("<font color=603ca9>").append("Заточка: ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("</font>").append("<font color=LEVEL> +").append(item.getEnchantLevel()).append("</font>").toString() : "").append("</td></tr>").toString();        
        str = new StringBuilder().append(str).append(getCount(item, player)).toString();
        str = new StringBuilder().append(str).append(getEnchant(item, player)).toString();
        str = new StringBuilder().append(str).append(getAugment(item, player)).toString();
        str = new StringBuilder().append(str).append("</table></td></tr></table><br>").toString();
        str = new StringBuilder().append(str).append("<table width=300><tr>").toString();
        str = new StringBuilder().append(str).append("<tr><td align=\"right\">").append("Валюта:").append(" </td><td align=\"left\"><combobox width=100 var=\"reward\" list=\"").append(getAvailablePrice()).append("\"></td></tr>").toString();
        str = new StringBuilder().append(str).append("<tr><td align=\"right\">").append("Цена:").append(" </td><td align=\"left\"><edit var=\"count\" width=100 height=10></td></tr>").toString();
        str = new StringBuilder().append(str).append("</tr></table><br>").toString();
        str = new StringBuilder().append(str).append("<table width=300><tr>").toString();
        str = new StringBuilder().append(str).append("<td align=center width=132><button value=\"").append("Выставить на продажу").append("\" action=\"bypass -h Quest Auction chose_accept ").append(item.getObjectId()).append(" $count $reward\" width=135 height=24 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"></td>").toString();
        str = new StringBuilder().append(str).append("<td align=center width=132><button value=\"").append("Отказаться").append("\" action=\"bypass -h Quest Auction page 1 0\" width=135 height=24 back=\"L2UI_CH3.bigbutton3_down\" fore=\"L2UI_CH3.bigbutton3\"></td>").toString();
        str = new StringBuilder().append(str).append("</tr></table>").toString();
        return str;
    }

    public String getAvailablePrice()
    {
        String rewards = "";
        for(int id : Config.AUCTION_ALLOWED_ITEM_ID)
            if (rewards.isEmpty())
                rewards = new StringBuilder().append(rewards).append(ItemData.getInstance().getTemplate(id).getName()).toString();
            else
                rewards = new StringBuilder().append(rewards).append(";").append(ItemData.getInstance().getTemplate(id).getName()).toString();
        return rewards;
    }

    public void choseAccept(Player player, ItemInstance item, int id_price, int price)
    {
        AuctionItem sellitem = new AuctionItem(player.getObjectId(), id_price, price, System.currentTimeMillis(), item);
        transferItem(player.getInventory(), AuctionInventory.getInstance(), item.getObjectId(), player, "add to auction");
        storeItem(sellitem, true);
        int auction_priceCount;
        int auction_priceId;
        if (Config.AUCTION_PERCENTAGE)
        {
            auction_priceId = id_price;
            auction_priceCount = price / 100 * Config.AUCTION_GET_PERCENT;
        }
        else
        {
            auction_priceId = item.isAugmented() ? Config.AUCTION_AUGMENT_PRICE[0] : Config.AUCTION_PRICE[0];
            auction_priceCount = item.isAugmented() ? Config.AUCTION_AUGMENT_PRICE[1] : Config.AUCTION_PRICE[1];
        }
        player.destroyItemWithoutTrace(auction_priceId, auction_priceCount);
    }

    public String getItemInfoForAddProduct(Player player, ItemInstance item, boolean urlBuy)
    {
        String str = "<table width=300><tr>";
        str = new StringBuilder().append(str).append("<td width=32><img src=\"").append(ItemIcons.getInstance().getIcon(item.getItemId())).append("\" width=32 height=32 align=left></td>").toString();
        str = new StringBuilder().append(str).append("<td width=250><table width=250>").toString();
        str = new StringBuilder().append(str).append("<tr><td> ").append(item.getName()).append(" ").append("</td></tr>").toString();
	//str = new StringBuilder().append(str).append("<tr><td> ").append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("<font color=LEVEL> +").append(item.getEnchantLevel()).append("</font>").toString() : "").append("</td></tr>").toString();
        //str = new StringBuilder().append(str).append("<td><<td> ").append("<font color=603ca9>").append("Количество: ").append(item.getCount()> 0 ? new StringBuilder().append("</font>").append("<font color=LEVEL> ").append(item.getCount()).append("</font>").toString() : "").append("</td></tr>").toString();
	//str = new StringBuilder().append(str).append("<tr><td>").append("<font color=603ca9>").append("Заточка: ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("</font>").append("<font color=LEVEL> +").append(item.getEnchantLevel()).append("</font>").toString() : "").append("</td></tr>").toString();
        str = new StringBuilder().append(str).append(getCount(item, player)).toString();
        str = new StringBuilder().append(str).append(getEnchant(item, player)).toString();
	str = new StringBuilder().append(str).append(getAugment(item, player)).toString();        
        if (urlBuy)
            str = new StringBuilder().append(str).append("<tr><td><a action=\"bypass -h Quest Auction chose ").append(item.getObjectId()).append("\">").append("Продать").append("</a></td></tr>").toString();
        str = new StringBuilder().append(str).append("</table></td></tr></table>").toString();
        return str;
    }

    public static String getAugment(ItemInstance item, Player player)
    {
        String augmentInfo = "";
        if(Config.AUCTION_ALLOW_SALE_AUGUMENT_ITEMS)
        {
            if(item.isAugmented() && item.getAugmentation().getSkill() != null)
            {
                L2Skill skill = item.getAugmentation().getSkill();
                String type;
                //String type;
                if(skill.isChance())
                    type = "Chance";
                else if(skill.isActive())
                    type = "Active";
                else
                    type = "Passive";
                augmentInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Аугмент:").append("</font> <font color=LEVEL>").append(skill.getName()).append(" - ").append(skill.getLevel()).append(" level</font> <font color=00ff00>[").append(type).append("]</font></td></tr>").toString();
            }
            else
                augmentInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Аугмент:").append("</font> <font color=LEVEL>").append("нет").append("</font></td></tr>").toString();
        }
        return augmentInfo;
    }
	
    public static String getEnchant(ItemInstance item, Player player)
    {
        String enchantInfo = "";
        if(Config.AUCTION_ALLOW_SALE_ENCHANT_ITEMS)
        {
			if (item.getEnchantLevel() > 0)
				enchantInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Заточка:").append("</font> <font color=LEVEL> +").append(item.getEnchantLevel()).append("</font></td></tr>").toString();
			else
				enchantInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Заточка:").append("</font> <font color=LEVEL>").append("нет").append("</font></td></tr>").toString();
        }
        return enchantInfo;
    }
    
    public static String getCount(ItemInstance item, Player player)
    {
        String countInfo = "";
        
	if (item.getCount() > 0)
		countInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Количество:").append("</font> <font color=LEVEL> ").append(item.getCount()).append("</font></td></tr>").toString();
			else
                            countInfo = new StringBuilder().append("<tr><td><font color=603ca9>").append("Количество:").append("</font> <font color=LEVEL>").append("1").append("</font></td></tr>").toString();
        
        return countInfo;
    }

    public String getPagesForAddProduct(int size, int page)
    {
        String str = "<table width=270><tr><td align=center><table><tr>";

        int countPage = (int)Math.ceil(size / Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE);
        if(size > 0 && countPage > 0)
        {
            if(size / countPage != Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE)
                countPage += 1;
            else if(size - (countPage * Config.AUCTION_SEE_COUNT_PRODUCTS_ON_PAGE) > 0)
                countPage += 1;
        }
        for (int i = 1; i <= countPage; i++)
        {
            if (i == page)
                str = new StringBuilder().append(str).append("<td width=12 align=center>").append(i).append("</td>").toString();
            else
                str = new StringBuilder().append(str).append("<td width=12 align=center><a action=\"bypass -h Quest Auction create_product ").append(i).append("\">").append(i).append("</a></td>").toString();
        }
        str = new StringBuilder().append(str).append("</tr></table></td></tr></table>").toString();
        return str;
    }

    public void restoreAuction()
    {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con =  ConnectionPool.getConnection();
            statement = con.prepareStatement("SELECT * FROM `character_item_auction`");
            rset = statement.executeQuery();
            int count = 0;
            while (rset.next())
            {
                int trader_objId = rset.getInt("trader_objId");
                int objId = rset.getInt("objId");
                int id_price = rset.getInt("id_price");
                int price = rset.getInt("price");
                long addTime = rset.getLong("addTime");
                boolean alreadySell = rset.getBoolean("alreadySell");
                AuctionItem item = new AuctionItem(trader_objId, id_price, price, addTime, objId, alreadySell);
                storeItem(item, false);
                count++;
            }
            rset.close();
            statement.close();
            _log.info(new StringBuilder().append("Auction: загружено ").append(count).append(" итемов на аукционах.").toString());
        }
        catch (Exception e)
        {
            _log.info(new StringBuilder().append("Auction: Невозможно восстановить предметы аукционов. Ошибка: ").append(e).toString());
        }
        finally
        {
            DbUtils.close(new Object[] { con, statement, rset });
        }
    }

    private void setSendPayment(AuctionItem item, boolean insertToBD)
    {
        Connection con = null;
        if (insertToBD)
        {
            try
            {
                con =  ConnectionPool.getConnection();
                try (PreparedStatement statement = con.prepareStatement("UPDATE `character_item_auction` set `alreadySell` = ? where objId = ?")) {
                    statement.setBoolean(1, true);
                    statement.setInt(2, item.getObjectId());
                    statement.execute();
                }
                if (Config.AUCTION_LOG)
                    _log.info(new StringBuilder().append("Auction: Item payment=true: ").append(item.getObjectId()).toString());
            }
            catch (Exception e)
            {
                _log.info(new StringBuilder().append("Auction: Could update Item: ").append(e.getLocalizedMessage()).toString());
            }
            finally
            {
                DbUtils.close(new Object[]{con});
            }
        }
        item.alreadySell = true;
    }

    private void storeItem(AuctionItem item, boolean insertToBD)
    {
        _products.put(Integer.valueOf(item.getObjectId()), item);
        Connection con = null;
        PreparedStatement statement = null;
        if (insertToBD)
            try
            {
                con =  ConnectionPool.getConnection();
                statement = con.prepareStatement("INSERT INTO `character_item_auction` (objId, trader_objId, id_price, price, addTime, alreadySell) values(?,?,?,?,?,?)");
                statement.setInt(1, item.getObjectId());
                statement.setInt(2, item.trader_objId);
                statement.setInt(3, item.id_price);
                statement.setInt(4, item.price);
                statement.setLong(5, item.addTime);
                statement.setBoolean(6, item.alreadySell);
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                _log.info(new StringBuilder().append("Auction: Could not store Item: ").append(e.getLocalizedMessage()).toString());
            }
            finally
            {
                DbUtils.close(new Object[] { con, statement });
            }
    }

    private void removeItem(AuctionItem item)
    {
        try
        {
            Connection con = null;
            PreparedStatement statement = null;
            try
            {
                con =  ConnectionPool.getConnection();
                statement = con.prepareStatement("DELETE FROM `character_item_auction` where objId = ?");
                statement.setInt(1, item.getObjectId());
                statement.execute();
                statement.close();
            }
            catch (Exception e)
            {
                _log.info(new StringBuilder().append("Auction: Could remove Item: ").append(e.getLocalizedMessage()).toString());
            }
            finally
            {
                DbUtils.close(new Object[] { con, statement });
            }
            _products.remove(Integer.valueOf(item.getObjectId()));
        }
        catch (Exception e)
        {
        }
    }

    private int getRewardId(String name)
    {
        for (int id : Config.AUCTION_ALLOWED_ITEM_ID)
        {
            if (ItemData.getInstance().getTemplate(id).getName().equals(name))
                return id;
        }
        return 57;
    }

    public boolean qualItem(ItemInstance item)
    {
        if(item != null)
        {
            if(item.isSellable())
            {
                if(Config.AUCTION_ALLOW_SALE_MATERIALS && item.getEtcItem().getItemType() == EtcItemType.MATERIAL)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_RECIPE && item.getEtcItem().getItemType() == EtcItemType.RECIPE)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_ENCHANT_ITEMS && item.getEnchantLevel() > 0)
                    return true;
                //if(Config.AUCTION_ALLOW_SALE_BOOKS && item.getItemName().startsWith("Spellbook:"))
				if(Config.AUCTION_ALLOW_SALE_BOOKS && item.getItemName().startsWith("Amulet:") || item.getItemName().startsWith("Spellbook:") || item.getItemName().startsWith("Spellbook - ") || item.getItemName().startsWith("Ancient Tactical Manual:") || item.getItemName().startsWith("Ancient Spellbook:"))				
                    return true;
				//if(Config.AUCTION_ALLOW_SALE_NO_GRADE && (item.isWeapon() || item.isArmor()) && !(item.getItem().isCrystallizable()))
                //if(Config.AUCTION_ALLOW_SALE_NO_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().getCrystalType() == CrystalType.NONE)				
                    //return true;
                if(Config.AUCTION_ALLOW_SALE_D_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().isCrystallizable() && item.getItem().getCrystalType() == CrystalType.D)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_C_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().isCrystallizable() && item.getItem().getCrystalType() == CrystalType.C)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_B_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().isCrystallizable() && item.getItem().getCrystalType() == CrystalType.B)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_A_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().isCrystallizable() && item.getItem().getCrystalType() == CrystalType.A)
                    return true;
                if(Config.AUCTION_ALLOW_SALE_S_GRADE && (item.isWeapon() || item.isArmor()) && item.getItem().isCrystallizable() && item.getItem().getCrystalType() == CrystalType.S)
                    return true;
            }
            else if(Config.AUCTION_ALLOW_SALE_AUGUMENT_ITEMS && item.isAugmented() && !_products.containsKey(item.getObjectId()))
                return true;
        }
        return false;
    }

    public boolean checkItem(Player player, AuctionItem item, int type)
    {
        boolean ok = !item.alreadySell;

        if (ok)
        {
            Item itm = ItemData.getInstance().getTemplate(item.getId());
            ok = (ok) && (itm != null);
            if (ok)
            {
                if(itm != null)
                {
                    switch (type)
                    {
                        case 1:
                            ok = itm.getType2() == 0;
                            break;
                        case 2:
                            ok = (itm.getType2() == 2) || (itm.getType2() == 1);
                            break;
                        case 3:
                            ok = item.trader_objId == player.getObjectId();
                    }
                }
            }
        }
        return ok;
    }

    public static ItemInstance transferItem(ItemContainer src, ItemContainer dst, int objId, Player player, String dec)
    {
        ItemInstance item = src.getItemByObjectId(objId);
        InventoryUpdate iu = new InventoryUpdate();
        if (item != null)
        {
            if(item.isEquipped())
            {
                player.useEquippableItem(item, true);
            }
            src.transferItem("Auction", objId, item.getCount(), dst, player, null);
            String sm;
            if (src.getOwnerId() == player.getObjectId())
            {
                iu.addRemovedItem(item);
                sm = (" выставлен на аукцион." +item.getName()).toString();
                if (Config.AUCTION_LOG)
                    _logAuction.info(AuctionLoggerUtils.getTransferItemMessage(player, AuctionInventory.getInstance().getOwner(), item, dec));
            }
            else
            {
                iu.addNewItem(item);
                sm = ("Вы получили " + item.getCount() + " " + item.getName()).toString();
                if (Config.AUCTION_LOG)
					_logAuction.info(AuctionLoggerUtils.getTransferItemMessage(AuctionInventory.getInstance().getOwner(), player, item, dec));
            }
            player.sendPacket(iu);
            player.sendMessage(sm);
        }
        return item;
    }

    public class checkRefaund implements Runnable
    {
        public checkRefaund()
        {
        }

        @Override
        public void run()
        {
            for (AuctionItem item : _products.values())
                if ((item.isOverdue()) || (item.alreadySell))
                    refundItem(item);
        }
    }
	
	public static void main(String args[])
	{
		new Auction();
		//new Auction(-1, "Auction", "custom");
	}
}
