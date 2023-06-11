package team.cqr.cqrepoured.item.armor;

import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.cqr.cqrepoured.item.ItemLore;

public class ItemBootsCloud extends ArmorItem {

	private final Multimap<Attribute, AttributeModifier> attributeModifier;
	
	//private AttributeModifier movementSpeed;

	public ItemBootsCloud(IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn, Properties prop) {
		super(materialIn, equipmentSlotIn, prop);

		//this.movementSpeed = new AttributeModifier("CloudBootsSpeedModifier", 0.15D, Operation.MULTIPLY_TOTAL);
		Multimap<Attribute, AttributeModifier> attributeMap = getDefaultAttributeModifiers(EquipmentSlotType.MAINHAND);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> modifierBuilder = ImmutableMultimap.builder();
		modifierBuilder.putAll(attributeMap);
		modifierBuilder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier("CloudBootsSpeedModifier", 0.15D, Operation.MULTIPLY_TOTAL));
		this.attributeModifier = modifierBuilder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return slot == MobEntity.getEquipmentSlotForItem(stack) ? this.attributeModifier : super.getAttributeModifiers(slot, stack);
	/*	Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == MobEntity.getEquipmentSlotForItem(stack)) {
			multimap.put(Attributes.MOVEMENT_SPEED, this.movementSpeed);
		}

		return multimap; */
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		super.onArmorTick(stack, world, player);
		
		player.addEffect(new EffectInstance(Effects.JUMP, 0, 4, false, false));

		player.flyingSpeed += 0.04F; //Correct replacement?
		if (player.fallDistance > 0.0F || player.isSprinting()) {
			if(!world.isClientSide) {
				((ServerWorld)world).sendParticles(ParticleTypes.CLOUD, player.position().x, player.position().y, player.position().y, 3, (random.nextFloat() - 0.5F) / 2.0F, -0.5D, (random.nextFloat() - 0.5F) / 2.0F, 1);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		ItemLore.addHoverTextLogic(tooltip, flagIn, this.getRegistryName().getPath());
	}

}
