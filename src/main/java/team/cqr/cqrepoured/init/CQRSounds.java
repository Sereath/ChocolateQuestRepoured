package team.cqr.cqrepoured.init;

import static team.cqr.cqrepoured.util.InjectionUtil.Null;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import team.cqr.cqrepoured.CQRConstants;

@ObjectHolder(CQRConstants.MODID)
public class CQRSounds {

	@ObjectHolder("item.magic_bell.use")
	public static final SoundEvent BELL_USE = Null();
	@ObjectHolder("item.gun.shoot")
	public static final SoundEvent GUN_SHOOT = Null();
	@ObjectHolder("item.revolver.shoot")
	public static final SoundEvent REVOLVER_SHOOT = Null();
	@ObjectHolder("item.musket.shoot")
	public static final SoundEvent MUSKET_SHOOT = Null();
	@ObjectHolder("item.magic")
	public static final SoundEvent MAGIC = Null();

	@ObjectHolder("entity.player.classic_hurt")
	public static final SoundEvent CLASSIC_HURT = Null();

	@ObjectHolder("entity.nether_dragon.hurt")
	public static final SoundEvent NETHER_DRAGON_HURT = Null();
	@ObjectHolder("entity.nether_dragon.death")
	public static final SoundEvent NETHER_DRAGON_DEATH = Null();

	@ObjectHolder("entity.gremlin.ambient")
	public static final SoundEvent GREMLIN_AMBIENT = Null();
	@ObjectHolder("entity.gremlin.hurt")
	public static final SoundEvent GREMLIN_HURT = Null();
	@ObjectHolder("entity.gremlin.death")
	public static final SoundEvent GREMLIN_DEATH = Null();

	// Source for goblin sounds: https://opengameart.org/content/goblins-sound-pack
	@ObjectHolder("entity.goblin.ambient")
	public static final SoundEvent GOBLIN_AMBIENT = Null();
	@ObjectHolder("entity.goblin.hurt")
	public static final SoundEvent GOBLIN_HURT = Null();
	@ObjectHolder("entity.goblin.death")
	public static final SoundEvent GOBLIN_DEATH = Null();

	@ObjectHolder("entity.pirate.ambient")
	public static final SoundEvent PIRATE_AMBIENT = Null();
	@ObjectHolder("entity.pirate.hurt")
	public static final SoundEvent PIRATE_HURT = Null();
	@ObjectHolder("entity.pirate.death")
	public static final SoundEvent PIRATE_DEATH = Null();

	@ObjectHolder("entity.ogre.ambient")
	public static final SoundEvent OGRE_AMBIENT = Null();
	@ObjectHolder("entity.ogre.hurt")
	public static final SoundEvent OGRE_HURT = Null();
	@ObjectHolder("entity.ogre.death")
	public static final SoundEvent OGRE_DEATH = Null();

	@ObjectHolder("entity.walker.ambient")
	public static final SoundEvent WALKER_AMBIENT = Null();
	@ObjectHolder("entity.walker.hurt")
	public static final SoundEvent WALKER_HURT = Null();
	@ObjectHolder("entity.walker.death")
	public static final SoundEvent WALKER_DEATH = Null();
	@ObjectHolder("entity.walker_king.death")
	public static final SoundEvent WALKER_KING_DEATH = Null();
	@ObjectHolder("entity.walker_king.death_effect")
	public static final SoundEvent WALKER_KING_DEATH_EFFECT = Null();
	@ObjectHolder("entity.walker_king.laugh")
	public static final SoundEvent WALKER_KING_LAUGH = Null();

	@ObjectHolder("entity.ender_calamity.charge_energy_sphere")
	public static final SoundEvent ENDER_CALAMITY_CHARGE_ENERGY_BALL = Null();
	@ObjectHolder("entity.ender_calamity.ready_energy_sphere")
	public static final SoundEvent ENDER_CALAMITY_READY_ENERGY_BALL = Null();
	@ObjectHolder("entity.ender_calamity.throw_item")
	public static final SoundEvent ENDER_CALAMITY_THROW_ITEM = Null();
	/* Sound taken from DungeonDefenders, credit goes to TrendyNet */
	@ObjectHolder("entity.ender_calamity.fire_energy_sphere")
	public static final SoundEvent ENDER_CALAMITY_FIRE_ENERGY_BALL = Null();

	/* Sound taken from DungeonDefenders, credit goes to TrendyNet */
	@ObjectHolder("entity.exterminator.electro_zap")
	public static final SoundEvent EXTERMINATOR_ELECTRO_ZAP = Null();
	@ObjectHolder("entity.exterminator.cannon_shoot")
	public static final SoundEvent EXTERMINATOR_CANNON_SHOOT = Null();

	/* Sound taken from DungeonDefenders, credit goes to TrendyNet */
	@ObjectHolder("projectile.energy_ball.impact")
	public static final SoundEvent PROJECTILE_ENERGY_SPHERE_IMPACT = Null();

	@ObjectHolder("entity.bubble.bubble")
	public static final SoundEvent BUBBLE_BUBBLE = Null();

	@EventBusSubscriber(modid = CQRConstants.MODID, bus = Bus.MOD)
	public static class EventHandler {

		@SubscribeEvent
		public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
			final SoundEvent[] sounds = {
					createSoundEvent("item.magic_bell.use"),
					createSoundEvent("item.gun.shoot"),
					createSoundEvent("item.revolver.shoot"),
					createSoundEvent("item.musket.shoot"),
					createSoundEvent("item.magic"),
					createSoundEvent("entity.player.classic_hurt"),
					createSoundEvent("entity.goblin.ambient"),
					createSoundEvent("entity.goblin.hurt"),
					createSoundEvent("entity.goblin.death"),
					createSoundEvent("entity.gremlin.ambient"),
					createSoundEvent("entity.gremlin.hurt"),
					createSoundEvent("entity.gremlin.death"),
					createSoundEvent("entity.nether_dragon.hurt"),
					createSoundEvent("entity.nether_dragon.death"),
					createSoundEvent("entity.pirate.ambient"),
					createSoundEvent("entity.pirate.hurt"),
					createSoundEvent("entity.pirate.death"),
					createSoundEvent("entity.ogre.ambient"),
					createSoundEvent("entity.ogre.hurt"),
					createSoundEvent("entity.ogre.death"),
					createSoundEvent("entity.walker.ambient"),
					createSoundEvent("entity.walker.hurt"),
					createSoundEvent("entity.walker.death"),
					createSoundEvent("entity.walker_king.death"),
					createSoundEvent("entity.walker_king.death_effect"),
					createSoundEvent("entity.walker_king.laugh"),
					createSoundEvent("entity.ender_calamity.charge_energy_sphere"),
					createSoundEvent("entity.ender_calamity.ready_energy_sphere"),
					createSoundEvent("entity.ender_calamity.fire_energy_sphere"),
					createSoundEvent("entity.ender_calamity.throw_item"),
					createSoundEvent("entity.exterminator.electro_zap"),
					createSoundEvent("entity.exterminator.cannon_shoot"),
					createSoundEvent("projectile.energy_ball.impact"),
					createSoundEvent("entity.bubble.bubble") };

			IForgeRegistry<SoundEvent> registry = event.getRegistry();

			for (SoundEvent sound : sounds) {
				registry.register(sound);
			}
		}

		private static SoundEvent createSoundEvent(String name) {
			ResourceLocation location = new ResourceLocation(CQRConstants.MODID, name);
			return new SoundEvent(location).setRegistryName(location);
		}

	}

}
