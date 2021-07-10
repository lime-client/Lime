package lime.ui.notifications.utils;

import lime.core.events.impl.Event2D;
import lime.ui.notifications.Notification;

import java.util.ArrayList;

public class NotificationManager {
    private final ArrayList<Notification> notifications;

    public NotificationManager() {
        this.notifications = new ArrayList<>();
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void renderNotifications(Event2D e) {
        if(!notifications.isEmpty()) {
            notifications.removeIf(Notification::isFinished);
            int i = 1;
            for (Notification notification : notifications) {
                notification.render(e, e.getScaledResolution().getScaledHeight() - (i * 42));
                ++i;
            }
        }
    }
}
