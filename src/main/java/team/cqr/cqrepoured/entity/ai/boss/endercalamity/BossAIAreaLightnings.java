package team.cqr.cqrepoured.entity.ai.boss.endercalamity;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity;
import team.cqr.cqrepoured.entity.boss.endercalamity.phases.EEnderCalamityPhase;
import team.cqr.cqrepoured.entity.misc.EntityColoredLightningBolt;

public class BossAIAreaLightnings extends AbstractBossAIEnderCalamity {

	private int lightningTick = 0;
	private int borderLightning = 20;
	private final int LIGHTNING_AREA_RADIUS;

	public BossAIAreaLightnings(EntityCQREnderCalamity entity, final int lightningRadius) {
		super(entity);
		//this.setMutexBits(0);
		//Correct??
		this.setFlags(EnumSet.noneOf(Flag.class));
		this.LIGHTNING_AREA_RADIUS = lightningRadius;
	}

	@Override
	public boolean canUse() {
		if (this.entity.hasAttackTarget()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		if (super.canContinueToUse() && this.canUse()) {
			this.lightningTick++;
			return (this.lightningTick > this.borderLightning);
		}
		return false;
	}

	@Override
	public void tick() {
		if (this.lightningTick < this.borderLightning) {
			return;
		}
		// strike lightning
		this.lightningTick = 0;
		this.borderLightning = 80;
		switch (this.world.getDifficulty()) {
		case EASY:
		case PEACEFUL:
			this.borderLightning += 30;
			break;
		case HARD:
			this.borderLightning -= 5;
			break;
		case NORMAL:
			this.borderLightning += 5;
			break;
		}
		// AI only executes every 3 ticks!
		this.borderLightning /= 3;
		int x = -this.LIGHTNING_AREA_RADIUS + this.entity.getRandom().nextInt((2 * this.LIGHTNING_AREA_RADIUS) + 1);
		int z = -this.LIGHTNING_AREA_RADIUS + this.entity.getRandom().nextInt((2 * this.LIGHTNING_AREA_RADIUS) + 1);
		int y = (-this.LIGHTNING_AREA_RADIUS + this.entity.getRandom().nextInt((2 * this.LIGHTNING_AREA_RADIUS) + 1)) / 2;

		BlockPos cp;
		if (this.entity.hasHomePositionCQR()) {
			cp = this.entity.getHomePositionCQR();
		} else {
			cp = this.entity.blockPosition();
		}
		x += cp.getX();
		y += cp.getY();
		z += cp.getZ();

		EntityColoredLightningBolt entitybolt = new EntityColoredLightningBolt(this.world, x, y, z, true, false, 0.8F, 0.01F, 0.98F, 0.4F);
		this.world.addFreshEntity(entitybolt);
	}

	@Override
	protected boolean canExecuteDuringPhase(EEnderCalamityPhase currentPhase) {
		return true;
	}

}
