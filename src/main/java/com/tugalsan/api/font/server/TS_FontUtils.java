package com.tugalsan.api.font.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.font.client.TGS_FontFamily;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.tuple.client.TGS_Tuple3;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.List;
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
        var fontAlreadyExists = of_buffer.stream()
                .filter(t -> t.value0.equals(fontPath))
                .filter(t -> t.value1.equals(derivedFontHeight))
                .map(t -> t.value2)
                .findAny().orElse(null);
        if (fontAlreadyExists != null) {
            return fontAlreadyExists;
        }
        var newFont = TGS_UnSafe.call(() -> {
            var typeStr = TS_FileUtils.getNameType(fontPath).toLowerCase();
            if (!Objects.equals(typeStr, "ttf")) {
                throw new IllegalArgumentException("Unknown font type '%s'".formatted(typeStr));
            }
            var fontType = Font.TRUETYPE_FONT;
            var font = Font.createFont(fontType, fontPath.toFile()).deriveFont(derivedFontHeight);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        });
        of_buffer.add(TGS_Tuple3.of(fontPath, derivedFontHeight, newFont));
        return newFont;
    }
    final private static TS_ThreadSyncLst<TGS_Tuple3<Path, Integer, Font>> of_buffer = TS_ThreadSyncLst.of();

    public static TGS_FontFamily<Font> toFont(TGS_FontFamily<Path> fontFalimyPath, int derivedFontHeight) {
        return new TGS_FontFamily(
                of(fontFalimyPath.regular(), derivedFontHeight),
                of(fontFalimyPath.bold(), derivedFontHeight),
                of(fontFalimyPath.italic(), derivedFontHeight),
                of(fontFalimyPath.boldItalic(), derivedFontHeight)
        );
    }

    public static List<TGS_FontFamily<Font>> toFont(List<TGS_FontFamily<Path>> fontFalimyPaths, int derivedFontHeight) {
        return TGS_StreamUtils.toLst(
                fontFalimyPaths.stream()
                        .map(ffp -> toFont(ffp, derivedFontHeight))
        );
    }

    public static List<String> listRegisteredFontNames() {
        return TGS_ListUtils.of(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()
        );
    }
}
