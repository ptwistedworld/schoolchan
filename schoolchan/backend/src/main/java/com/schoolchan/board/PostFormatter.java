package com.schoolchan.board;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PostFormatter {

    private static final Pattern QUOTE_LINK = Pattern.compile(">>(\\d+)");

    public String format(Post post) {
        String raw = post.getTranslatedText() != null && !post.getTranslatedText().isBlank()
                ? post.getTranslatedText()
                : post.getOriginalText();
        return formatPlain(raw);
    }

    public String formatPlain(String raw) {
        if (raw == null) {
            return "";
        }
        String[] lines = raw.split("\\r?\\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append("<br/>");
            }
            sb.append(formatLine(lines[i]));
        }
        return sb.toString();
    }

    private String formatLine(String line) {
        if (line.startsWith(">") && !line.startsWith(">>")) {
            return "<span class=\"greentext\">" + HtmlUtils.htmlEscape(line) + "</span>";
        }
        Matcher m = QUOTE_LINK.matcher(line);
        StringBuffer buf = new StringBuffer();
        int last = 0;
        while (m.find()) {
            buf.append(HtmlUtils.htmlEscape(line.substring(last, m.start())));
            String num = m.group(1);
            buf.append("<a href=\"#p").append(num).append("\" class=\"quote-link\">&gt;&gt;")
                    .append(num).append("</a>");
            last = m.end();
        }
        buf.append(HtmlUtils.htmlEscape(line.substring(last)));
        return buf.toString();
    }
}
