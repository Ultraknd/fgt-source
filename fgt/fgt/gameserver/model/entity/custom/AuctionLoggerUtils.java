package fgt.gameserver.model.entity.custom;

import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.instance.ItemInstance;

public class AuctionLoggerUtils
{
    static String REMOVE_ITEM_PATTERN = "Remove item [objId: %objId% | itemId: %itemId% | enchant: %enchant% | augment: %augment% | seller: %seller% | target: %target%]";
    static String REMOVE_ITEM_PATTERN_ALREADY_SELL = "Remove item [objId: %objId% | seller: %seller%], already sell.";
    static String TRANSFER_ITEM_PATTERN = "Transfer item [sender: %sender% | target: %target% | objId: %objId% | itemId: %itemId%] description: %desc%";

    public static String getRemoveItemMessageAlreadySell(AuctionItem aItem)
    {
        String message = REMOVE_ITEM_PATTERN_ALREADY_SELL;
        message = message.replace("%objId%", Integer.toString(aItem.getObjectId()));
        message = message.replace("%seller%", Integer.toString(aItem.trader_objId));
        return message;
    }

    public static String getRemoveItemMessage(AuctionItem aItem, ItemInstance item, Player target)
    {
        String message = REMOVE_ITEM_PATTERN;
        message = message.replace("%objId%", Integer.toString(aItem.getObjectId()));
        message = message.replace("%itemId%", Integer.toString(item.getItemId()));
        message = message.replace("%enchant%", Integer.toString(item.getEnchantLevel()));
        message = message.replace("%augment%", item.getAugmentation() != null ? item.getAugmentation().getSkill().getName() : "not have");
        message = message.replace("%seller%", Integer.toString(aItem.trader_objId));
        message = message.replace("%target%", target != null ? Integer.toString(target.getObjectId()) : "already transfer");
        return message;
    }

    public static String getTransferItemMessage(Creature seller, Creature target, ItemInstance item, String desc)
    {
        String message = TRANSFER_ITEM_PATTERN;
        message = message.replace("%sender%", getTarget(seller));
        message = message.replace("%target%", getTarget(target));
        message = message.replace("%objId%", Integer.toString(item.getObjectId()));
        message = message.replace("%itemId%", Integer.toString(item.getItemId()));
        message = message.replace("%desc%", desc);
        return message;
    }

    public static String getTarget(Creature character)
    {
        if ((character instanceof Npc))
            return Integer.toString(AuctionInventory.getInstance().getOwnerId());
        return Integer.toString(((Creature)character).getObjectId());
    }
}
