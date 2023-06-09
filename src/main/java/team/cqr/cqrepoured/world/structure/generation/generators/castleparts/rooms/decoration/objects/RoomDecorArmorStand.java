//package team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.decoration.objects;
//
//import net.minecraft.entity.item.ArmorStandEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.math.vector.Vector3i;
//import net.minecraft.world.World;
//import team.cqr.cqrepoured.util.BlockStateGenArray;
//
//public class RoomDecorArmorStand extends RoomDecorEntityBase {
//	public RoomDecorArmorStand() {
//		super();
//		this.footprint.add(new Vector3i(0, 0, 0));
//		this.footprint.add(new Vector3i(0, 1, 0));
//	}
//
//	@Override
//	protected void createEntityDecoration(World world, BlockPos pos, BlockStateGenArray genArray, Direction side) {
//		// Need to add 0.5 to each position amount so it spawns in the middle of the tile
//		ArmorStandEntity stand = new ArmorStandEntity(world);
//		float rotation = side.getHorizontalAngle();
//		stand.setPosition((pos.getX() + 0.5), (pos.getY() + 0.5), (pos.getZ() + 0.5));
//		stand.rotationYaw = rotation;
//		genArray.addEntity(BlockPos.ORIGIN, stand);
//	}
//}
