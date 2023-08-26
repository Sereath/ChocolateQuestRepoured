package team.cqr.cqrepoured.init;

//@EventBusSubscriber(modid = CQRConstants.MODID)
public class CQREntities {

	private static int entityID = 0;

//	@SubscribeEvent
//	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
//		final EntityEntry[] entityEntries = {
//				createEntityEntryWithoutEgg(EntitySlimePart.class, "slime_part", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileBullet.class, "projectile_bullet", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileCannonBall.class, "projectile_cannon_ball", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileEarthQuake.class, "projectile_earth_quake", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectilePoisonSpell.class, "projectile_poison_spell", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileSpiderBall.class, "projectile_spider_ball", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileVampiricSpell.class, "projectile_vampiric_spell", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileFireWallPart.class, "projectile_firewall_part", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileHookShotHook.class, "projectile_hookshot_hook", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileSpiderHook.class, "projectile_spider_hook", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileBubble.class, "projectile_bubble", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileHotFireball.class, "projectile_hot_fireball", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileWeb.class, "projectile_web", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileThrownBlock.class, "projectile_thrown_block", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileHomingEnderEye.class, "projectile_homing_ender_eye", 64, 1, true),
//				createEntityEntryWithoutEgg(ProjectileEnergyOrb.class, "projectile_energy_orb", 64, 1, true),
//
//				createEntityEntry(EntityCQRDummy.class, "dummy", 64, 1, true, 0xC29D62, 0x67502C),
//				createEntityEntry(EntityCQRDwarf.class, "dwarf", 64, 1, true, 0x333333, 0x582800),
//				createEntityEntry(EntityCQREnderman.class, "enderman", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRGremlin.class, "gremlin", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRGoblin.class, "goblin", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRGolem.class, "golem", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRHuman.class, "human", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRIllager.class, "illager", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRNPC.class, "npc", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRMinotaur.class, "minotaur", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRMandril.class, "mandril", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRMummy.class, "mummy", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQROgre.class, "ogre", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQROrc.class, "orc", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRBoarman.class, "boarman", 64, 1, true, 0x333333, 0xEA9393),
//				createEntityEntry(EntityCQRPirate.class, "pirate", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRSkeleton.class, "skeleton", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRSpectre.class, "spectre", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRTriton.class, "triton", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRWalker.class, "walker", 64, 1, true, 0x333333, 0xC3BDBD),
//				createEntityEntry(EntityCQRZombie.class, "zombie", 64, 1, true, 0x333333, 0xC3BDBD),
//
//				// Mounts
//				createEntityEntry(EntityGiantEndermite.class, "giant_endermite", 64, 1, true, 0xC29D62, 0xEA9393),
//				createEntityEntry(EntityGiantSilverfishNormal.class, "giant_silverfish", 64, 1, true, 0xC29D62, 0xEA9393),
//				createEntityEntry(EntityGiantSilverfishRed.class, "giant_silverfish1", 64, 1, true, 0xC29D62, 0xEA9393),
//				createEntityEntry(EntityGiantSilverfishGreen.class, "giant_silverfish2", 64, 1, true, 0xC29D62, 0xEA9393),
//				/* createEntityEntry(EntityPollo.class, "pollo", 64, 1, true, 0xC29D62, 0xEA9393), */
//
//				// Bosses
//				createEntityEntry(EntityCQRNetherDragon.class, "nether_dragon", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRGiantTortoise.class, "giant_tortoise", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRLich.class, "lich", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRBoarmage.class, "boar_mage", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRNecromancer.class, "necromancer", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRWalkerKing.class, "walker_king", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRPirateCaptain.class, "pirate_captain", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQRGiantSpider.class, "giant_spider", 64, 1, true, 0x323232, 0x0),
//
//				// Misc Entities
//				createEntityEntryWithoutEgg(EntitySummoningCircle.class, "summoning_circle", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityFlyingSkullMinion.class, "flying_skull", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityBubble.class, "bubble_entity", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityWalkerKingIllusion.class, "illusion_walker_king", 64, 1, true),
//				/* createEntityEntry(EntityCQRWasp.class, "wasp", 64, 1, true, 0xC29D62, 0xEA9393), */
//				createEntityEntryWithoutEgg(EntityColoredLightningBolt.class, "colored_lightning_bolt", 512, 1, true),
//				createEntityEntryWithoutEgg(EntityWalkerTornado.class, "walker_tornado", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityCQRPirateParrot.class, "pirate_parrot", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityIceSpike.class, "ice_spike", 64, 1, true),
//				createEntityEntryWithoutEgg(EntitySpiderEgg.class, "spider_egg", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityElectricField.class, "electric_field_entity", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityTNTPrimedCQR.class, "PrimedTnt", 64, 1, true),
//
//				// ender calamity
//				createEntityEntry(EntityCQREnderCalamity.class, "ender_calamity", 64, 1, true, 0x323232, 0x0),
//				createEntityEntry(EntityCQREnderKing.class, "ender_king", 64, 1, true, 0x323232, 0x0),
//				createEntityEntryWithoutEgg(EntityEndLaserTargeting.class, "end_targeting_laser", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityEndLaser.class, "end_laser", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityCalamityCrystal.class, "calamity_crystal", 64, 1, true),
//				createEntityEntryWithoutEgg(EntityCalamitySpawner.class, "calamity_spawner", 64, 1, true),
//
//				// Exterminator
//				createEntityEntry(EntityCQRExterminator.class, "exterminator", 64, 1, true, 0x323232, 0x0),
//				createEntityEntryWithoutEgg(EntityExterminatorHandLaser.class, "exterminator_laser", 64, 1, true) };
//
//		event.getRegistry().registerAll(entityEntries);
//
//		if (CQRMain.isWorkspaceEnvironment) {
//			event.getRegistry().registerAll(// spectre lord
//					createEntityEntry(EntityCQRSpectreLord.class, "spectre_lord", 64, 1, true, 0x323232, 0x0), createEntityEntryWithoutEgg(EntitySpectreLordIllusion.class, "spectre_lord_illusion", 64, 1, true), createEntityEntryWithoutEgg(EntitySpectreLordCurse.class, "spectre_lord_curse", 64, 1, true),
//					createEntityEntryWithoutEgg(EntitySpectreLordExplosion.class, "spectre_lord_explosion", 64, 1, true), createEntityEntryWithoutEgg(EntityRotatingLaser.class, "rotating_laser", 64, 1, true), createEntityEntryWithoutEgg(EntityTargetingLaser.class, "targeting_laser", 64, 1, true));
//		}
//
//		// Spawns
//		// EntityRegistry.addSpawn(EntityCQRWasp.class, 24, 3, 9, EnumCreatureType.CREATURE, Biomes.SWAMPLAND,
//		// Biomes.MUTATED_SWAMPLAND, Biomes.JUNGLE,
//		// Biomes.MUTATED_JUNGLE);
//	}

//	private static EntityEntry createEntityEntry(@Nonnull Class<? extends Entity> entityClass, String name, int trackerRange, int trackerUpdateFrequency, boolean sendVelocityUpdates, int eggColor1, int eggColor2) {
//		return EntityEntryBuilder.create().entity(entityClass).id(new ResourceLocation(CQRConstants.MODID, name), entityID++).name("cqr_" + name).egg(eggColor1, eggColor2).tracker(trackerRange, trackerUpdateFrequency, sendVelocityUpdates).build();
//	}
//
//	private static EntityEntry createEntityEntryWithoutEgg(@Nonnull Class<? extends Entity> entityClass, String name, int trackerRange, int trackerUpdateFrequency, boolean sendVelocityUpdates) {
//		return EntityEntryBuilder.create().entity(entityClass).id(new ResourceLocation(CQRConstants.MODID, name), entityID++).name("cqr_" + name).tracker(trackerRange, trackerUpdateFrequency, sendVelocityUpdates).build();
//	}

}
