package team.cqr.cqrepoured.client.mcmetaserializers;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.client.resources.data.BaseMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Tuple;

//TODO: Change to be completely json based, not some weird string monsters
public class GlowingMetadataSectionSerializer extends BaseMetadataSectionSerializer<GlowingMetadataSection> implements JsonSerializer<GlowingMetadataSection> {

	@Override
	public String getSectionName() {
		return "glowsections";
	}

	@Override
	public GlowingMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonobject = JsonUtils.getJsonObject(json, "metadata section");
		if (jsonobject.has("sections")) {
			JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "sections");
			GlowingMetadataSection result = new GlowingMetadataSection();
			for (int i = 0; i < jsonarray.size(); ++i) {
				JsonElement jsonelement = jsonarray.get(i);
				String entry1 = JsonUtils.getString(jsonelement.getAsJsonObject(), "first");
				String entry2 = JsonUtils.getString(jsonelement.getAsJsonObject(), "second");

				if (entry1.split("-").length != 2) {
					continue;
				}
				
				if (entry2.split("-").length != 2) {
					continue;
				}

				String[] split = entry1.split("-");
				Tuple<Integer, Integer> pos1 = new Tuple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
				split = entry2.split("-");
				Tuple<Integer, Integer> pos2 = new Tuple<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
				if (pos1.getFirst() <= pos2.getFirst() && pos1.getSecond() <= pos2.getSecond()) {
					result.addSection(pos1, pos2);
				}

			}
			if (!result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(GlowingMetadataSection src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonobject = new JsonObject();

		if (!src.isEmpty()) {
			JsonArray jsonarray = new JsonArray();

			for (Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> entry : src.getGlowingSections()) {
				JsonObject jsonobject1 = new JsonObject();
				jsonobject1.addProperty("first", entry.getFirst().getFirst() + "-" + entry.getFirst().getSecond());
				jsonobject1.addProperty("second", entry.getSecond().getFirst() + "-" + entry.getSecond().getSecond());

				jsonarray.add(jsonobject1);
			}

			jsonobject.add("sections", jsonarray);
		}

		return jsonobject;
	}

}
