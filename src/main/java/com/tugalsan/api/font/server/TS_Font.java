package com.tugalsan.api.font.server;

import com.tugalsan.api.coronator.client.TGS_Coronator;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.union.client.TGS_Union;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Path;

@Deprecated//TODO: NOT USED
public class TS_Font {

    public static enum STYLE {
        PLAIN, BOLD, ITALIC, BOLD_ITALIC;
    }
    final public static TS_ThreadSyncLst<TS_Font> buffer = TS_ThreadSyncLst.of();

    private TS_Font(TS_Font fontBall, int height, STYLE style) {
        this.filePath = fontBall.filePath;
        this.height = height;
        this.style = style;
        this.font = TGS_Coronator.of(Font.class).coronateAs(__ -> {
            if (style == STYLE.BOLD) {
                return fontBall.font.deriveFont(Font.BOLD, height);
            }
            if (style == STYLE.ITALIC) {
                return fontBall.font.deriveFont(Font.ITALIC, height);
            }
            if (style == STYLE.BOLD_ITALIC) {
                return fontBall.font.deriveFont(Font.BOLD | Font.ITALIC, height);
            }
            return fontBall.font.deriveFont(Font.PLAIN, height);
        });
        this.name = fontBall.name;
    }

    private TS_Font(Path filePath) throws FontFormatException, IOException {
        this.filePath = filePath;
        this.height = 1;
        this.style = STYLE.PLAIN;
        this.font = Font.createFont(Font.TRUETYPE_FONT, filePath.toFile());
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        this.name = font.getName();
    }
    final public Path filePath;
    final public int height;
    final public STYLE style;
    final public Font font;
    final public String name;

    public static TGS_Union<TS_Font> of(Path filePath) {
        try {
            return TGS_Union.of(new TS_Font(filePath));
        } catch (FontFormatException | IOException ex) {
            return TGS_Union.ofThrowable(ex);
        }
    }

    public TS_Font derive(int height, STYLE style) {
        return new TS_Font(this, height, style);
    }
}
