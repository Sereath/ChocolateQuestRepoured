package team.cqr.cqrepoured.item.staff;

import org.joml.Vector3d;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import team.cqr.cqrepoured.entity.projectiles.ProjectilePoisonSpell;
import team.cqr.cqrepoured.item.IRangedWeapon;
import team.cqr.cqrepoured.item.ItemLore;

public class ItemStaffPoison extends ItemLore implements IRangedWeapon {

	public ItemStaffPoison(Properties properties)
	{
		super(properties.durability(2048));
		//this.setMaxDamage(2048);
		//this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		this.shoot(stack, worldIn, playerIn, handIn);
		return ActionResult.success(stack);
	}

	public void shoot(ItemStack stack, World worldIn, PlayerEntity player, Hand handIn) {
		worldIn.playLocalSound(player.position().x, player.position().y, player.position().z, SoundEvents.SNOWBALL_THROW, SoundCategory.MASTER, 4.0F, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F, false);
		player.swing(handIn);

		if (!worldIn.isClientSide) {
			ProjectilePoisonSpell spell = new ProjectilePoisonSpell(player, worldIn);
			spell.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 2.0F, 0F);
			worldIn.addFreshEntity(spell);
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(handIn));
			player.getCooldowns().addCooldown(stack.getItem(), 20);
		}
	}

	@Override
	public void shoot(World worldIn, LivingEntity shooter, Entity target, Hand handIn) {
		shooter.swing(handIn);

		if (!worldIn.isClientSide) {
			ProjectilePoisonSpell spell = new ProjectilePoisonSpell(shooter, worldIn);
			Vector3d v = target.position().subtract(shooter.position());
			v = v.normalize();
			v = v.scale(2D);
			// spell.setVelocity(v.x, v.y, v.z);
			spell.setDeltaMovement(v);
			//spell.motionX = v.x;
			//spell.motionY = v.y;
			//spell.motionZ = v.z;
			//spell.velocityChanged = true;
			worldIn.addFreshEntity(spell);
		}
	}

	@Override
	public SoundEvent getShootSound() {
		return SoundEvents.SNOWBALL_THROW;
	}

	@Override
	public double getRange() {
		return 32.0D;
	}

	@Override
	public int getCooldown() {
		return 60;
	}

	@Override
	public int getChargeTicks() {
		return 0;
	}

}
