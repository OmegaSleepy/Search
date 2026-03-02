package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterParser {

    public static ParsedChapter parse(String input) {
        input = input.trim();

        String arcOrVol = "UnknownArc";
        String chapter = "UnknownChapter";
        String title = "";

        // 1. Remove source after "|" or trailing "– site"
        String[] parts = input.split("\\|");
        String main = parts[0].trim();
        main = main.replaceAll("\\s+–\\s+[^–|]+$", ""); // remove trailing – source

        // 2. Extract Arc or Vol number
        Pattern arcPattern = Pattern.compile("(?:Arc|Vol)\\.?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher mArc = arcPattern.matcher(main);
        if (mArc.find()) {
            arcOrVol = mArc.group(1).trim();
        }

        // 3. Extract Chapter (after Chapter/Ch)
        Pattern chapPattern = Pattern.compile("(?:Chapter|Ch)\\.?\\s*([^–:,|“\"]+)", Pattern.CASE_INSENSITIVE);
        Matcher mChap = chapPattern.matcher(main);
        if (mChap.find()) {
            chapter = mChap.group(1).trim();
        }

        // 4. Extract Title (after dash or colon)
        Pattern titlePattern = Pattern.compile("(?:–|:|,)?\\s*(?:“|\")?(.*?)(?:”|\")?$");
        Matcher mTitle = titlePattern.matcher(main);
        if (mTitle.find()) {
            title = mTitle.group(1).trim();
        }

        return new ParsedChapter(arcOrVol, chapter, title);
    }

    public record ParsedChapter(String volume, String chapter, String title) {
    }

    public static void main(String[] args) {
        String[] test = {
                "Arc 7, Chapter 64 – “Magic Words” | Witch Cult Translations",
                "Arc 9, Interlude – “Cheer” | Witch Cult Translations",
                "Vol. 3 Ch. 4: A trip’s departure, many difficulties lay ahead – kagurojp",
                "Arc 6 – Chapter 42, “The Tower of the Dead” | Witch Cult Translations",
                "Arc 5, Chapter 67 – Liliana Masquerade | Witch Cult Translations"
        };

        for (String s : test) {
            ParsedChapter p = parse(s);
            System.out.println("Volume/Arc: " + p.volume + ", Chapter: " + p.chapter + ", Title: " + p.title);
        }
    }
}