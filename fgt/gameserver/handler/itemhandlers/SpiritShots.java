package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.enums.items.ShotType;
import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.model.item.kind.Weapon;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.MagicSkillUse;

public class SpiritShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final Weapon weaponItem = player.getActiveWeaponItem();
		
		// Check if sps can be used
		if (weaponInst == null || weaponItem.getSpiritShotCount() == 0)
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.CANNOT_USE_SPIRITSHOTS);
			
			return;
		}
		
		// Check if sps is already active
		if (player.isChargedShot(ShotType.SPIRITSHOT))
			return;
		
		if (weaponItem.getCrystalType() != item.getItem().getCrystalType())
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
			
			return;
		}
		
		// Consume sps if player has enough of them
		if (!player.destroyItemWithoutTrace(item.getObjectId(), weaponItem.getSpiritShotCount()))
		{
			if (!player.disableAutoShot(item.getItemId()))
				player.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS);
			
			return;
		}
		
		final IntIntHolder[] skills = item.getItem().getSkills();
		
		player.sendPacket(SystemMessageId.ENABLED_SPIRITSHOT);
		player.setChargedShot(ShotType.SPIRITSHOT, true);
		player.broadcastPacketInRadius(new MagicSkillUse(player, player, skills[0].getId(), 1, 0, 0), 600);
	}
}