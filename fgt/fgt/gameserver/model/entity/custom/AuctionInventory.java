package fgt.gameserver.model.entity.custom;

import fgt.Config;
import fgt.gameserver.enums.items.ItemLocation;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.itemcontainer.ItemContainer;
import fgt.gameserver.scripting.script.custom.Auction;

public class AuctionInventory extends ItemContainer
{
    private static AuctionInventory _insatnce;

    public static AuctionInventory getInstance()
    {
        if (_insatnce == null)
            _insatnce = new AuctionInventory();
        return _insatnce;
    }

    @Override
    public int getOwnerId()
    {
        return Config.AUCTION_NPC_ID;
    }

    @Override
    protected ItemLocation getBaseLocation()
    {
        return ItemLocation.INVENTORY;
    }

    @Override
    public Creature getOwner()
    {
        return Auction.AUCTIONER_INSTANCE;
    }
}
