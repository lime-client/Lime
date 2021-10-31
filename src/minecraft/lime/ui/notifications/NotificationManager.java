package lime.ui.notifications;

import lime.core.events.impl.Event2D;

import java.util.ArrayList;

public class NotificationManager {
    private final ArrayList<Notification> notifications;

    public NotificationManager() {
        this.notifications = new ArrayList<>();
    }

    public void addNotification(String title, Notification.Type type) {
        this.notifications.add(new Notification(title, type));
    }

    public void addNotification(String title, int time, Notification.Type type) {
        this.notifications.add(new Notification(title, time, type));
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void drawNotifications(Event2D e) {
        int i = 0;
        for (Notification notification : notifications) {
            notification.drawNotification(e.getScaledResolution().getScaledWidth() - notification.getWidth() - 4, e.getScaledResolution().getScaledHeight() - notification.getHeight() - (i * (notification.getHeight() + 4) + 4));
            ++i;
        }

        notifications.removeIf(Notification::isDone);
    }
}
