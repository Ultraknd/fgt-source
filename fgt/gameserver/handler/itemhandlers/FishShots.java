package fgt.gameserver.handler.itemhandlers;

import fgt.gameserver.enums.items.ShotType;
import fgt.gameserver.enums.items.WeaponType;
import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.model.item.kind.Weapon;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.MagicSkillUse;

public class FishShots implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final Weapon weaponItem = player.getActiveWeaponItem();
		
		if (weaponInst == null || weaponItem.getItemType() != WeaponType.FISHINGROD)
			return;
		
		// Fishshot is already active
		if (player.isChargedShot(ShotType.FISH_SOULSHOT))
			return;
		
		// Wrong grade of soulshot for that fishing pole.
		if (weaponItem.getCrystalType() != item.getItem().getCrystalType())
		{
			player.sendPacket(SystemMessageId.WRONG_FISHINGSHOT_GRADE);
			return;
		}
		
		if (!player.destroyItemWithoutTrace(item.getObjectId(), 1))
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
			return;
		}
		
		final IntIntHolder[] skills = item.getItem().getSkills();
		
		player.setChargedShot(ShotType.FISH_SOULSHOT, true);
		player.broadcastPacket(new MagicSkillUse(player, skills[0].getId(), 1, 0, 0));
	}
}