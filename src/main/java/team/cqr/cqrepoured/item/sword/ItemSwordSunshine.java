package team.cqr.cqrepoured.item.sword;

import java.util.List;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.item.IItemTier;
import net.minecraft.world.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.cqr.cqrepoured.item.IEquipListener;
import team.cqr.cqrepoured.item.ItemLore;

public class ItemSwordSunshine extends ItemCQRWeapon implements IEquipListener {

	protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("126A7773-3B53-4AC4-8A90-1CD46C888FD4");
	private static final double DAMAGE_BONUS = 3.0D;

	public ItemSwordSunshine(IItemTier material, Item.Properties props) {
		super(material, props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		if (!(entityIn instanceof LivingEntity)) {
			return;
		}
		if (!isSelected) {
			return;
		}
		AttributeInstance attribute = ((LivingEntity) entityIn).getAttribute(Attributes.ATTACK_DAMAGE);
		if (worldIn.isDay()) {
			if (attribute.getModifier(ATTACK_DAMAGE_MODIFIER) != null) {
				return;
			}
			attribute.addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "sunshine_damage_bonus", DAMAGE_BONUS, Operation.ADDITION));
		} else {
			attribute.removeModifier(ATTACK_DAMAGE_MODIFIER);
		}
	}

	@Override
	public void onEquip(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {

	}

	@Override
	public void onUnequip(LivingEntity entity, ItemStack stack, EquipmentSlot slot) {
		AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
		attribute.removeModifier(ATTACK_DAMAGE_MODIFIER);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<TextComponent> tooltip, TooltipFlag flagIn) {
		ItemLore.addHoverTextLogic(tooltip, flagIn, this.getRegistryName().getPath());

		tooltip.add(new TranslationTextComponent("item.cqrepoured.sword_sunshine.attack_damage_at_day", 3).withStyle(ChatFormatting.GOLD));
	}

}
