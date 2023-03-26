package ru.uskov.dmitry.http.rest.server.utils;

import java.util.regex.Pattern;

import static ru.uskov.dmitry.http.rest.server.utils.TemplateUtils.TEMPLATE_REGEXP;

public class UrlUtils {

    private static final String URL_PATTERN =
            "[/ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-]*";

    private static final Pattern URL_VALIDATION_PATTERN =
            Pattern.compile("^(" + URL_PATTERN + "(/" + TEMPLATE_REGEXP + "/*)*)+$");

    public static boolean validatePath(String path) {
        if (path == null) {
            throw new NullPointerException();
        }
        return URL_VALIDATION_PATTERN.matcher(path).matches();
    }


    public static String cleanPath(String path) {
        if (path == null) {
            throw new NullPointerException();
        }

        path = path.trim();
        if (path.isEmpty()) {
            return "/";
        }

        path = path.replaceAll("/+", "/");

        if (path.equals("/")) {
            return path;
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        if (path.charAt(0) != '/') {
            path = '/' + path;
        }
        return path;
    }
}
