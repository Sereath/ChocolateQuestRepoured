package team.cqr.cqrepoured.entity.ai.item;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.util.EntityUtil;

import java.util.EnumSet;
import java.util.List;

public class EntityAIHealingPotion extends AbstractCQREntityAI<AbstractEntityCQR> {

	protected int ticksNotHealing;
	protected boolean isHealing;

	public EntityAIHealingPotion(AbstractEntityCQR entity) {
		super(entity);
		//this.setMutexBits(3);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		return this.entity.getHealingPotions() > 0 && this.entity.getHealth() <= Math.max(this.entity.getMaxHealth() * 0.15F, 5.0F);
	}

	@Override
	public void start() {
		this.entity.getNavigation().stop();
		this.ticksNotHealing = 0;
		this.isHealing = false;
	}

	@Override
	public void stop() {
		this.ticksNotHealing = 0;
		this.isHealing = false;
		//this.entity.resetActiveHand();
		this.entity.stopUsingItem();
		if (this.entity.isHoldingPotion()) {
			this.entity.swapWeaponAndPotionSlotItemStacks();
		}
	}

	@Override
	public void tick() {
		Entity attackTarget = this.entity.getTarget();

		if (this.isHealing) {
			this.entity.swing(InteractionHand.MAIN_HAND);
		} else {
			if (attackTarget == null) {
				this.startHealing();
			}
		}

		boolean flag = true;
		if (attackTarget != null) {
			double alertRadius = CQRConfig.SERVER_CONFIG.mobs.alertRadius.get();
			Vec3 vec1 = this.entity.position().add(alertRadius, alertRadius * 0.5D, alertRadius);
			Vec3 vec2 = this.entity.position().subtract(alertRadius, alertRadius * 0.5D, alertRadius);
			AABB aabb = new AABB(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
			List<Entity> possibleEnts = this.entity.level.getEntities(this.entity, aabb, TargetUtil.createPredicateAlly(this.entity.getFaction()));

			if (!possibleEnts.isEmpty()) {
				Entity e1 = null;
				int count = -1;
				double distance = Double.MAX_VALUE;
				for (Entity e2 : possibleEnts) {
					AABB aabb1 = new AABB(e2.getX() - 4, e2.getY() - 2, e2.getZ() - 4, e2.getX() + 4, e2.getY() + 2, e2.getZ() + 4);
					List<Entity> list = e2.level.getEntities(e2, aabb1, TargetUtil.createPredicateAlly(this.entity.getFaction()));
					double d = this.entity.distanceToSqr(e2);
					if (list.size() > count || (list.size() == count && d < distance)) {
						e1 = e2;
						count = list.size();
						distance = d;
					}
				}
				if (count >= 5) {
					this.entity.getNavigation().moveTo(e1, 1.0D);
					flag = false;
				}
			}

			boolean canMoveBackwards = this.canMoveBackwards();

			if (flag) {
				// No larger group in range
				this.updateRotation(attackTarget, 2.5F, 2.5F);

				if (canMoveBackwards) {
					EntityUtil.move2D(this.entity, 0.0D, -0.2D, this.entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * 1.5, this.entity.yHeadRot);
				}
			}

			if (!this.isHealing) {
				if (this.entity.horizontalCollision || !canMoveBackwards || this.ticksNotHealing > 80 || this.entity.distanceTo(attackTarget) > 8.0F) {
					this.startHealing();
				} else {
					this.checkAndPerformBlock();
				}
			}
		}
	}

	private void updateRotation(Entity entity, float deltaYaw, float deltaPitch) {
		double x = entity.getX() - this.entity.getX();
		double y = entity.getY() - this.entity.getY();
		double z = entity.getZ() - this.entity.getZ();
		double d = Math.sqrt(x * x + z * z);

		float yaw = (float) Math.toDegrees(Math.atan2(-x, z));
		float pitch = (float) Math.toDegrees(Math.atan2(-y, d));
		this.entity.yBodyRot += Mth.clamp(Mth.wrapDegrees(yaw - this.entity.yBodyRot), -deltaYaw, deltaYaw);
		this.entity.yBodyRot = Mth.wrapDegrees(this.entity.yBodyRot);
		this.entity.xRot += Mth.clamp(Mth.wrapDegrees(pitch - this.entity.xRot), -deltaPitch, deltaPitch);
		this.entity.xRot = Mth.clamp(this.entity.xRot, -90.0F, 90.0F);
		this.entity.yHeadRot = this.entity.yBodyRot;
	}

	private void checkAndPerformBlock() {
		if (!this.entity.isBlocking()) {
			ItemStack offhand = this.entity.getItemInHand(InteractionHand.OFF_HAND);

			if (offhand.getItem().isShield(offhand, this.entity)) {
				this.entity.startUsingItem(InteractionHand.OFF_HAND);;
			}
		}
	}

	private boolean canMoveBackwards() {
		double sin = -Math.sin(Math.toRadians(this.entity.yBodyRot));
		double cos = Math.cos(Math.toRadians(this.entity.yBodyRot));
		BlockPos pos = new BlockPos(this.entity.getX() - sin, this.entity.getY() - 0.001D, this.entity.getZ() - cos);
		BlockState state = this.entity.level.getBlockState(pos);
		return state.isFaceSturdy(this.entity.level, pos, Direction.UP);
	}

	public void startHealing() {
		if (!this.isHealing) {
			this.isHealing = true;
			if (!this.entity.isHoldingPotion()) {
				this.entity.swapWeaponAndPotionSlotItemStacks();
			}
			this.entity.stopUsingItem();
			this.entity.startUsingItem(InteractionHand.MAIN_HAND);
		}
	}

}
