package fgt.gameserver.model.spawn;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import fgt.commons.logging.CLogger;
import fgt.gameserver.data.manager.DayNightManager;
import fgt.gameserver.data.xml.NpcData;
import fgt.gameserver.model.actor.template.NpcTemplate;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;


public class Spawnlist
{
    int npce;
    private static final CLogger _log = new CLogger(Spawnlist.class.getName());

    private final Set<Spawn> _spawntable = ConcurrentHashMap.newKeySet();

    public static Spawnlist getInstance()
    {
        return SingletonHolder._instance;
    }

    protected Spawnlist()
    {
        fillSpawnTablee();
    }

    public Set<Spawn> getSpawnTable()
    {
        return _spawntable;
    }


    private void fillSpawnTablee()
    {
        try
        {
            final File f = new File("./data/xml/spawnlist.xml");
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            Spawn spawnDat;
            NpcTemplate template1;

            final Node n = doc.getFirstChild();
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
            {
                if (d.getNodeName().equalsIgnoreCase("spawnlist"))
                {
                    NamedNodeMap attrs = d.getAttributes();
                    npce = Integer.parseInt(attrs.getNamedItem("npc_templateid").getNodeValue());
                    template1 = NpcData.getInstance().getTemplate(npce);

                    spawnDat = new Spawn(template1);
                    spawnDat.setLoc(Integer.parseInt(attrs.getNamedItem("locx").getNodeValue()), Integer.parseInt(attrs.getNamedItem("locy").getNodeValue()), Integer.parseInt(attrs.getNamedItem("locz").getNodeValue()), Integer.parseInt(attrs.getNamedItem("heading").getNodeValue()));
                    spawnDat.setRespawnDelay(Integer.parseInt(attrs.getNamedItem("respawn_delay").getNodeValue()));
                    spawnDat.setRespawnRandom(Integer.parseInt(attrs.getNamedItem("respawn_rand").getNodeValue()));
                    int periodOfDay = Integer.parseInt(attrs.getNamedItem("periodOfDay").getNodeValue());

                    switch (periodOfDay)
                    {
                        case 0: // default
                            spawnDat.run();
                            break;

                        case 1: // Day
                            DayNightManager.getInstance().addDayCreature(spawnDat);
                            break;

                        case 2: // Night
                            DayNightManager.getInstance().addNightCreature(spawnDat);
                            break;
                    }
                    _spawntable.add(spawnDat);
                }
            }
        }
        catch (Exception e)
        {
            _log.warn("SpawnTable: Data missing in NPC table for ID: " + npce + ".");
        }

        _log.info("Loaded " + _spawntable.size() + " Npc Spawn Locations.");
    }


    public void reloadAll()
    {
        _spawntable.clear();
        fillSpawnTablee();
    }


    private static class SingletonHolder
    {
        protected static final Spawnlist _instance = new Spawnlist();
    }
}