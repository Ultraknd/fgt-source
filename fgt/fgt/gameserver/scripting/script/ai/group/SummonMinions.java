package fgt.gameserver.scripting.script.ai.group;

import java.util.HashMap;
import java.util.Map;

import fgt.commons.random.Rnd;

import fgt.gameserver.enums.ScriptEventType;
import fgt.gameserver.model.actor.Creature;
import fgt.gameserver.model.actor.Npc;
import fgt.gameserver.scripting.script.ai.AttackableAIScript;
import fgt.gameserver.skills.L2Skill;

/**
 * Summon minions the first time being hitten.<br>
 * For Orcs case, send also a message.
 */
public class SummonMinions extends AttackableAIScript
{
	private static final String[] ORCS_WORDS =
	{
		"Come out, you children of darkness!",
		"Destroy the enemy, my brothers!",
		"Show yourselves!",
		"Forces of darkness! Follow me!"
	};
	
	private static final Map<Integer, int[]> MINIONS = new HashMap<>();
	
	static
	{
		MINIONS.put(20767, new int[]
		{
			20768,
			20769,
			20770
		}); // Timak Orc Troop
		MINIONS.put(21524, new int[]
		{
			21525
		}); // Blade of Splendor
		MINIONS.put(21531, new int[]
		{
			21658
		}); // Punishment of Splendor
		MINIONS.put(21539, new int[]
		{
			21540
		}); // Wailing of Splendor
	}
	
	public SummonMinions()
	{
		super("ai/group");
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(MINIONS.keySet(), ScriptEventType.ON_ATTACK);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isScriptValue(0))
		{
			final int npcId = npc.getNpcId();
			if (npcId != 20767)
			{
				for (int val : MINIONS.get(npcId))
				{
					final Npc newNpc = addSpawn(val, npc, true, 0, false);
					newNpc.forceAttack(attacker, 200);
				}
			}
			else
			{
				for (int val : MINIONS.get(npcId))
					addSpawn(val, npc, true, 0, false);
				
				npc.broadcastNpcSay(Rnd.get(ORCS_WORDS));
			}
			npc.setScriptValue(1);
		}
		
		return super.onAttack(npc, attacker, damage, skill);
	}
}