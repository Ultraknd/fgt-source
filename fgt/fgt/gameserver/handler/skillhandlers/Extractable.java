package fgt.gameserver.handler.skillhandlers;

import fgt.commons.random.Rnd;

import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.handler.ISkillHandler;
import fgt.gameserver.model.WorldObject;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.extractable.ExtractableProductItem;
import fgt.gameserver.skills.extractable.ExtractableSkill;

public class Extractable implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.EXTRACTABLE,
		SkillType.EXTRACTABLE_FISH
	};
	
	@Override
	public void useSkill(Creature activeChar, L2Skill skill, WorldObject[] targets)
	{
		if (!(activeChar instanceof Player))
			return;
		
		final ExtractableSkill exItem = skill.getExtractableSkill();
		if (exItem == null || exItem.getProductItems().isEmpty())
		{
			LOGGER.warn("Missing informations for extractable skill id: {}.", skill.getId());
			return;
		}
		
		final Player player = activeChar.getActingPlayer();
		
		int chance = Rnd.get(100000);
		boolean created = false;
		for (ExtractableProductItem expi : exItem.getProductItems())
		{
			chance -= (int) (expi.getChance() * 1000);
			if (chance >= 0)
				continue;
			
			// The inventory is full, terminate.
			if (!player.getInventory().validateCapacityByItemIds(expi.getItems()))
			{
				player.sendPacket(SystemMessageId.SLOTS_FULL);
				return;
			}
			
			// Inventory has space, create all items.
			for (IntIntHolder item : expi.getItems())
			{
				player.addItem("Extract", item.getId(), item.getValue(), player, true);
				created = true;
			}
			
			break;
		}
		
		if (!created)
			player.sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}