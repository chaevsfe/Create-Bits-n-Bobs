package com.kipti.bnb;

import com.kipti.bnb.content.trinkets.nixie.foundation.TextBlockSubAtlas;
import com.cake.struts.compat.flywheel.StrutsFlywheelCompatLoader;
import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialContext;
import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialRenderer;
import com.kipti.bnb.foundation.behaviour.drag.DragInteractionClientHandler;
import com.kipti.bnb.foundation.client.BnbClientEvents;
import com.kipti.bnb.foundation.client.GenericBlockEntityRenderModels;
import com.kipti.bnb.foundation.ponder.BnbPonderPlugin;
import com.kipti.bnb.registrate.client.RegistrateClient;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.kipti.bnb.registry.content.BnbBlockEntities;
import com.kipti.bnb.content.kinetics.cogwheel_chain.render.CogwheelChainLevelRenderer;
import com.kipti.bnb.content.kinetics.gigantic_cogwheel.GiganticCogwheelRenderer;
import com.kipti.bnb.content.trinkets.light.headlamp.rendering.pipeline.block_entity.HeadlampVertexBufferCache;
import com.zurrtum.create.client.catnip.render.SuperByteBufferCache;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CreateBitsnBobsClient {

    private CreateBitsnBobsClient() {
    }

    public static void initClient() {
        RegistrateClient.flush(CreateBitsnBobs.REGISTRATE);

        BnbPartialModels.register();
        BnbSpriteShifts.register();
        TextBlockSubAtlas.register();
        CogwheelMaterialRenderer.init();
        GenericBlockEntityRenderModels.register();
        CogwheelChainLevelRenderer.register();
        DragInteractionClientHandler.register();
        BnbClientEvents.register();

        SuperByteBufferCache.getInstance().registerCompartment(GiganticCogwheelRenderer.GIGANTIC_COGWHEEL_CACHE);
        SuperByteBufferCache.getInstance().registerCompartment(HeadlampVertexBufferCache.HEADLAMP);
        SuperByteBufferCache.getInstance().registerCompartment(CogwheelMaterialContext.COGWHEEL_MATERIAL);

        StrutsFlywheelCompatLoader.registerStrutVisual(BnbBlockEntities.GIRDER_STRUT.get());

        PonderIndex.addPlugin(new BnbPonderPlugin());
    }
}
