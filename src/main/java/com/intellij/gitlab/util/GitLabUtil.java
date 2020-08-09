package com.intellij.gitlab.util;

import com.intellij.util.text.DateFormatUtil;

import java.util.Date;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class GitLabUtil {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getPrettyDateTime(Date date){
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : "";
    }

}
