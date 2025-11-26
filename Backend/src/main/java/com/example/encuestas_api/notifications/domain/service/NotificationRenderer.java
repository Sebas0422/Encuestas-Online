package com.example.encuestas_api.notifications.domain.service;

import com.example.encuestas_api.notifications.domain.valueobject.Message;
import com.example.encuestas_api.notifications.domain.valueobject.NotificationTemplate;
import com.example.encuestas_api.notifications.domain.valueobject.TemplateModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NotificationRenderer {
    private static final Pattern VAR = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_\\.]+)\\s*}}");

    private NotificationRenderer() {}

    public static Message render(NotificationTemplate template, TemplateModel model) {
        String subj = template.getSubjectTmpl() == null ? "" : replace(template.getSubjectTmpl(), model);
        String body = replace(template.getBodyTmpl(), model);
        return new Message(subj, body);
    }

    private static String replace(String tmpl, TemplateModel model) {
        Matcher m = VAR.matcher(tmpl);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String val = model.getString(key);
            m.appendReplacement(sb, Matcher.quoteReplacement(val == null ? "" : val));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
