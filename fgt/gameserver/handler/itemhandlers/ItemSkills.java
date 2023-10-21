package fgt.gameserver.handler.itemhandlers;

import fgt.commons.util.ArraysUtil;

import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Playable;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Pet;
import fgt.gameserver.model.actor.instance.Servitor;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.skills.L2Skill;
import fgt.gameserver.skills.effects.EffectTemplate;

public class ItemSkills implements IItemHandler
{
	
	private static final int[] HP_POTION_SKILL_IDS =
	{
		2031, // Lesser Healing Potion
		2032, // Healing potion
		2037 // Greater Healing Potion
	};
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable instanceof Servitor)
			return;
		
		final boolean isPet = playable instanceof Pet;
		final Player player = playable.getActingPlayer();
		final Creature target = playable.getTarget() instanceof Creature ? (Creature) playable.getTarget() : null;
		
		// Pets can only use tradable items.
		if (isPet && !item.isTradable())
		{
			player.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return;
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
		{
			LOGGER.warn("{} doesn't have any registered skill for handler.", item.getName());
			return;
		}
		
		for (final IntIntHolder skillInfo : skills)
		{
			if (skillInfo == null)
				continue;
			
			final L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill == null)
				continue;
			
			if (!itemSkill.checkCondition(playable, target, false))
				return;
			
			// No message on retail, the use is just forgotten.
			if (playable.isSkillDisabled(itemSkill))
				return;
			
			// Potions and Energy Stones bypass the AI system. The rest does not.
			if (itemSkill.isPotion() || itemSkill.isSimultaneousCast())
			{
				playable.getCast().doInstantCast(itemSkill, item);
				
				if (!isPet && item.isHerb() && player.hasServitor())
					player.getSummon().getCast().doInstantCast(itemSkill, item);
			}
			else
				playable.getAI().tryToCast(target, itemSkill, forceUse, false, (item.isEtcItem() ? item.getObjectId() : 0));
			
			// Send message to owner.
			if (isPet)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(itemSkill));
			else
			{
				// Buff icon for healing potions.
				final int skillId = skillInfo.getId();
				if (ArraysUtil.contains(HP_POTION_SKILL_IDS, skillId) && skillId >= player.getShortBuffTaskSkillId())
				{
					final EffectTemplate template = itemSkill.getEffectTemplates().get(0);
					if (template != null)
					{
						player.shortBuffStatusUpdate(skillId, skillInfo.getValue(), template.getCounter() * template.getPeriod());
					}
				}
			}
		}
	}
}