package team.cqr.cqrepoured.entity.ai.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.item.ItemCursedBone;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.util.VectorUtil;

// TODO: Make entity strafe backwards (like with the bow) while they are casting
public class EntityAICursedBoneSummoner extends AbstractCQREntityAI<AbstractEntityCQR> implements ISummoner {

	private List<Entity> summonedEntities = new ArrayList<>();

	private static final int SUMMONS_PER_CAST = 2;
	private static final int MAX_COOLDOWN = 300;
	private static final int MIN_COOLDOWN = 200;

	private int prevTimeUsed;
	private int cooldown = 20;
	private int chargingTicks = 20;

	public EntityAICursedBoneSummoner(AbstractEntityCQR entity) {
		super(entity);
		//this.setMutexBits(0);
		//No flags needed here
	}

	private boolean hasCursedBone() {
		return this.entity.getMainHandItem().getItem() instanceof ItemCursedBone || this.entity.getOffhandItem().getItem() instanceof ItemCursedBone;
	}

	@Override
	public boolean canUse() {
		this.filterSummons();
		if (!this.hasCursedBone()) {
			return false;
		}
		if (!this.entity.hasAttackTarget()) {
			return false;
		}
		if (this.entity.tickCount - this.prevTimeUsed < this.cooldown) {
			return false;
		}
		return this.summonedEntities.size() < this.getMaxSummonedEntities();
	}

	private int getMaxSummonedEntities() {
		switch (this.world.getDifficulty()) {
		case HARD:
			return 6;
		case NORMAL:
			return 5;
		default:
			return 4;
		}
	}

	private void filterSummons() {
		if (this.summonedEntities.isEmpty()) {
			return;
		}
		this.summonedEntities.removeIf(e -> !e.isAlive());
	}

	@Override
	public boolean canContinueToUse() {
		if (!this.hasCursedBone()) {
			return false;
		}
		return this.chargingTicks >= 0;
	}

	// TODO: Add some magic sounds...
	@Override
	public void tick() {
		this.chargingTicks--;
		super.tick();

		if(!hasCursedBone()) {
			return;
		}
		
		ItemStack stack = this.entity.getMainHandItem();
		if (!(stack.getItem() instanceof ItemCursedBone)) {
			stack = this.entity.getOffhandItem();
			this.entity.swing(Hand.OFF_HAND);
		} else {
			this.entity.swing(Hand.MAIN_HAND);
		}

		if (this.chargingTicks < 0) {
			int remainingEntitySlots = this.getMaxSummonedEntities() - this.summonedEntities.size();
			int mobCount = Math.min(SUMMONS_PER_CAST, remainingEntitySlots);

			if (mobCount > 0) {
				Vector3d vector = this.entity.getLookAngle().normalize().scale(3);
				ItemCursedBone cursedBone = (ItemCursedBone) stack.getItem();
				for (int i = 0; i < mobCount; i++) {
					Vector3d posV = this.entity.position().add(vector);
					BlockPos pos = new BlockPos(posV.x, posV.y, posV.z);
					Optional<Entity> circle = cursedBone.spawnEntity(pos, this.world, stack, this.entity, this);
					if (circle.isPresent()) {
						this.summonedEntities.add(circle.get());
						vector = VectorUtil.rotateVectorAroundY(vector, 360 / mobCount);
					}
				}
			}
		}
	}

	@Override
	public void stop() {
		this.cooldown = DungeonGenUtils.randomBetween(MIN_COOLDOWN, MAX_COOLDOWN, this.entity.getRandom());
		this.chargingTicks = 20;
		this.prevTimeUsed = this.entity.tickCount;
		super.stop();
	}

	@Override
	public Faction getSummonerFaction() {
		return this.entity.getFaction();
	}

	@Override
	public List<Entity> getSummonedEntities() {
		return this.summonedEntities;
	}

	@Override
	public LivingEntity getSummoner() {
		return this.entity;
	}

	// TODO: Integrate with looter, so when he summons something, he will equip the
	// entity with gear from his backpack
	@Override
	public void addSummonedEntityToList(Entity summoned) {
		this.summonedEntities.add(summoned);
		this.tryEquipSummon(summoned, this.world.random);
	}

}
