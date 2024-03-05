package com.tugalsan.api.font.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.font.client.TGS_FontFamily;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
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

    public static Font ofPlain(Path fontPath, float height) {
        return of(fontPath, height, Font.PLAIN);
    }

    public static Font ofBold(Path fontPath, float height) {
        return of(fontPath, height, Font.BOLD);
    }

    public static Font ofItalic(Path fontPath, float height) {
        return of(fontPath, height, Font.ITALIC);
    }

    public static Font ofBoldItalic(Path fontPath, float height) {
        return of(fontPath, height, Font.BOLD | Font.ITALIC);
    }

    private static Font of(Path path, float height, int style) {
//        var fontAlreadyExists = fontBuffer.stream()
//                .filter(t -> t.path.equals(path))
//                .filter(t -> t.style == style)
//                .filter(t -> t.height == height)
//                .map(t -> t.font)
//                .findAny().orElse(null);
//        if (fontAlreadyExists != null) {
//            return fontAlreadyExists;
//        }
        var newFont = TGS_UnSafe.call(() -> {
            var typeStr = TS_FileUtils.getNameType(path).toLowerCase();
            if (!Objects.equals(typeStr, "ttf")) {
                throw new IllegalArgumentException("Unknown font type '%s'".formatted(typeStr));
            }
            var fontType = Font.TRUETYPE_FONT;
            var font = Font.createFont(fontType, path.toFile()).deriveFont(style, height);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        });
//        fontBuffer.add(new FontBufferItem(path, height, style, newFont));
        return newFont;
    }
//    final private static TS_ThreadSyncLst<FontBufferItem> fontBuffer = TS_ThreadSyncLst.of();
//
//    final private record FontBufferItem(Path path, int height, int style, Font font) {
//
//    }

    public static TGS_FontFamily<Font> toFont(TGS_FontFamily<Path> fontFalimyPath, int derivedFontHeight) {
        return new TGS_FontFamily(
                ofPlain(fontFalimyPath.regular(), derivedFontHeight),//USE REGULAR FOR JAVA
                ofBold(fontFalimyPath.regular(), derivedFontHeight),//USE REGULAR FOR JAVA
                ofItalic(fontFalimyPath.regular(), derivedFontHeight),//USE REGULAR FOR JAVA
                ofBoldItalic(fontFalimyPath.regular(), derivedFontHeight)//USE REGULAR FOR JAVA
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
