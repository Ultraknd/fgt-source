package fgt.gameserver.network.clientpackets;

import java.util.HashSet;
import java.util.Set;

import fgt.Config;
import fgt.gameserver.enums.actors.OperateType;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.trade.ItemRequest;
import fgt.gameserver.model.trade.TradeList;
import fgt.gameserver.network.SystemMessageId;

public final class RequestPrivateStoreBuy extends L2GameClientPacket
{
	private static final int BATCH_LENGTH = 12; // length of one item
	
	private int _storePlayerId;
	private Set<ItemRequest> _items = null;
	
	@Override
	protected void readImpl()
	{
		_storePlayerId = readD();
		int count = readD();
		if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * BATCH_LENGTH != _buf.remaining())
			return;
		
		_items = new HashSet<>();
		
		for (int i = 0; i < count; i++)
		{
			int objectId = readD();
			int cnt = readD();
			int price = readD();
			
			if (objectId < 1 || cnt < 1 || price < 0)
			{
				_items = null;
				return;
			}
			
			_items.add(new ItemRequest(objectId, cnt, price));
		}
	}
	
	@Override
	protected void runImpl()
	{
		if (_items == null)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null || player.isDead())
			return;
		
		if (player.isCursedWeaponEquipped())
			return;
		
		final Player storePlayer = World.getInstance().getPlayer(_storePlayerId);
		if (storePlayer == null || storePlayer.isDead())
			return;
		
		if (!player.isIn3DRadius(storePlayer, Npc.INTERACTION_DISTANCE))
			return;
		
		if (!(storePlayer.getOperateType() == OperateType.SELL || storePlayer.getOperateType() == OperateType.PACKAGE_SELL))
			return;
		
		final TradeList storeList = storePlayer.getSellList();
		if (storeList == null)
			return;
		
		if (!player.getAccessLevel().allowTransaction())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (storePlayer.getOperateType() == OperateType.PACKAGE_SELL && storeList.size() > _items.size())
			return;
		
		if (!storeList.privateStoreBuy(player, _items))
			return;
		
		if (storeList.isEmpty())
		{
			storePlayer.setOperateType(OperateType.NONE);
			storePlayer.broadcastUserInfo();
		}
	}
}