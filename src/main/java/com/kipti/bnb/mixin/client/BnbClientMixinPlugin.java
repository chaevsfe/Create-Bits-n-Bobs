package com.kipti.bnb.mixin.client;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BnbClientMixinPlugin implements IMixinConfigPlugin {

    private static final String ENCASED_PACKAGE = "com/zurrtum/create/client/content/kinetics/simpleRelays/encased/";
    private static final String CHAIN_COGWHEEL_VISUAL = "com/hlysine/create_connected/content/chaincogwheel/ChainCogwheelVisual.class";

    @Override
    public void onLoad(final String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        final List<String> mixins = new ArrayList<>();
        if (classExists("EncasedSmallCogVisual") && classExists("EncasedLargeCogVisual"))
            mixins.add("cogwheel_material.EncasedSplitCogVisualMaterialMixin");
        else if (classExists("EncasedCogVisual"))
            mixins.add("cogwheel_material.EncasedCogVisualMaterialMixin");
        if (resourceExists(CHAIN_COGWHEEL_VISUAL))
            mixins.add("cogwheel_material.ChainCogwheelVisualMaterialMixin");
        return mixins;
    }

    private static boolean classExists(final String simpleName) {
        return resourceExists(ENCASED_PACKAGE + simpleName + ".class");
    }

    private static boolean resourceExists(final String path) {
        return BnbClientMixinPlugin.class.getClassLoader().getResource(path) != null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }

}
