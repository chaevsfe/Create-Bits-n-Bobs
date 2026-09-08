package com.kipti.bnb.registry.core;

import com.zurrtum.create.client.flywheel.api.material.DepthTest;
import com.zurrtum.create.client.flywheel.api.material.Material;
import com.zurrtum.create.client.flywheel.api.material.Transparency;
import com.zurrtum.create.client.flywheel.lib.material.SimpleMaterial;

public class BnbMaterials {

    public static final Material HEADLAMP_MATERIAL = SimpleMaterial.builder()
            .transparency(Transparency.ORDER_INDEPENDENT)
            .mipmap(false)
            .blur(false)
            .polygonOffset(true) // Ensure it renders on top of the block
            .depthTest(DepthTest.LEQUAL)
            .build();
    public static final Material HEADLAMP_NO_DIFFUSE_MATERIAL = SimpleMaterial.builder()
            .transparency(Transparency.ORDER_INDEPENDENT)
            .mipmap(false)
            .blur(false)
            .polygonOffset(true) // Ensure it renders on top of the block
            .diffuse(false)
            .depthTest(DepthTest.LEQUAL)
            .build();

}

