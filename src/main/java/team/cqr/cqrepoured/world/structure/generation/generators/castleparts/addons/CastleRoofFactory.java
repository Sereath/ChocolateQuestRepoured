//package team.cqr.cqrepoured.world.structure.generation.generators.castleparts.addons;
//
//import net.minecraft.core.BlockPos;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.RandomCastleConfigOptions;
//
//public class CastleRoofFactory {
//	public static CastleAddonRoofBase createRoof(RandomCastleConfigOptions.RoofType type, BlockPos startPos, int sizeX, int sizeZ) {
//		switch (type) {
//		case TWO_SIDED:
//			return new CastleAddonRoofTwoSided(startPos, sizeX, sizeZ);
//		case SPIRE:
//			return new CastleAddonRoofSpire(startPos, sizeX, sizeZ);
//		case FOUR_SIDED:
//		default:
//			return new CastleAddonRoofFourSided(startPos, sizeX, sizeZ);
//		}
//	}
//}
