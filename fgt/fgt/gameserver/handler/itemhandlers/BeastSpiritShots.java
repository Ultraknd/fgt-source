package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.enums.items.ShotType;
import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.Summon;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.MagicSkillUse;
import fgt.gameserver.network.serverpackets.SystemMessage;

public class BeastSpiritShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable == null)
			return;
		
		final Player player = playable.getActingPlayer();
		if (player == null)
			return;
		
		if (playable instanceof Summon)
		{
			player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
			return;
		}
		
		final Summon summon = player.getSummon();
		if (summon == null)
		{
			player.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return;
		}
		
		if (summon.isDead())
		{
			player.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return;
		}
		
		final int itemId = item.getItemId();
		final boolean isBlessed = (itemId == 6647);
		
		// shots are already active.
		if (summon.isChargedShot(isBlessed ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT))
			return;
		
		if (!player.destroyItemWithoutTrace(item.getObjectId(), summon.getSpiritShotsPerHit()))
		{
			if (!player.disableAutoShot(itemId))
				player.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS_FOR_PET);
			
			return;
		}
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addItemName(itemId));
		summon.setChargedShot(isBlessed ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, true);
		player.broadcastPacketInRadius(new MagicSkillUse(summon, summon, (isBlessed ? 2009 : 2008), 1, 0, 0), 600);
	}
}