package ru.uskov.dmitry.http.rest.server.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateUtils {
    public static final String TEMPLATE_REGEXP = "\\$\\{[a-zA-Z][a-zA-Z0-9]*\\}";
    public static final Pattern TEMPLATE_PATTERN_FINDER = Pattern.compile(TEMPLATE_REGEXP);
    public static final Pattern TEMPLATE_PATTERN_VALIDATOR = Pattern.compile("^" + TEMPLATE_REGEXP + "$");


    public static Map<String, Function<String, String>> getResolvers(@NotNull String path) {
        Matcher matcher = TEMPLATE_PATTERN_FINDER.matcher(path);
        List<String> templates = new ArrayList<>();
        while(matcher.find()) {
            String foundedTemplate = matcher.group(0);
            if(templates.contains(foundedTemplate)) {
                throw new IllegalArgumentException("There are two same template in one path: '" + path + "'. Same template: '" + foundedTemplate + "'");
            }
            templates.add(foundedTemplate);
        }
        String pathValueRegexp = "[a-zA-Z0-9-]+";
        Map<String, Function<String, String>> result = new HashMap<>();
        //crete Patter finder for each template
        for (String template : templates) {
            Pattern compile = Pattern.compile(path.replace(template, "(" + pathValueRegexp + ")").replaceAll(TEMPLATE_REGEXP, pathValueRegexp));
            result.put(clearTemplate(template), s -> {
                Matcher m = compile.matcher(s);
                boolean res = m.find();
                if(res) {
                    return m.group(1);
                } else {
                    return null;
                }
            });
        }
        return result;
    }

    private static String clearTemplate(String template) {
        return template.substring(2, template.length()-1);
    }


}
