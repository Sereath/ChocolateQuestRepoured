package team.cqr.cqrepoured.entity.ai.boss.exterminator;

import net.minecraft.entity.EntityLivingBase;
import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
import team.cqr.cqrepoured.entity.boss.AbstractEntityLaser;
import team.cqr.cqrepoured.entity.boss.exterminator.EntityCQRExterminator;
import team.cqr.cqrepoured.entity.boss.exterminator.EntityExterminatorHandLaser;

public class BossAIExterminatorHandLaser extends AbstractCQREntityAI<EntityCQRExterminator> {

	private static final int MAX_DISTANCE = 32;
	private static final int MIN_DISTANCE = 8;

	private AbstractEntityLaser activeLaser = null;
	private EntityLivingBase target = null;

	private int timer;
	private int timeOut = 0;

	public BossAIExterminatorHandLaser(EntityCQRExterminator entity) {
		super(entity);

		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (this.timeOut > 0) {
			this.timeOut--;
			return false;
		}
		if (this.entity != null && this.entity.isEntityAlive() && this.entity.hasAttackTarget() && !this.entity.isCurrentlyPlayingAnimation() && (this.entity.getHealth() / this.entity.getMaxHealth() <= 0.5F)) {
			if (this.entity.isStunned()) {
				return false;
			}

			final float distance = this.entity.getDistance(this.entity.getAttackTarget());
			this.target = this.entity.getAttackTarget();
			return this.entity.hasAttackTarget() && distance <= MAX_DISTANCE && distance >= MIN_DISTANCE;
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.entity.isStunned()) {
			return false;
		}
		return this.entity.isEntityAlive() && this.target != null && this.target.isEntityAlive() && this.timer > 0;
	}

	@Override
	public void startExecuting() {
		this.timer = 150;
		super.startExecuting();
		this.checkAndOrStartCannonLaser();
	}

	private boolean checkAndOrStartCannonLaser() {
		// If the laser already exists => no need to validate the rest!
		if (this.activeLaser != null) {
			return true;
		}
		if ((!this.entity.isCannonArmReadyToShoot() || !this.entity.isCannonRaised())) {
			// System.out.println("cannon not ready, can we raise it?");
			if (!this.entity.isCannonRaised()) {
				// System.out.println("cannon is not raised, telling it to raise...");
				if (this.entity.switchCannonArmState(true)) {
					// System.out.println("raise command received!");
				} else {
					// System.out.println("Raise command failed?!");
				}
			}
		} else {
			// System.out.println("Cannon is ready and cannon is raised");
			// System.out.println("Laser does not exist, cannon is raised, so create the laser...");
			this.activeLaser = new EntityExterminatorHandLaser(this.entity, this.target);
			this.activeLaser.setupPositionAndRotation();
			this.world.spawnEntity(this.activeLaser);
			this.entity.switchCannonArmState(true);
			return true;
		}
		return false;
	}

	@Override
	public void updateTask() {
		super.updateTask();
		this.timer--;
		// System.out.println("Executing...");
		if (this.checkAndOrStartCannonLaser()) {
			// System.out.println("we have a laser, let's position it...");
			this.entity.rotationYaw = this.activeLaser.rotationYawCQR /* + 90.0F */;
			this.entity.prevRotationYaw = this.activeLaser.prevRotationYawCQR /* + 90.0F */;

			this.entity.faceEntity(this.target, 180, 180);
		} else {
			// System.out.println("No laser :/");
		}
	}

	@Override
	public boolean isInterruptible() {
		return false;
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.target = null;
		this.timer = 300;
		if (this.activeLaser != null) {
			this.activeLaser.setDead();
			this.activeLaser = null;
		}
		this.entity.setCannonArmAutoTimeoutForLowering(40);
		this.timeOut = 200;
	}

}
