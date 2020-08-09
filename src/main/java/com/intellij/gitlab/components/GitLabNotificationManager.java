package com.intellij.gitlab.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;

import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;
import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;

public class GitLabNotificationManager implements BaseComponent {

    private static final NotificationGroup BALLON_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("GitLab Notifications");
    private static final NotificationGroup STICKY_BALLOON_NOTIFICATION_GROUP = new NotificationGroup("GitLab Notifications", STICKY_BALLOON, true);


    public static GitLabNotificationManager getInstance(){
        return ApplicationManager.getApplication().getComponent(GitLabNotificationManager.class);
    }

    public Notification createNotification(String title, String content){
        return BALLON_NOTIFICATION_GROUP.createNotification(title, null, content, INFORMATION);
    }

    public Notification createNotificationError(String title, String content){
        return STICKY_BALLOON_NOTIFICATION_GROUP.createNotification(title, null, content, ERROR);
    }

    public Notification createSilentNotification(String title, String content){
        return BALLON_NOTIFICATION_GROUP.createNotification(title, null, content, INFORMATION);
    }

}
