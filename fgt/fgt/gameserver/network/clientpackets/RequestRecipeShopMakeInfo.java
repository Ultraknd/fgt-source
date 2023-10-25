package fgt.gameserver.network.clientpackets;

import fgt.gameserver.enums.actors.OperateType;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.network.serverpackets.RecipeShopItemInfo;

public final class RequestRecipeShopMakeInfo extends L2GameClientPacket
{
	private int _objectId;
	private int _recipeId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_recipeId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_objectId);
		if (manufacturer == null || manufacturer.getOperateType() != OperateType.MANUFACTURE)
			return;
		
		player.sendPacket(new RecipeShopItemInfo(manufacturer, _recipeId));
	}
}