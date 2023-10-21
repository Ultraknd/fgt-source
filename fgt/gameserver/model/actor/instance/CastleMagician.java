package fgt.gameserver.model.actor.instance;

import fgt.gameserver.data.manager.SevenSignsManager;
import fgt.gameserver.enums.CabalType;
import fgt.gameserver.enums.ZoneId;
import fgt.gameserver.enums.actors.NpcTalkCond;
import fgt.gameserver.enums.skills.EffectType;
import fgt.gameserver.model.actor.Player;
import fgt.gameserver.model.actor.template.NpcTemplate;
import fgt.gameserver.network.serverpackets.ActionFailed;
import fgt.gameserver.network.serverpackets.NpcHtmlMessage;

public class CastleMagician extends Folk
{
	public CastleMagician(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		final NpcTalkCond condition = getNpcTalkCond(player);
		if (condition == NpcTalkCond.NONE)
			html.setFile("data/html/castlemagician/magician-no.htm");
		else if (condition == NpcTalkCond.UNDER_SIEGE)
			html.setFile("data/html/castlemagician/magician-busy.htm");
		else
		{
			if (val == 0)
				html.setFile("data/html/castlemagician/magician.htm");
			else
				html.setFile("data/html/castlemagician/magician-" + val + ".htm");
		}
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			showChatWindow(player, val);
		}
		else if (command.equals("gotoleader"))
		{
			if (player.getClan() != null)
			{
				Player clanLeader = player.getClan().getLeader().getPlayerInstance();
				if (clanLeader == null)
					return;
				
				if (clanLeader.getFirstEffect(EffectType.CLAN_GATE) != null)
				{
					if (!validateGateCondition(clanLeader, player))
						return;
					
					player.teleportTo(clanLeader.getX(), clanLeader.getY(), clanLeader.getZ(), 0);
					return;
				}
				String filename = "data/html/castlemagician/magician-nogate.htm";
				showChatWindow(player, filename);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	protected NpcTalkCond getNpcTalkCond(Player player)
	{
		if (getCastle() != null && player.getClan() != null)
		{
			if (getCastle().getSiegeZone().isActive())
				return NpcTalkCond.UNDER_SIEGE;
			
			if (getCastle().getOwnerId() == player.getClanId())
				return NpcTalkCond.OWNER;
		}
		return NpcTalkCond.NONE;
	}
	
	private static final boolean validateGateCondition(Player clanLeader, Player player)
	{
		if (clanLeader.isAlikeDead() || clanLeader.isOperating() || clanLeader.isRooted() || clanLeader.isInCombat() || clanLeader.isInOlympiadMode() || clanLeader.isFestivalParticipant() || clanLeader.isInObserverMode() || clanLeader.isInsideZone(ZoneId.NO_SUMMON_FRIEND))
		{
			player.sendMessage("Couldn't teleport to clan leader. The requirements was not meet.");
			return false;
		}
		
		if (player.isIn7sDungeon())
		{
			final CabalType targetCabal = SevenSignsManager.getInstance().getPlayerCabal(clanLeader.getObjectId());
			if (SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				if (targetCabal != SevenSignsManager.getInstance().getWinningCabal())
				{
					player.sendMessage("Couldn't teleport to clan leader. The requirements was not meet.");
					return false;
				}
			}
			else
			{
				if (targetCabal == CabalType.NORMAL)
				{
					player.sendMessage("Couldn't teleport to clan leader. The requirements was not meet.");
					return false;
				}
			}
		}
		
		return true;
	}
}