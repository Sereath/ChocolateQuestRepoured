package team.cqr.cqrepoured.item;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.item.ItemStack;
import team.cqr.cqrepoured.tileentity.TileEntityExporter;

public class ItemUnprotectedPositionTool extends ItemLore {

	private static final String POSITIONS_NBT_KEY = "positions";

	public ItemUnprotectedPositionTool(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
		if (!this.removePosition(stack, context.getClickedPos())) {
			this.addPosition(stack, context.getClickedPos());
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isCrouching()) {
			ItemStack stack = playerIn.getItemInHand(handIn);
			this.clearPositions(stack);
			return ActionResult.success(stack);
		}
		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		TileEntity tileEntity = player.level.getBlockEntity(pos);
		if (!(tileEntity instanceof TileEntityExporter)) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}
		TileEntityExporter exporter = (TileEntityExporter) tileEntity;
		if (!player.isCrouching()) {
			exporter.setUnprotectedBlocks(this.getPositions(itemstack).toArray(BlockPos[]::new));
		} else {
			BlockPos[] exporterPositions = exporter.getUnprotectedBlocks();
			this.clearPositions(itemstack);
			Arrays.stream(exporterPositions).forEach(p -> this.addPosition(itemstack, p));
		}
		return true;
	}

	public boolean hasPosition(ItemStack stack, BlockPos pos) {
		return this.getPositions(stack).anyMatch(pos::equals);
	}

	public void addPosition(ItemStack stack, BlockPos pos) {
		int[] data = new int[] { pos.getX(), pos.getY(), pos.getZ() };
		this.getOrCreatePositionTagList(stack).add(new IntArrayNBT(data));
	}

	public boolean removePosition(ItemStack stack, BlockPos pos) {
		ListNBT tagList = this.getPositionTagList(stack);
		if (tagList == null) {
			return false;
		}
		Iterator<INBT> iterator = tagList.iterator();
		while (iterator.hasNext()) {
			INBT tag = iterator.next();
			int[] data = ((IntArrayNBT) tag).getAsIntArray();
			if (data[0] == pos.getX() && data[1] == pos.getY() && data[2] == pos.getZ()) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	public void clearPositions(ItemStack stack) {
		if (!stack.hasTag()) {
			return;
		}
		CompoundNBT nbt = stack.getTag();
		nbt.remove(POSITIONS_NBT_KEY);
	}

	public Stream<BlockPos> getPositions(ItemStack stack) {
		ListNBT tagList = this.getPositionTagList(stack);
		if (tagList == null) {
			return Stream.empty();
		}
		return tagList.stream().map(tag -> {
			int[] data = ((IntArrayNBT) tag).getAsIntArray();
			return new BlockPos(data[0], data[1], data[2]);
		});
	}

	@Nullable
	private ListNBT getPositionTagList(ItemStack stack) {
		if (!stack.hasTag()) {
			return null;
		}
		CompoundNBT nbt = stack.getTag();
		if (!nbt.contains(POSITIONS_NBT_KEY, Constants.NBT.TAG_LIST)) {
			return null;
		}
		return nbt.getList(POSITIONS_NBT_KEY, Constants.NBT.TAG_INT_ARRAY);
	}

	private ListNBT getOrCreatePositionTagList(ItemStack stack) {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}
		CompoundNBT nbt = stack.getTag();
		if (!nbt.contains(POSITIONS_NBT_KEY, Constants.NBT.TAG_LIST)) {
			nbt.put(POSITIONS_NBT_KEY, new ListNBT());
		}
		return nbt.getList(POSITIONS_NBT_KEY, Constants.NBT.TAG_INT_ARRAY);
	}

}
