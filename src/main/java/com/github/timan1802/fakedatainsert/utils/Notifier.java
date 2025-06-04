package com.github.timan1802.fakedatainsert.utils;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {

  public static final String NOTIFICATION_GROUP = "com.github.timan1802.fakedatainsert.NotificationGroup";

  public static void info(Project project, String content) {
    notify(content, NotificationType.INFORMATION, project);
  }

  public static void warn(Project project, String content) {
    notify(content, NotificationType.WARNING, project);
  }

  public static void error(Project project, String content) {
    notify(content, NotificationType.ERROR, project);
  }

  public static void notify(String content, NotificationType notificationType, Project project) {
    NotificationGroupManager.getInstance()
      .getNotificationGroup(NOTIFICATION_GROUP)
      .createNotification("FakeDataInsert", content, notificationType)
      .notify(project);
  }
}
