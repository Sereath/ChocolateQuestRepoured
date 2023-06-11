package team.cqr.cqrepoured.world.structure.generation;

import java.util.Properties;

import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonBase;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonGridCity;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonGuardedCastle;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonHangingCity;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonStrongholdLinear;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonStrongholdOpen;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonTemplateSurface;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonVegetatedCave;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonVolcano;

/**
 * Copyright (c) 29.04.2019 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 */
public enum EDungeonGenerator {

	TEMPLATE_SURFACE(DungeonTemplateSurface::new),
	//RANDOMIZED_CASTLE(DungeonRandomizedCastle::new),
	VOLCANO(DungeonVolcano::new),
	GRID_CITY(DungeonGridCity::new),
	HANGING_CITY(DungeonHangingCity::new),
	LINEAR_STRONGHOLD(DungeonStrongholdLinear::new),
	VEGETATED_CAVE(DungeonVegetatedCave::new),

	OPEN_STRONGHOLD(DungeonStrongholdOpen::new),
	GUARDED_CASTLE(DungeonGuardedCastle::new),

	ABANDONED((name, prop) -> null),
	RUIN((name, prop) -> null);

	private IDungeonGenerator generator;

	EDungeonGenerator(IDungeonGenerator generator) {
		this.generator = generator;
	}

	public DungeonBase createDungeon(String name, Properties prop) {
		return this.generator.createDungeon(name, prop);
	}

	public static EDungeonGenerator getDungeonGenerator(String toTest) {
		try {
			return EDungeonGenerator.valueOf(toTest.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@FunctionalInterface
	private interface IDungeonGenerator {
		DungeonBase createDungeon(String name, Properties prop);
	}

}
