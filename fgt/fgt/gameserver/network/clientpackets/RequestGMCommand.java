package fgt.gameserver.network.clientpackets;

import fgt.gameserver.data.sql.ClanTable;
import fgt.gameserver.model.World;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.pledge.Clan;
import fgt.gameserver.network.serverpackets.GMHennaInfo;
import fgt.gameserver.network.serverpackets.GMViewCharacterInfo;
import fgt.gameserver.network.serverpackets.GMViewItemList;
import fgt.gameserver.network.serverpackets.GMViewPledgeInfo;
import fgt.gameserver.network.serverpackets.GMViewQuestList;
import fgt.gameserver.network.serverpackets.GMViewSkillInfo;
import fgt.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;

public final class RequestGMCommand extends L2GameClientPacket
{
	private String _targetName;
	private int _command;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_command = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// prevent non gm or low level GMs from viewing player stuff
		if (!player.isGM() || !player.getAccessLevel().allowAltG())
			return;
		
		final Player target = World.getInstance().getPlayer(_targetName);
		final Clan clan = ClanTable.getInstance().getClanByName(_targetName);
		
		if (target == null && (clan == null || _command != 6))
			return;
		
		switch (_command)
		{
			case 1: // target status
				sendPacket(new GMViewCharacterInfo(target));
				sendPacket(new GMHennaInfo(target));
				break;
			
			case 2: // target clan
				if (target != null && target.getClan() != null)
					sendPacket(new GMViewPledgeInfo(target.getClan(), target));
				break;
			
			case 3: // target skills
				sendPacket(new GMViewSkillInfo(target));
				break;
			
			case 4: // target quests
				sendPacket(new GMViewQuestList(target));
				break;
			
			case 5: // target inventory
				sendPacket(new GMViewItemList(target));
				sendPacket(new GMHennaInfo(target));
				break;
			
			case 6: // player or clan warehouse
				if (target != null)
					sendPacket(new GMViewWarehouseWithdrawList(target));
				else
					sendPacket(new GMViewWarehouseWithdrawList(clan));
				break;
		}
	}
}