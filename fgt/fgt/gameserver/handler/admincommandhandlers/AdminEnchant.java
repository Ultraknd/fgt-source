package fgt.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import fgt.gameserver.data.SkillTable;
import fgt.gameserver.data.xml.ArmorSetData;
import fgt.gameserver.enums.Paperdoll;
import fgt.gameserver.handler.IAdminCommandHandler;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.item.ArmorSet;
import fgt.gameserver.model.item.instance.ItemInstance;
import fgt.gameserver.model.item.kind.Armor;
import fgt.gameserver.model.item.kind.Item;
import fgt.gameserver.model.item.kind.Weapon;
import fgt.gameserver.network.serverpackets.InventoryUpdate;
import fgt.gameserver.skills.L2Skill;

public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_enchant"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken(); // skip command
		
		if (st.countTokens() == 2)
		{
			try
			{
				final Paperdoll paperdoll = Paperdoll.getEnumByName(st.nextToken());
				if (paperdoll == Paperdoll.NULL)
				{
					player.sendMessage("Unknown paperdoll slot.");
					return;
				}
				
				final int enchant = Integer.parseInt(st.nextToken());
				if (enchant < 0 || enchant > 65535)
				{
					player.sendMessage("You must set the enchant level between 0 - 65535.");
					return;
				}
				
				final Player targetPlayer = getTargetPlayer(player, true);
				
				final ItemInstance item = targetPlayer.getInventory().getItemFrom(paperdoll);
				if (item == null)
				{
					player.sendMessage(targetPlayer.getName() + " doesn't wear any item in " + paperdoll + " slot.");
					return;
				}
				
				final Item toTestItem = item.getItem();
				final int oldEnchant = item.getEnchantLevel();
				
				// Do nothing if both values are the same.
				if (oldEnchant == enchant)
				{
					player.sendMessage(targetPlayer.getName() + "'s " + toTestItem.getName() + " enchant is already set to " + enchant + ".");
					return;
				}
				
				item.setEnchantLevel(enchant);
				item.updateDatabase();
				
				// If item is equipped, verify the skill obtention/drop (+4 duals, +6 armorset).
				if (item.isEquipped())
				{
					final int currentEnchant = item.getEnchantLevel();
					
					// Skill bestowed by +4 duals.
					if (toTestItem instanceof Weapon)
					{
						// Old enchant was >= 4 and new is lower : we drop the skill.
						if (oldEnchant >= 4 && currentEnchant < 4)
						{
							final L2Skill enchant4Skill = ((Weapon) toTestItem).getEnchant4Skill();
							if (enchant4Skill != null)
							{
								targetPlayer.removeSkill(enchant4Skill.getId(), false);
								targetPlayer.sendSkillList();
							}
						}
						// Old enchant was < 4 and new is 4 or more : we add the skill.
						else if (oldEnchant < 4 && currentEnchant >= 4)
						{
							final L2Skill enchant4Skill = ((Weapon) toTestItem).getEnchant4Skill();
							if (enchant4Skill != null)
							{
								targetPlayer.addSkill(enchant4Skill, false);
								targetPlayer.sendSkillList();
							}
						}
					}
					// Add skill bestowed by +6 armorset.
					else if (toTestItem instanceof Armor)
					{
						// Old enchant was >= 6 and new is lower : we drop the skill.
						if (oldEnchant >= 6 && currentEnchant < 6)
						{
							// Check if player is wearing a chest item.
							final int itemId = targetPlayer.getInventory().getItemIdFrom(Paperdoll.CHEST);
							if (itemId > 0)
							{
								final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
								if (armorSet != null)
								{
									final int skillId = armorSet.getEnchant6skillId();
									if (skillId > 0)
									{
										targetPlayer.removeSkill(skillId, false);
										targetPlayer.sendSkillList();
									}
								}
							}
						}
						// Old enchant was < 6 and new is 6 or more : we add the skill.
						else if (oldEnchant < 6 && currentEnchant >= 6)
						{
							// Check if player is wearing a chest item.
							final int itemId = targetPlayer.getInventory().getItemIdFrom(Paperdoll.CHEST);
							if (itemId > 0)
							{
								final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
								if (armorSet != null && armorSet.isEnchanted6(targetPlayer)) // has all parts of set enchanted to 6 or more
								{
									final int skillId = armorSet.getEnchant6skillId();
									if (skillId > 0)
									{
										final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
										if (skill != null)
										{
											targetPlayer.addSkill(skill, false);
											targetPlayer.sendSkillList();
										}
									}
								}
							}
						}
					}
				}
				
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(item);
				targetPlayer.sendPacket(iu);
				
				targetPlayer.broadcastUserInfo();
				
				player.sendMessage(targetPlayer.getName() + "'s " + toTestItem.getName() + " enchant was modified from " + oldEnchant + " to " + enchant + ".");
			}
			catch (Exception e)
			{
				player.sendMessage("Please specify a new enchant value.");
			}
		}
		else
		{
			player.sendMessage("Usage: //enchant slot enchant");
			player.sendMessage("Slots: under|lear|rear|neck|lfinger|rfinger|head|rhand|lhand");
			player.sendMessage("Slots: gloves|chest|legs|feet|cloak|face|hair|hairall");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}