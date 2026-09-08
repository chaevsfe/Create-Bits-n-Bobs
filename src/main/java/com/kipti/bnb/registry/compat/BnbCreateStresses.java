package com.kipti.bnb.registry.compat;

import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.infrastructure.config.AllConfigs;
import com.zurrtum.create.infrastructure.config.CStress;

public class BnbCreateStresses {

    public static void registerRedirects() {
        BlockStressValues.IMPACTS.registerProvider((p) -> {
            if (BnbKineticBlocks.CHAIN_PULLEY.is(p)) {
                final CStress stress = AllConfigs.server().kinetics.stressValues;
                return stress.getImpact(AllBlocks.ROPE_PULLEY);
            }
            return null;
        });
    }

}

