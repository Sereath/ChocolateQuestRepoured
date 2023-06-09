package team.cqr.cqrepoured.item.gun;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.item.UseAction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import team.cqr.cqrepoured.entity.projectiles.ProjectileBubble;
import team.cqr.cqrepoured.init.CQRSounds;
import team.cqr.cqrepoured.item.IRangedWeapon;
import team.cqr.cqrepoured.item.ItemLore;

import java.util.Random;

public class ItemBubblePistol extends ItemLore implements IRangedWeapon {

	private final Random rng = new Random();

	public ItemBubblePistol(Properties properties) {
		super(properties.durability(200));
		//this.setMaxDamage(this.getMaxUses());
		//this.setMaxStackSize(1);
	}

	//public int getMaxUses() {
	//	return 200;
	//}

	public double getInaccurary() {
		return 0.5D;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 10;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (entityLiving instanceof Player) {
			((Player) entityLiving).getCooldowns().addCooldown(this, this.getCooldown());
		}
		stack.hurtAndBreak(1, entityLiving, e -> e.broadcastBreakEvent(e.getUsedItemHand()));
		return super.finishUsingItem(stack, worldIn, entityLiving);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
		stack.hurtAndBreak(1, entityLiving, e -> e.broadcastBreakEvent(e.getUsedItemHand()));
		if (entityLiving instanceof Player) {
			((Player) entityLiving).getCooldowns().addCooldown(this, this.getCooldown());
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof LivingEntity && ((LivingEntity) entityIn).isUsingItem() && ((LivingEntity) entityIn).getUseItem() == stack) {
			this.shootBubbles((LivingEntity) entityIn);
		}
	}

	private void shootBubbles(LivingEntity entity) {
		double x = -Math.sin(Math.toRadians(entity.yRot));
		double z = Math.cos(Math.toRadians(entity.yRot));
		double y = -Math.sin(Math.toRadians(entity.xRot));
		this.shootBubbles(new Vec3(x, y, z), entity);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
	}

	private void shootBubbles(Vec3 velocity, LivingEntity shooter) {
		Vec3 v = new Vec3(-this.getInaccurary() + velocity.x + (2 * this.getInaccurary() * this.rng.nextDouble()), -this.getInaccurary() + velocity.y + (2 * this.getInaccurary() * this.rng.nextDouble()), -this.getInaccurary() + velocity.z + (2 * this.getInaccurary() * this.rng.nextDouble()));
		v = v.normalize();
		v = v.scale(1.4);

		shooter.playSound(CQRSounds.BUBBLE_BUBBLE, 1, 0.75F + (0.5F * shooter.getRandom().nextFloat()));

		ProjectileBubble bubble = new ProjectileBubble(shooter, shooter.level);
		bubble.setDeltaMovement(v);
		//bubble.motionX = v.x;
		///bubble.motionY = v.y;
		//bubble.motionZ = v.z;
		//bubble.velocityChanged = true;
		shooter.level.addFreshEntity(bubble);
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public void shoot(Level world, LivingEntity shooter, Entity target, InteractionHand hand) {
		this.shootBubbles(shooter);
	}

	@Override
	public SoundEvent getShootSound() {
		return SoundEvents.FISHING_BOBBER_THROW;
	}

	@Override
	public double getRange() {
		return 32.0D;
	}

	@Override
	public int getCooldown() {
		return 80;
	}

	@Override
	public int getChargeTicks() {
		return 0;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

}
