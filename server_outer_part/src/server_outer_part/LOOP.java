package server_outer_part;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.scheduler.BukkitRunnable;

public class LOOP {

	public void main(Server server, Person_splitter person_splitter) {
		// Called first when the Programm starts!
		// Explanation:
		// Run Task Later is used in order to Run the world-Creation
		// Syncroniously because else the Server isn't creaing the world
		// Creating every world hapens on the Server-Start. Every 30 Seconds the
		// worlds are updated with an mysql-Databse
		// In an earlyer Version of this Plugin MultiVerse Core was used so if
		// you find something from Multiverse please delete it
		//
		//
		//
		//
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// All things put in here are checked every 30 Seconds!
				// Get world list
				Person_splitter.worlds = MYSQL_CONNECTOR_OPTIONS.get_worlds();
				List<String> worlds = Person_splitter.worlds;
				if (Person_splitter.debug) {
					person_splitter.getLogger().info("Now Checking for information for worlds again!");
				}
				try {

					for (int i1 = 1; i1 < worlds.size() - 1; i1 += 6) {

						Boolean worldexists = false;

						try {
							World worldtemp = Person_splitter.server.getWorld(worlds.get(i1 + 5) + worlds.get(i1));
							if(worldtemp!=null){
								worldexists=true;
							}
						} catch (Exception e) {
							worldexists = false;
						}
						if (!worldexists) {
							WorldCreator c = new WorldCreator(worlds.get(i1 + 5) + worlds.get(i1));
							if (worlds.get(i1 + 5).equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds.get(i1 + 5).equals("flat")) {
								c.type(WorldType.FLAT);
							} else if (worlds.get(i1 + 5).equals("nether")) {
								c.environment(Environment.NETHER);
							} else if (worlds.get(i1 + 5).equals("end")) {
								c.environment(Environment.THE_END);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										person_splitter.getLogger().info("Creating world " + c.name());
										person_splitter.getLogger().info("" + server.createWorld(c));
										person_splitter.getLogger().info("Done!");
									} else {
										server.createWorld(c);
									}
								}
							}, 0L);// End of creation
						} else {
							if (Person_splitter.debug) {
								Person_splitter.logger
										.info("world " + worlds.get(i1 + 5) + worlds.get(i1) + " already existing!");
							}
						}
					} // End of for
					if (Person_splitter.debug) {
						person_splitter.getLogger()
								.info("Created all new worlds! Becasuse of run task later creation may be later!");
					}
					// Unloading of the world happens in the
					// MYSQL_CONNECTOR_OPTIONS.worlds() automatically
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 30000, 30000);

	}

	public BukkitRunnable createWorld(WorldCreator c, Server server) {
		try {
			server.createWorld(c);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}