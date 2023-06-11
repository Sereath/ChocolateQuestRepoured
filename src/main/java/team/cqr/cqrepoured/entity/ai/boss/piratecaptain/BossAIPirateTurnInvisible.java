package team.cqr.cqrepoured.entity.ai.boss.piratecaptain;

import net.minecraft.util.Hand;
import net.minecraft.world.item.ItemStack;
import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
import team.cqr.cqrepoured.entity.boss.EntityCQRPirateCaptain;
import team.cqr.cqrepoured.init.CQRItems;

public class BossAIPirateTurnInvisible extends AbstractCQREntityAI<EntityCQRPirateCaptain> {

	private int cooldown = 0;
	private int invisibleTime = 0;

	public BossAIPirateTurnInvisible(EntityCQRPirateCaptain entity) {
		super(entity);
	}

	@Override
	public boolean canUse() {
		if (this.entity != null && this.entity.getHealth() / this.entity.getMaxHealth() <= 0.5 && this.entity.getTarget() != null && !this.entity.isDeadOrDying()) {
			this.cooldown--;
			return this.cooldown <= 0;
		}
		return false;
	}

	@Override
	public void start() {
		this.invisibleTime = 200;
		this.entity.setInvisibility(this.invisibleTime);
	}

	@Override
	public boolean canContinueToUse() {
		return this.entity.getInvisibility() > 0;
	}

	@Override
	public void tick() {
		boolean disInt = false;
		boolean reInt = false;
		if (this.invisibleTime <= EntityCQRPirateCaptain.TURN_INVISIBLE_ANIMATION_TIME) {
			reInt = true;
			// this.entity.setInvisibleTicks(this.entity.getInvisibleTicks() - 1);
			this.entity.setItemInHand(Hand.MAIN_HAND, new ItemStack(CQRItems.CAPTAIN_REVOLVER.get(), 1));
		} else if (this.invisibleTime >= 200 - EntityCQRPirateCaptain.TURN_INVISIBLE_ANIMATION_TIME) {
			disInt = true;
			// this.entity.setInvisibleTicks(this.entity.getInvisibleTicks() + 1);
			this.entity.setItemInHand(Hand.MAIN_HAND, new ItemStack(CQRItems.DAGGER_NINJA.get(), 1));
		}
		this.invisibleTime--;
		if (this.invisibleTime <= 0) {
			disInt = false;
			reInt = false;
			this.cooldown = 100;
		}
		// this.entity.setInvisible(invi);
		this.entity.setIsReintegrating(reInt);
		this.entity.setIsDisintegrating(disInt);
	}

}
