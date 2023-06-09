package team.cqr.cqrepoured.util;

import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class CraftingHelper {

	public static boolean areItemStacksEqualIgnoreCount(ItemStack stack1, ItemStack stack2, boolean ignoreMeta, boolean ignoreTag) {
		if (stack1.isEmpty() && stack2.isEmpty()) {
			return true;
		}
		if (stack1.isEmpty() != stack2.isEmpty()) {
			return false;
		}
		if (stack1.getItem() != stack2.getItem()) {
			return false;
		}
		if (!ignoreMeta && stack1.getDamageValue() != stack2.getDamageValue()) {
			return false;
		}
		if (!ignoreTag) {
			if (stack1.getTag() == null && stack2.getTag() == null) {
				return true;
			}
			if (stack1.hasTag() != stack2.hasTag()) {
				return false;
			}
			if (!stack1.getTag().equals(stack2.getTag())) {
				return false;
			}
			if (!stack1.areCapsCompatible(stack2)) {
				return false;
			}
		}
		return true;
	}

	public static boolean remove(Iterable<ItemStack> itemStacks, ItemStack stack, boolean simulate, boolean ignoreMeta, boolean ignoreTag) {
		int i = stack.getCount();
		for (ItemStack stack1 : itemStacks) {
			if (i == 0) {
				break;
			}
			if (CraftingHelper.areItemStacksEqualIgnoreCount(stack, stack1, ignoreMeta, ignoreTag)) {
				int j = Math.min(stack1.getCount(), i);
				i -= j;
				if (!simulate) {
					stack1.shrink(j);
				}
			}
		}
		return i == 0;
	}

	public static boolean remove(ItemStack[] itemStacks, ItemStack stack, boolean simulate, boolean ignoreMeta, boolean ignoreTag) {
		return remove(Arrays.asList(itemStacks), stack, simulate, ignoreMeta, ignoreTag);
	}

}
