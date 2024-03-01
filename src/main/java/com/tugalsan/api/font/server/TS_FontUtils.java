package com.tugalsan.api.font.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.Objects;

public class TS_FontUtils {

    public static int canDisplayUpTo(Font font, CharSequence text) {
        if (text == null) {
            return -1;
        }
        return font.canDisplayUpTo(text.toString());
    }

    public static boolean canDisplay(Font font, CharSequence text) {
        return canDisplayUpTo(font, text) == -1;
    }

    public static boolean canDisplay(Font font, int codePoint) {
        return font.canDisplay(codePoint);
    }

    public static Font of(Path fontPath, int derivedFontHeight) {
        return TGS_UnSafe.call(() -> {
            var typeStr = TS_FileUtils.getNameType(fontPath).toLowerCase();
            if (!Objects.equals(typeStr, "ttf")) {
                throw new IllegalArgumentException("Unknown font type '%s'".formatted(typeStr));
            }
            var fontType = Font.TRUETYPE_FONT;
            var font = Font.createFont(fontType, fontPath.toFile()).deriveFont(derivedFontHeight);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        });
    }
}
