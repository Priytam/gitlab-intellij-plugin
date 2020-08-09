package com.intellij.gitlab.util;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.ImageUtil;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import static java.util.Objects.isNull;

public class GitLabIconUtil {

    private static final int SMALL_ICON = 16;


    public static Icon getIcon(@Nullable String iconUrl){
        if(StringUtil.isEmpty(iconUrl)){
            return null;
        }

        try {
            return IconLoader.findIcon(new URL(iconUrl));
        } catch (MalformedURLException e) {
            return null;
        }
    }


    public static Icon getSmallIcon(@Nullable String iconUrl){
        Icon icon = getIcon(iconUrl);
        if(isNull(icon)){
            return null;
        }

        Image image = IconLoader.toImage(icon);
        BufferedImage bufferedImage = ImageUtil.toBufferedImage(image);
        BufferedImage resizeImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, SMALL_ICON);

        return new ImageIcon(resizeImage);
    }

}
