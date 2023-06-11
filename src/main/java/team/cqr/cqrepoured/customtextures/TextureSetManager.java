package team.cqr.cqrepoured.customtextures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.network.server.packet.SPacketCustomTextures;

public class TextureSetManager {

	private Map<String, TextureSet> textureSets = new HashMap<>();
	private static TextureSetManager INSTANCE;

	private TextureSetManager() {
	}

	public static TextureSetManager getInstance() {
		try {
			return INSTANCE.get();
		} catch (NullPointerException npe) {
			INSTANCE = new TextureSetManager();
			return INSTANCE;
		}
	}

	TextureSetManager get() {
		return this;
	}

	public static void registerTextureSet(TextureSet set) {
		getInstance().registerTextureSetImpl(set);
	}

	public static void unloadTextures() {
		try {
			getInstance().unloadTexturesImpl();
		} catch (NoSuchMethodError ex) {
			// Ignore
		}
	}

	public static void loadTextureSetsFromFolder(File folder) {
		getInstance().clearData();
		unloadTextures();
		if (folder.isDirectory()) {
			List<File> files = new ArrayList<>(FileUtils.listFiles(folder, new String[] { "cfg", "prop", "properties" }, true));
			int loadedSets = 0;
			for (File f : files) {
				Properties prop = new Properties();
				try (InputStream inputStream = new FileInputStream(f)) {
					prop.load(inputStream);
				} catch (IOException e) {
					CQRMain.logger.error("Failed to load file {}", f.getName(), e);
					continue;
				}
				try {
					new TextureSet(prop, f.getName().substring(0, f.getName().lastIndexOf('.')));
					CQRMain.logger.info("Successfully loaded texture set: {}!", f.getName().substring(0, f.getName().lastIndexOf('.')));
					loadedSets++;
				} catch (Exception e) {
					CQRMain.logger.info("Failed loading texture set", e);
				}
			}
			CQRMain.logger.info("Loaded {} texture Sets!", loadedSets);
			
			//Now, register the pack...
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			if(resourceManager instanceof SimpleReloadableResourceManager) {
				((SimpleReloadableResourceManager)resourceManager).add(CTResourcepack.getInstance());
			} else {
				//WTF?!
			}

			//Correct replacement for FMLCommonHandler.isServer()?
			if (FMLEnvironment.dist.isClient()) {
				// Load the textures
				for (Map.Entry<String, File> entry : TextureSet.getLoadedFiles().entrySet()) {
					TextureUtil.loadFileInResourcepack(entry.getValue(), TextureSet.getResLocOfTexture(entry.getKey()));
				}
				TextureUtil.reloadResourcepacks();
			}
		}
	}

	private void clearData() {
		if (!this.textureSets.isEmpty()) {
			this.textureSets.clear();
		}
	}

	public static void sendTexturesToClient(ServerPlayer joiningPlayer) {
		try {
			getInstance().sendTexturesToClientImpl(joiningPlayer);
		} catch (NoSuchMethodError ex) {
			// Ignore
		}
	}

	private void sendTexturesToClientImpl(ServerPlayer joiningPlayer) {
		SPacketCustomTextures packet = new SPacketCustomTextures();
		/*
		 * for (File texture : TextureSet.getLoadedTextures()) { String base64 = CompressionUtil.encodeFileToBase64(texture);
		 * String path =
		 * texture.getAbsolutePath().substring(CQRMain.CQ_CUSTOM_TEXTURES_FOLDER_TEXTURES.getAbsolutePath().length());
		 * 
		 * packet.addPair(base64, path); }
		 */
		for (Map.Entry<String, File> entry : TextureSet.getLoadedFiles().entrySet()) {
			byte[] base64 = CompressionUtil.encodeFileToBase64(entry.getValue());
			String path = entry.getKey();

			packet.addPair(base64, path);
		}
		for (TextureSet ts : this.textureSets.values()) {
			packet.addTextureSet(ts);
		}
		CQRMain.NETWORK.send(PacketDistributor.PLAYER.with(() -> joiningPlayer), packet);
	}

	private void registerTextureSetImpl(TextureSet set) {
		this.textureSets.put(set.getName(), set);
	}

	@OnlyIn(Dist.CLIENT)
	private void unloadTexturesImpl() {
		for (TextureSet set : this.textureSets.values()) {
			for (ResourceLocation rs : set.getTextures()) {
				try {
					TextureUtil.unloadTexture(rs);
				} catch (Exception ex) {
					// Ignore
				}
			}
			set.clearTextureCache();
		}
		this.textureSets.clear();
	}

	@Nullable
	public TextureSet getTextureSet(String name) {
		return this.textureSets.getOrDefault(name, null);
	}

	public static List<TextureSet> getAllTextureSets() {
		return getInstance().getAllTextureSetsImpl();
	}

	private List<TextureSet> getAllTextureSetsImpl() {
		return new ArrayList<>(this.textureSets.values());
	}

}
