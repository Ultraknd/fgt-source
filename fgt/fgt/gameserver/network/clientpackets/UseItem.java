package fgt.gameserver.network.clientpackets;

import fgt.Config;
import fgt.gameserver.enums.Paperdoll;
import fgt.gameserver.enums.items.ActionType;
import fgt.gameserver.enums.items.EtcItemType;
import fgt.gameserver.enums.items.WeaponType;
import fgt.gameserver.enums.skills.SkillType;
import fgt.gameserver.handler.IItemHandler;
import fgt.gameserver.handler.ItemHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.instance.Pet;
import fgt.gameserver.model.holder.IntIntHolder;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.network.SystemMessageId;
import fgt.gameserver.network.serverpackets.ItemList;
import fgt.gameserver.network.serverpackets.PetItemList;
import fgt.gameserver.network.serverpackets.SystemMessage;
import fgt.gameserver.scripting.Quest;
import fgt.gameserver.scripting.QuestState;
import fgt.gameserver.skills.L2Skill;

public final class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() != 0;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isOperating())
		{
			player.sendPacket(SystemMessageId.ITEMS_UNAVAILABLE_FOR_STORE_MANUFACTURE);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.sendPacket(SystemMessageId.CANNOT_PICKUP_OR_USE_ITEM_WHILE_TRADING);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		if (item == null)
			return;
		
		if (player.isItemDisabled(item))
			return;
		
		if (item.getItem().getType2() == Item.TYPE2_QUEST)
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_QUEST_ITEMS);
			return;
		}
		
		if (player.isAlikeDead() || player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAfraid())
			return;
		
		if (!Config.KARMA_PLAYER_CAN_TELEPORT && player.getKarma() > 0)
		{
			final IntIntHolder[] sHolders = item.getItem().getSkills();
			if (sHolders != null)
			{
				for (final IntIntHolder sHolder : sHolders)
				{
					final L2Skill skill = sHolder.getSkill();
					if (skill != null && (skill.getSkillType() == SkillType.TELEPORT || skill.getSkillType() == SkillType.RECALL))
						return;
				}
			}
		}
		
		if (player.isFishing() && item.getItem().getDefaultAction() != ActionType.fishingshot)
		{
			player.sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_3);
			return;
		}
		
		/*
		 * The player can't use pet items if no pet is currently summoned. If a pet is summoned and player uses the item directly, it will be used by the pet.
		 */
		if (item.isPetItem())
		{
			// If no pet, cancels the use
			if (!player.hasPet())
			{
				player.sendPacket(SystemMessageId.CANNOT_EQUIP_PET_ITEM);
				return;
			}
			
			final Pet pet = ((Pet) player.getSummon());
			
			if (!pet.canWear(item.getItem()))
			{
				player.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
				return;
			}
			
			if (pet.isDead())
			{
				player.sendPacket(SystemMessageId.CANNOT_GIVE_ITEMS_TO_DEAD_PET);
				return;
			}
			
			if (!pet.getInventory().validateCapacity(item))
			{
				player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
				return;
			}
			
			if (!pet.getInventory().validateWeight(item, 1))
			{
				player.sendPacket(SystemMessageId.UNABLE_TO_PLACE_ITEM_YOUR_PET_IS_TOO_ENCUMBERED);
				return;
			}
			
			player.transferItem("Transfer", _objectId, 1, pet.getInventory(), pet);
			
			// Equip it, removing first the previous item.
			if (item.isEquipped())
			{
				pet.getInventory().unequipItemInSlot(item.getLocationSlot());
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(item));
			}
			else
			{
				pet.getInventory().equipPetItem(item);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PUT_ON_S1).addItemName(item));
			}
			
			player.sendPacket(new PetItemList(pet));
			pet.updateAndBroadcastStatus(1);
			return;
		}
		
		if (!item.isEquipped())
		{
			if (!item.getItem().checkCondition(player, player, true))
				return;
		}
		
		if (item.isEquipable())
		{
			switch (item.getItem().getBodyPart())
			{
				case Item.SLOT_LR_HAND:
				case Item.SLOT_L_HAND:
				case Item.SLOT_R_HAND:
					if (player.isMounted())
					{
						player.sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
						return;
					}
					
					if (player.isCursedWeaponEquipped())
						return;
					
					player.getAI().tryToUseItem(_objectId);
					break;
				
				default:
					if (player.isCursedWeaponEquipped() && item.getItemId() == 6408) // Don't allow to put formal wear
						return;
					
					final ItemInstance itemToTest = player.getInventory().getItemByObjectId(_objectId);
					if (itemToTest == null)
						return;
					
					player.useEquippableItem(itemToTest, false);
					break;
			}
		}
		else
		{
			if (player.getAttackType() == WeaponType.FISHINGROD && item.getItem().getItemType() == EtcItemType.LURE)
			{
				player.getInventory().setPaperdollItem(Paperdoll.LHAND, item);
				player.broadcastUserInfo();
				
				sendPacket(new ItemList(player, false));
				return;
			}
			
			final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
			if (handler != null)
			{
				handler.useItem(player, item, _ctrlPressed);
				
				if (item.isEtcItem())
					player.disableItem(item, item.getEtcItem().getReuseDelay());
			}
			
			for (final Quest quest : item.getQuestEvents())
			{
				final QuestState state = player.getQuestList().getQuestState(quest.getName());
				if (state == null || !state.isStarted())
					continue;
				
				quest.notifyItemUse(item, player, player.getTarget());
			}
		}
	}
}