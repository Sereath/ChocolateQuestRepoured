package com.teamcqr.chocolatequestrepoured.intrusive.ASM;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * Used by the CQR IntrusiveModificationHelper for ASM-related tasks
 * Should not be interacted with directly unless you 1000% know what you're doing
 */
public class CQRPlugin implements IFMLLoadingPlugin {

    // Variables
    private boolean isDevEnvironment = false; // Should be properly overridden later; defaults to false so as to prioritise end user\
    public boolean getIsDevEnvironment() { return isDevEnvironment; }

    /**
     * FML: Returns a list of names of transformer classes that implement IClassTransformer
     */
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    /**
     * FML: Returns a ModContainer for injection into Forge Mod List
     */
    @Override
    public String getModContainerClass() {
        return CQRCoremodContainer.getInstance().getClass().getName();
    }

    /**
     * FML: Inject coremod data such as mcLocation, coremodList, and coremodLocation.
     *
     * Used in CQR to determine if running in a development environment
     */
    @Override
    public void injectData(Map<String, Object> data) {
        isDevEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    /* Unused in CQR */
    public String getSetupClass() { return null; }
    public String getAccessTransformerClass() { return null; }
    /* Unused in CQR */

}
