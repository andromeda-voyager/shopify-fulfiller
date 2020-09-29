package sample;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


class ClipboardIO {

    private static Clipboard clipboard = Clipboard.getSystemClipboard();

    static String readClipboard() {
        return clipboard.getString();
    }

    static void writeToClipboard(String content) {
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
    }

}
