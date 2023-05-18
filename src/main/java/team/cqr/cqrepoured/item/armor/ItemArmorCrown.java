package team.cqr.cqrepoured.item.armor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;
import team.cqr.cqrepoured.capability.armor.kingarmor.CapabilityDynamicCrown;
import team.cqr.cqrepoured.capability.armor.kingarmor.CapabilityDynamicCrownProvider;
import team.cqr.cqrepoured.item.ItemLore;

public class ItemArmorCrown extends GeoArmorItem implements IAnimatable {

	public static final String NBT_KEY_CROWN = "CQR Crown";

	public ItemArmorCrown(IArmorMaterial materialIn, Properties props) {
		super(materialIn, EquipmentSlotType.HEAD, props);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return CapabilityDynamicCrownProvider.createProvider();
	}

	@Nullable
	public Item getAttachedItem(ItemStack stack) {
		if (stack.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).isPresent()) {
			return stack.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).resolve().get().getAttachedItem();
		}
		return null;
	}

	public void attachItem(ItemStack crown, Item toAttach) {
		if (crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).isPresent()) {
			crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).resolve().get().attachItem(toAttach);
		}
	}

	public void attachItem(ItemStack crown, ItemStack toAttach) {
		if (crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).isPresent()) {
			crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).resolve().get().attachItem(toAttach);
		}
	}

	public void attachItem(ItemStack crown, ResourceLocation toAttach) {
		if (crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).isPresent()) {
			crown.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null).resolve().get().attachItem(toAttach);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		LazyOptional<CapabilityDynamicCrown> lOpCap = stack.getCapability(CapabilityDynamicCrownProvider.DYNAMIC_CROWN, null);
		if (lOpCap.isPresent()) {
			tooltip.add(new StringTextComponent("Attached helmet: " + new ItemStack(lOpCap.resolve().get().getAttachedItem()).getHoverName().getString()));
		}

		ItemLore.addHoverTextLogic(tooltip, flagIn, this.getRegistryName().getPath());
	}

	// TODO: Tooltip that shows the attachment
	// TODO: Stats are affected by attachment

	public static boolean hasCrown(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(NBT_KEY_CROWN, Constants.NBT.TAG_COMPOUND);
	}
	
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	@Override
	public void registerControllers(AnimationData data) {
		
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

}
