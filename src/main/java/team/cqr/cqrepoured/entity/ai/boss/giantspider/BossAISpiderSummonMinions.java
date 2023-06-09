//package team.cqr.cqrepoured.entity.ai.boss.giantspider;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityList;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.vector.Vector3d;
//import team.cqr.cqrepoured.CQRMain;
//import team.cqr.cqrepoured.entity.ai.AbstractCQREntityAI;
//import team.cqr.cqrepoured.entity.bases.ISummoner;
//import team.cqr.cqrepoured.entity.boss.EntityCQRGiantSpider;
//import team.cqr.cqrepoured.util.DungeonGenUtils;
//import team.cqr.cqrepoured.util.VectorUtil;
//
//public class BossAISpiderSummonMinions extends AbstractCQREntityAI<EntityCQRGiantSpider> {
//
//	protected ISummoner summoner = null;
//	protected int MAX_MINIONS = 6;
//	protected int MAX_MINIONS_AT_A_TIME = 3;
//	protected ResourceLocation minionOverride = new ResourceLocation(CQRMain.MODID, "spider_egg");
//
//	private int cooldown = 0;
//	private static final int MAX_COOLDOWN = 150;
//	private static final int MIN_COOLDOWN = 50;
//
//	public BossAISpiderSummonMinions(EntityCQRGiantSpider spiderqueen) {
//		super(spiderqueen);
//		this.summoner = spiderqueen;
//	}
//
//	@Override
//	public boolean canUse() {
//		if (this.summoner == null || this.entity == null) {
//			return false;
//		}
//		if (this.cooldown > 0) {
//			this.cooldown--;
//		}
//		if (!this.entity.hasAttackTarget()) {
//			return false;
//		}
//		if (this.getAliveMinionCount() < this.MAX_MINIONS) {
//			if (this.entity.getHealth() / this.entity.getMaxHealth() <= 0.75) {
//				return this.cooldown <= 0;
//			}
//		}
//		return false;
//	}
//
//	protected int getAliveMinionCount() {
//		int aliveMinions = 0;
//		for (Entity minio : this.summoner.getSummonedEntities()) {
//			if (minio != null && minio.isAlive()) {
//				aliveMinions++;
//			}
//		}
//		return aliveMinions;
//	}
//
//	@Override
//	public void start() {
//		if (this.summoner == null || this.entity == null) {
//			return;
//		}
//		int minionCount = Math.min(this.MAX_MINIONS_AT_A_TIME, this.MAX_MINIONS - this.getAliveMinionCount());
//		double angle = 360 / minionCount;
//		Vector3d v = new Vector3d(1, 0, 0);
//		for (int i = 0; i < minionCount; i++) {
//			Vector3d pos = this.entity.position().add(v);
//			v = VectorUtil.rotateVectorAroundY(v, angle);
//
//			Entity minion = EntityList.createEntityByIDFromName(this.minionOverride, this.entity.level);
//			minion.setPos(pos.x, pos.y, pos.z);
//			this.entity.level.addFreshEntity(minion);
//			if (this.summoner != null && this.summoner.getSummoner().isAlive()) {
//				this.summoner.setSummonedEntityFaction(minion);
//				this.summoner.addSummonedEntityToList(minion);
//			}
//		}
//		this.cooldown = DungeonGenUtils.randomBetween(MIN_COOLDOWN, MAX_COOLDOWN, this.entity.getRandom());
//	}
//
//	@Override
//	public boolean canContinueToUse() {
//		return false;
//	}
//
//}
