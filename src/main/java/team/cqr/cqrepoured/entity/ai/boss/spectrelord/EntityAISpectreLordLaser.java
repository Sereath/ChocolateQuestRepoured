package team.cqr.cqrepoured.entity.ai.boss.spectrelord;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import team.cqr.cqrepoured.entity.ai.spells.AbstractEntityAISpell;
import team.cqr.cqrepoured.entity.ai.spells.IEntityAISpellAnimatedVanilla;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityCQRSpectreLord;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityRotatingLaser;
import team.cqr.cqrepoured.entity.misc.AbstractEntityLaser;

import java.util.ArrayList;
import java.util.List;

public class EntityAISpectreLordLaser extends AbstractEntityAISpell<EntityCQRSpectreLord> implements IEntityAISpellAnimatedVanilla {

	private LivingEntity target;
	private final List<AbstractEntityLaser> lasers = new ArrayList<>();

	public EntityAISpectreLordLaser(EntityCQRSpectreLord entity, int cooldown, int chargingTicks, int castingTicks) {
		super(entity, cooldown, chargingTicks, castingTicks);
		this.setup(true, true, false, false);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		for (AbstractEntityLaser laser : this.lasers) {
			laser.remove();
		}
		this.lasers.clear();
	}

	@Override
	public void startChargingSpell() {
		super.startChargingSpell();
		this.target = this.entity.getTarget();
	}

	@Override
	public void startCastingSpell() {
		super.startCastingSpell();
		float yaw = (float) Math.toDegrees(Math.atan2(-(this.target.getX() - this.entity.getX()), this.target.getZ() - this.entity.getZ()));
		AbstractEntityLaser laser1 = new EntityRotatingLaser(this.world, this.entity, 32.0F, 1.0F, 0.0F);
		laser1.rotationYawCQR = yaw - 90.0F;
		Vec3 vec1 = Vec3.directionFromRotation(0.0F, laser1.rotationYawCQR);
		laser1.setPos(this.entity.getX() + vec1.x * 0.25D, this.entity.getY() + this.entity.getBbHeight() * 0.6D + vec1.y * 0.25D, this.entity.getZ() + vec1.z * 0.25D);
		this.world.addFreshEntity(laser1);
		this.lasers.add(laser1);
		AbstractEntityLaser laser2 = new EntityRotatingLaser(this.world, this.entity, 32.0F, -2.0F, 0.0F);
		laser2.rotationYawCQR = yaw + 90.0F;
		Vec3 vec2 = Vec3.directionFromRotation(0.0F, laser2.rotationYawCQR);
		laser2.setPos(this.entity.getX() + vec2.x * 0.25D, this.entity.getY() + this.entity.getBbHeight() * 0.6D + vec2.y * 0.25D, this.entity.getZ() + vec2.z * 0.25D);
		this.world.addFreshEntity(laser2);
		this.lasers.add(laser2);
	}

	@Override
	protected SoundEvent getStartChargingSound() {
		return SoundEvents.EVOKER_PREPARE_ATTACK;
	}

	@Override
	public int getWeight() {
		return 10;
	}

	@Override
	public boolean ignoreWeight() {
		return false;
	}

	@Override
	public float getRed() {
		return 0.1F;
	}

	@Override
	public float getGreen() {
		return 0.8F;
	}

	@Override
	public float getBlue() {
		return 0.6F;
	}

}
