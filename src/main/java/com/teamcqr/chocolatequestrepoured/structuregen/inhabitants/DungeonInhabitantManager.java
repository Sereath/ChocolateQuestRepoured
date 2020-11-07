package com.teamcqr.chocolatequestrepoured.structuregen.inhabitants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.factions.CQRFaction;
import com.teamcqr.chocolatequestrepoured.factions.FactionRegistry;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.DungeonGenUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;

public class DungeonInhabitantManager {

	public static final DungeonInhabitant DEFAULT_DUNGEON_INHABITANT = new DungeonInhabitant(EDefaultInhabitants.DEFAULT);
	private static final DungeonInhabitantManager INSTANCE = new DungeonInhabitantManager();

	private final Random random = new Random();
	private final Map<String, DungeonInhabitant> inhabitantMapping = new HashMap<>();
	// The entry "default" (or similar) is not allowed to be put in the distantMapping
	private final List<List<String>> distantMapping = new ArrayList<>();

	private DungeonInhabitantManager() {

	}

	public static DungeonInhabitantManager instance() {
		return INSTANCE;
	}

	public void loadDungeonInhabitants() {
		this.inhabitantMapping.clear();
		this.loadDefaultInhabitants();
		this.loadInhabitantConfigs();
		this.loadDistantMapping();
		this.inhabitantMapping.put("DEFAULT", DEFAULT_DUNGEON_INHABITANT);
	}

	private void loadDefaultInhabitants() {
		for (EDefaultInhabitants defInha : EDefaultInhabitants.values()) {
			DungeonInhabitant inha = new DungeonInhabitant(defInha);
			this.inhabitantMapping.put(inha.getName().toUpperCase(), inha);
		}
	}

	private void loadInhabitantConfigs() {
		for (File file : FileUtils.listFiles(CQRMain.CQ_INHABITANT_FOLDER, new String[] { "cfg", "prop", "properties" }, true)) {
			try (InputStream inputStream = new FileInputStream(file)) {
				Properties prop = new Properties();
				prop.load(inputStream);

				try {
					DungeonInhabitant inha = new DungeonInhabitant(prop);
					this.inhabitantMapping.put(inha.getName().toUpperCase(), inha);
				} catch (Exception e) {
					CQRMain.logger.warn(String.format("Failed to create DungeonInhabitant object from file: %s", file.getName()), e);
				}
			} catch (IOException e) {
				CQRMain.logger.error(String.format("Failed to load file %s", file.getName()), e);
			}
		}
	}

	private void loadDistantMapping() {
		File file = new File(CQRMain.CQ_CONFIG_FOLDER, "defaultInhabitantConfig.properties");
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String currentLine;

				while ((currentLine = br.readLine()) != null) {
					if (currentLine.startsWith("#")) {
						continue;
					}

					String[] entries = currentLine.split(",");
					List<String> tmpList = new ArrayList<>();
					for (String s : entries) {
						s = s.trim();
						if (this.inhabitantMapping.containsKey(s) && !s.equalsIgnoreCase(DEFAULT_DUNGEON_INHABITANT.getName())) {
							tmpList.add(s);
						}
					}

					if (!tmpList.isEmpty()) {
						this.distantMapping.add(tmpList);
					}
				}
			} catch (IOException e) {
				CQRMain.logger.error(String.format("Failed to load file %s", file.getName()), e);
			}
		}
	}

	public DungeonInhabitant getInhabitant(String name) {
		return this.inhabitantMapping.getOrDefault(name, DEFAULT_DUNGEON_INHABITANT);
	}

	public DungeonInhabitant getInhabitantByDistance(World world, int blockX, int blockZ) {
		if (this.distantMapping.isEmpty()) {
			return (DungeonInhabitant) this.inhabitantMapping.values().toArray()[this.random.nextInt(this.inhabitantMapping.size())];
		}

		int x1 = blockX - DungeonGenUtils.getSpawnX(world);
		int z1 = blockZ - DungeonGenUtils.getSpawnZ(world);
		int distToSpawn = (int) Math.sqrt((double) (x1 * x1 + z1 * z1));
		int index = distToSpawn / CQRConfig.mobs.mobTypeChangeDistance;

		if (index >= this.distantMapping.size()) {
			index = this.random.nextInt(this.distantMapping.size());
		}
		List<String> tmpList = this.distantMapping.get(index);
		return this.getInhabitant(tmpList.get(this.random.nextInt(tmpList.size())));
	}

	public DungeonInhabitant getInhabitantByDistanceIfDefault(String name, World world, int blockX, int blockZ) {
		DungeonInhabitant dungeonInhabitant = this.getInhabitant(name);
		if (dungeonInhabitant != DEFAULT_DUNGEON_INHABITANT) {
			return dungeonInhabitant;
		}
		return this.getInhabitantByDistance(world, blockX, blockZ);
	}

	public List<DungeonInhabitant> getListOfFactionInhabitants(CQRFaction faction, World world) {
		List<DungeonInhabitant> result = new ArrayList<>();

		for (DungeonInhabitant inha : this.inhabitantMapping.values()) {
			if (!inha.getName().equalsIgnoreCase(DEFAULT_DUNGEON_INHABITANT.getName())) {
				if (inha.getFactionOverride() != null) {
					if (faction.equals(FactionRegistry.instance().getFactionInstance(inha.getFactionOverride()))) {
						result.add(inha);
					}
				} else {
					// Maybe change this because DungeonInhabitant#getEntityID() returns a random one?
					Entity entity = EntityList.createEntityByIDFromName(inha.getEntityID(), world);
					if (entity != null && FactionRegistry.instance().getFactionOf(entity).equals(faction)) {
						result.add(inha);
					}
				}
			}
		}

		return result;
	}

}
