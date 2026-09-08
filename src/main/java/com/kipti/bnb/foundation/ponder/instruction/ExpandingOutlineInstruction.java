package com.kipti.bnb.foundation.ponder.instruction;

import com.kipti.bnb.foundation.client.outline.ExpandingLineOutline;
import com.zurrtum.create.client.ponder.api.PonderPalette;
import com.zurrtum.create.client.ponder.foundation.PonderScene;
import com.zurrtum.create.client.ponder.foundation.instruction.TickingInstruction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class ExpandingOutlineInstruction extends TickingInstruction {

    private final PonderPalette color;
    private final ExpandingLineOutline outline = new ExpandingLineOutline();

    public ExpandingOutlineInstruction(final PonderPalette color, final Vec3 start, final Vec3 end,
                                       final int ticks, final int growingTicks) {
        super(false, ticks);
        this.color = color;
        this.outline.setGrowingTicks(growingTicks);
        this.outline.set(start, end);
    }

    @Override
    public void onScheduled(final PonderScene scene) {
        super.onScheduled(scene);
        this.outline.setGrowingTicksElapsed(0);
    }

    @Override
    public void tick(final PonderScene scene) {
        super.tick(scene);
        this.outline.tickGrowingTicksElapsed();
        scene.getOutliner()
                .showOutline(this, this.outline)
                .lineWidth(1 / 16f)
                .colored(this.color.getColor());
    }
}
