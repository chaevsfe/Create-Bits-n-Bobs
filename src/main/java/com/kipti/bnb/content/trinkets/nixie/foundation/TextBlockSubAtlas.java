package com.kipti.bnb.content.trinkets.nixie.foundation;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.client.catnip.render.StitchedSprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Util class to handle a sub-atlas of text characters for Nixie displays.
 * This class generates UV coordinates for a set of characters, the texture must be already in the block atlas.
 */
@Environment(EnvType.CLIENT)
public class TextBlockSubAtlas {

    /**
     * Additional chars are from Unicode private use area
     */
    public static final TextBlockSubAtlas NIXIE_TEXT_SUB_ATLAS = new TextBlockSubAtlas(
            CreateBitsnBobs.asResource("block/nixie/text_atlas"),
            512, 16,
            """
                    ABCDEFGHIJKLMNOPQRSTUVWXYZ
                    abcdefghijklmnopqrstuvwxyz
                    1234567890(),.!?#$%\\@;:
                    \uE000\uE001\uE002\uE003\uE004\uE005\uE006\uE007\uE008\uE009\uE00A\uE00B\uE00C\uE00D\uE00E\uE00F\
                    \uE010\uE011\uE012\uE013\uE014\uE015\uE016\uE017\uE018\uE019\uE01A\uE01B\uE01C\uE01D\uE01E\uE01F
                    """.trim()
    ).andExcludeColorsFor(
            "\uE000\uE001\uE002\uE003\uE004\uE005\uE006\uE007\uE008\uE009\uE00A\uE00B\uE00C\uE00D\uE00E\uE00F" +
                    "\uE010\uE011\uE012\uE014\uE015\uE016\uE017\uE018\uE019\uE01A\uE01B\uE01C\uE01D\uE01E\uE01F"
    ); //\uE013 is the heart emoji, which should be colorable

    /**
     * Additional chars are from Unicode private use area
     */
    public static final TextBlockSubAtlas SMALL_NIXIE_TEXT_SUB_ATLAS = NIXIE_TEXT_SUB_ATLAS.withAllowedCharacters(
            "\uE000\uE001\uE002\uE003\uE004\uE005\uE006\uE007\uE008\uE009\uE00A\uE00B\uE00C\uE00D\uE00E\uE00F" +
                    "\uE010\uE011\uE012\uE013\uE014\uE015\uE016\uE017\uE018\uE019\uE01A\uE01B\uE01C\uE01D\uE01E\uE01F"
    );

    private final Identifier atlasLocation;
    private final Map<Integer, Uv> localUvs;
    private final StitchedSprite stitched;
    private Map<Integer, Uv> characterUvs;
    private TextureAtlasSprite resolvedFor;
    /**
     * Used to select only a certain part of the atlas to be avaliable, such as for the small text to only keep the special characters and use normal rendering when possible.
     */
    private final @Nullable Set<Integer> allowedCharacters;
    /**
     * Characters to exclude from color rendering, such as emojis that should always be rendered in white.
     */
    private @Nullable Set<Integer> colorExcludedCharacters;

    public TextBlockSubAtlas(final Identifier atlasLocation, final int textureSize, final int elementSize, final String characterSet, @Nullable final String allowedCharacters) {
        this.atlasLocation = atlasLocation;
        this.colorExcludedCharacters = null;

        this.allowedCharacters = allowedCharacters != null ? allowedCharacters.chars().boxed().collect(Collectors.toSet()) : null;

        final Map<Character, Uv> localUvs = new HashMap<>();

        //Calculate the local uvs for each character in the set
        //Process the uv for each character in the set, if the cursor is at the end of a line, it will wrap to the next line
        // if a \n is encountered, it will reset the cursor to the start of the next line
        final float step = ((float) textureSize / elementSize);

        float x = 0, y = 0;

        for (int i = 0; i < characterSet.length(); i++) {
            final char c = characterSet.charAt(i);
            if (c == '\n') {
                x = 0;
                y += step;
                continue;
            }

            final float u0 = x / textureSize;
            final float v0 = y / textureSize;
            final float u1 = (x + step) / textureSize;
            final float v1 = (y + step) / textureSize;

            localUvs.put(c, new Uv(u0, v0, u1, v1));

            x += step;
            if (x >= textureSize) {
                x = 0;
                y += step;
            }
        }

        // The atlas is not stitched yet when this runs, so the block atlas UVs are resolved on first use.
        final Map<Integer, Uv> byCode = new HashMap<>();
        for (final Map.Entry<Character, Uv> entry : localUvs.entrySet())
            byCode.put((int) (char) entry.getKey(), entry.getValue());
        this.localUvs = byCode;
        this.stitched = new StitchedSprite(atlasLocation);
    }

    public TextBlockSubAtlas(final Identifier atlasLocation, final int textureSize, final int elementSize, final String characterSet) {
        this(atlasLocation, textureSize, elementSize, characterSet, null);
    }

    private TextBlockSubAtlas(final TextBlockSubAtlas parent, final String allowedCharacters) {
        this.atlasLocation = parent.atlasLocation;
        this.allowedCharacters = allowedCharacters.chars().boxed().collect(Collectors.toSet());
        this.localUvs = parent.localUvs;
        this.stitched = parent.stitched;
        this.colorExcludedCharacters = parent.colorExcludedCharacters;
    }

    /** Forces class initialisation so the StitchedSprite is registered before the first atlas stitch. */
    public static void register() {
    }

    public TextBlockSubAtlas withAllowedCharacters(final String allowedCharacters) {
        return new TextBlockSubAtlas(this, allowedCharacters);
    }

    private TextBlockSubAtlas andExcludeColorsFor(final String colorExcludedCharacters) {
        this.colorExcludedCharacters = colorExcludedCharacters.chars().boxed().collect(Collectors.toSet());
        return this;
    }

    public Identifier getAtlasLocation() {
        return atlasLocation;
    }

    public boolean isInCharacterSet(final int c) {
        return localUvs.containsKey(c) && (allowedCharacters == null || allowedCharacters.contains(c));
    }

    private Map<Integer, Uv> resolved() {
        final TextureAtlasSprite sprite = stitched.get();
        if (sprite == null)
            return Map.of();
        if (sprite == resolvedFor && characterUvs != null)
            return characterUvs;

        final float u0 = sprite.getU0();
        final float v0 = sprite.getV0();
        final float u1 = sprite.getU1();
        final float v1 = sprite.getV1();
        final Map<Integer, Uv> mapped = new HashMap<>();
        for (final Map.Entry<Integer, Uv> entry : localUvs.entrySet()) {
            final Uv local = entry.getValue();
            mapped.put(entry.getKey(), new Uv(
                    u0 + local.u0 * (u1 - u0),
                    v0 + local.v0 * (v1 - v0),
                    u0 + local.u1 * (u1 - u0),
                    v0 + local.v1 * (v1 - v0)
            ));
        }
        resolvedFor = sprite;
        characterUvs = mapped;
        return mapped;
    }

    public Uv getUvForCharacter(final int c) {
        return resolved().getOrDefault(c, new Uv(0, 0, 0, 0)); // Return empty UVs if character not found
    }

    public boolean isInColorExcludedCharacterSet(final int charCode) {
        return colorExcludedCharacters != null && colorExcludedCharacters.contains(charCode);
    }

    public static class Uv {
        public final float u0;
        public final float v0;
        public final float u1;
        public final float v1;

        public Uv(final float u0, final float v0, final float u1, final float v1) {
            this.u0 = u0;
            this.v0 = v0;
            this.u1 = u1;
            this.v1 = v1;
        }

        public float getU0() {
            return u0;
        }

        public float getV0() {
            return v0;
        }

        public float getU1() {
            return u1;
        }

        public float getV1() {
            return v1;
        }
    }

}

