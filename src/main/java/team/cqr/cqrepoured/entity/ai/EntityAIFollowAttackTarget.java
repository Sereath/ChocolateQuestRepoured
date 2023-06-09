package team.cqr.cqrepoured.entity.ai;

import net.minecraft.world.phys.Vec3;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;

import java.util.EnumSet;

public class EntityAIFollowAttackTarget extends AbstractCQREntityAI<AbstractEntityCQR> {

	private int ticksWaiting;

	public EntityAIFollowAttackTarget(AbstractEntityCQR entity) {
		super(entity);
		//this.setMutexBits(3);
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return this.entity.getTarget() != null;
	}

	@Override
	public boolean canContinueToUse() {
		if (this.entity.getTarget() == null) {
			return false;
		}
		if (this.entity.isPathFinding()) {
			return true;
		}
		if (this.ticksWaiting < 100) {
			return true;
		}
		this.entity.setTarget(null);
		return false;
	}

	@Override
	public void start() {
		Vec3 v = this.entity.getLastPosAttackTarget();
		this.entity.getNavigation().moveTo(v.x, v.y, v.z, 1.0D);
	}

	@Override
	public void stop() {
		this.entity.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (this.entity.getLastTimeSeenAttackTarget() + 100 >= this.entity.tickCount) {
			Vec3 v = this.entity.getLastPosAttackTarget();
			this.entity.getNavigation().moveTo(v.x, v.y, v.z, 1.0D);
		}
		if (!this.entity.isPathFinding()) {
			this.ticksWaiting++;
		} else {
			this.ticksWaiting = 0;
		}
	}

}
