//package team.cqr.cqrepoured.command;
//
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.text.StringTextComponent;
//import team.cqr.cqrepoured.CQRMain;
//import team.cqr.cqrepoured.tileentity.TileEntityExporter;
//
//import java.io.File;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class CommandExport extends CommandBase {
//
//	@Override
//	public String getName() {
//		return "cqr_export_all";
//	}
//
//	@Override
//	public String getUsage(ICommandSender sender) {
//		return "/cqr_export_all [override_files]";
//	}
//
//	@Override
//	public int getRequiredPermissionLevel() {
//		return 4;
//	}
//
//	@Override
//	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//		List<TileEntityExporter> exporterList = new LinkedList<>();
//
//		for (TileEntity tileEntity : sender.getEntityWorld().loadedTileEntityList) {
//			if (tileEntity instanceof TileEntityExporter) {
//				exporterList.add((TileEntityExporter) tileEntity);
//			}
//		}
//
//		sender.sendMessage(new StringTextComponent("Trying to export " + exporterList.size() + " structures..."));
//
//		Set<String> fileNames = new HashSet<>();
//		for (int i = 0; i < exporterList.size(); i++) {
//			TileEntityExporter exporter = exporterList.get(i);
//			if (!fileNames.add(exporter.getStructureName())) {
//				exporterList.remove(i--);
//				sender.sendMessage(new StringTextComponent("Couldn't export structure " + exporter.getStructureName() + " because there is another exporter which wants to write to that file."));
//			}
//		}
//
//		for (TileEntityExporter exporter : exporterList) {
//			File file = new File(CQRMain.CQ_EXPORT_FILES_FOLDER, exporter.getStructureName() + ".nbt");
//
//			if (!file.exists() || (args.length >= 1 && args[0].equals("true"))) {
//				exporter.saveStructure((PlayerEntity) sender);
//			} else {
//				sender.sendMessage(new StringTextComponent("Couldn't export structure " + exporter.getStructureName() + " because a file with that name already exists and file overriding is disabled."));
//			}
//		}
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
//		if (args.length == 1) {
//			return Stream.of("false", "true").collect(Collectors.toList());
//		} else {
//			return Collections.emptyList();
//		}
//	}
//
//}
