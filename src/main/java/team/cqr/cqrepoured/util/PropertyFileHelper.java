package team.cqr.cqrepoured.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.world.processor.IReplaceBlocksProcessor;
import team.cqr.cqrepoured.world.structure.generation.DungeonSpawnPos;

/**
 * Copyright (c) 29.04.2019 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 */
public class PropertyFileHelper {

	@Nullable
	public static Properties readPropFile(File file) {
		if (file.exists() && file.isFile()) {
			Properties prop = new Properties();

			try (InputStream inputStream = new FileInputStream(file)) {
				prop.load(inputStream);
				return prop;
			} catch (IOException e) {
				CQRMain.logger.error("Failed to load file {}", file.getName(), e);
				return null;
			}
		}
		return null;
	}

	public static String getStringProperty(Properties prop, String key, String defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		return s;
	}

	public static float getFloatProperty(Properties prop, String key, float defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public static boolean getBooleanProperty(Properties prop, String key, boolean defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		if (s.equalsIgnoreCase("true")) {
			return true;
		}
		if (s.equalsIgnoreCase("false")) {
			return false;
		}
		return defVal;
	}

	public static int getIntProperty(Properties prop, String key, int defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public static int getIntProperty(Properties prop, String key, int defVal, int min, int max) {
		return Mth.clamp(getIntProperty(prop, key, defVal), min, max);
	}

	public static double getDoubleProperty(Properties prop, String key, double defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return defVal;
		}
	}

	public static double getDoubleProperty(Properties prop, String key, double defVal, double min, double max) {
		return Mth.clamp(getDoubleProperty(prop, key, defVal), min, max);
	}

	public static ResourceLocation getResourceLocationProperty(Properties prop, String key, ResourceLocation defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		return getResourceLocationFromString(s, defVal);
	}

	public static BlockState getBlockStateProperty(Properties prop, String key, BlockState defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		return getBlockStateFromString(s, defVal);
	}

	public static BlockPos getBlockPosProperty(Properties prop, String key, BlockPos defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		return getBlockPosFromString(s, defVal);
	}

	public static DungeonSpawnPos getDungeonSpawnPosProperty(Properties prop, String key, DungeonSpawnPos defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		return getDungeonSpawnPosFromString(s, defVal);
	}

	public static File getStructureFolderProperty(Properties prop, String key, String defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return new File(CQRMain.CQ_STRUCTURE_FILES_FOLDER, defVal);
		}

		s = s.trim();
		if (s.isEmpty()) {
			return new File(CQRMain.CQ_STRUCTURE_FILES_FOLDER, defVal);
		}

		File retFile = new File(CQRMain.CQ_STRUCTURE_FILES_FOLDER, s);
		if (!retFile.isDirectory()) {
			return new File(CQRMain.CQ_STRUCTURE_FILES_FOLDER, defVal);
		}

		return retFile;
	}

	/*public static EnumMCWoodType getWoodTypeProperty(Properties prop, String key, EnumMCWoodType defVal) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (s.isEmpty()) {
			return defVal;
		}

		EnumMCWoodType retType = EnumMCWoodType.getTypeFromString(s);
		if (retType == null) {
			retType = defVal;
		}

		return retType;
	}*/

	public static String[] getStringArrayProperty(Properties prop, String key, String[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		String[] retArr = new String[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			String tmp = splitStr[i].trim();
			if (!tmp.isEmpty()) {
				retArr[i - removed] = tmp;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static boolean[] getBooleanArrayProperty(Properties prop, String key, boolean[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		boolean[] retArr = new boolean[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			String tmp = splitStr[i].trim();
			if (tmp.equalsIgnoreCase("true")) {
				retArr[i - removed] = true;
			} else if (tmp.equalsIgnoreCase("false")) {
				retArr[i - removed] = false;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static int[] getIntArrayProperty(Properties prop, String key, int[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		int[] retArr = new int[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			String tmp = splitStr[i].trim();
			try {
				retArr[i - removed] = Integer.parseInt(tmp);
			} catch (NumberFormatException e) {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static double[] getDoubleArrayProperty(Properties prop, String key, double[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		double[] retArr = new double[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			String tmp = splitStr[i].trim();
			try {
				retArr[i - removed] = Double.parseDouble(tmp);
			} catch (NumberFormatException e) {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static ResourceLocation[] getResourceLocationArrayProperty(Properties prop, String key, ResourceLocation[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		ResourceLocation[] retArr = new ResourceLocation[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			ResourceLocation rs = getResourceLocationFromString(splitStr[i], null);
			if (rs != null) {
				retArr[i - removed] = rs;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static BlockState[] getBlockStateArrayProperty(Properties prop, String key, BlockState[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(",");
		BlockState[] retArr = new BlockState[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			BlockState state = getBlockStateFromString(splitStr[i], null);
			if (state != null) {
				retArr[i - removed] = state;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static BlockPos[] getBlockPosArrayProperty(Properties prop, String key, BlockPos[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(";");
		BlockPos[] retArr = new BlockPos[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			BlockPos pos = getBlockPosFromString(splitStr[i], null);
			if (pos != null) {
				retArr[i - removed] = pos;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static DungeonSpawnPos[] getDungeonSpawnPosArrayProperty(Properties prop, String key, DungeonSpawnPos[] defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr = s.split(";");
		DungeonSpawnPos[] retArr = new DungeonSpawnPos[splitStr.length];
		int removed = 0;
		for (int i = 0; i < splitStr.length; i++) {
			DungeonSpawnPos dungeonSpawnPos = getDungeonSpawnPosFromString(splitStr[i], null);
			if (dungeonSpawnPos != null) {
				retArr[i - removed] = dungeonSpawnPos;
			} else {
				retArr = ArrayUtils.remove(retArr, i - removed);
				removed++;
			}
		}

		if (!allowEmpty && retArr.length == 0) {
			return defVal;
		}

		return retArr;
	}

	public static CQRWeightedRandom<BlockState> getWeightedBlockStateList(Properties prop, String key, CQRWeightedRandom<BlockState> defVal, boolean allowEmpty) {
		String s = prop.getProperty(key);
		if (s == null) {
			return defVal;
		}

		s = s.trim();
		if (!allowEmpty && s.isEmpty()) {
			return defVal;
		}

		String[] splitStr1 = s.split(";");
		CQRWeightedRandom<BlockState> retList = new CQRWeightedRandom<>();
		for (String string : splitStr1) {
			String[] splitStr2 = string.split(",");
			if (splitStr2.length >= 2) {
				BlockState state = getBlockStateFromString(splitStr2[0], null);
				int weight = 0;
				try {
					weight = Integer.parseInt(splitStr2[1].trim());
				} catch (NumberFormatException e) {
					// ignore
				}
				if (state != null && weight > 0) {
					retList.add(state, weight);
				}
			}
		}

		if (!allowEmpty && retList.numItems() == 0) {
			return defVal;
		}

		return retList;
	}

	private static ResourceLocation getResourceLocationFromString(String s, ResourceLocation defVal) {
		String[] splitStr = s.split(":");
		String namespace = "minecraft";
		String path = null;

		if (splitStr.length >= 2) {
			String s1 = splitStr[0].trim();
			String s2 = splitStr[1].trim();
			if (!s1.isEmpty()) {
				namespace = s1;
			}
			if (!s2.isEmpty()) {
				path = s2;
			}
		} else {
			String s1 = s.trim();
			if (!s1.isEmpty()) {
				path = s1;
			}
		}

		return path != null ? new ResourceLocation(namespace, path) : defVal;
	}

	/*
	 * DONE: Change to fit the new system with square bracket stuff...
	 */
	private static BlockState getBlockStateFromString(String s, BlockState defVal) {
		BlockState parsed = IReplaceBlocksProcessor.parseBlockState(s);
		if(parsed != null) {
			return parsed;
		}
		return defVal;
	}

	public static BlockPos getBlockPosFromString(String s, BlockPos defVal) {
		String[] splitStr = s.split(",");

		if (splitStr.length >= 3) {
			try {
				int x = Integer.parseInt(splitStr[0].trim());
				int y = Integer.parseInt(splitStr[1].trim());
				int z = Integer.parseInt(splitStr[2].trim());
				return new BlockPos(x, y, z);
			} catch (NumberFormatException e) {
				// ignore
			}
		}

		return defVal;
	}

	public static DungeonSpawnPos getDungeonSpawnPosFromString(String s, DungeonSpawnPos defVal) {
		String[] splitStr = s.split(",");

		if (splitStr.length >= 2) {
			try {
				int x = Integer.parseInt(splitStr[0].trim());
				int z = Integer.parseInt(splitStr[1].trim());
				boolean spawnPointRelative = false;
				if (splitStr.length >= 3) {
					spawnPointRelative = Boolean.valueOf(splitStr[2].trim());
				}
				return new DungeonSpawnPos(x, z, spawnPointRelative);
			} catch (NumberFormatException e) {
				// ignore
			}
		}

		return defVal;
	}

}
