package lime.managers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager {
    private final ArrayList<String> friends;

    public FriendManager() {
        this.friends = new ArrayList<>();
    }

    public void addFriend(String name) {
        friends.add(name);
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public boolean isFriend(Entity entity) {
        return entity instanceof EntityPlayer && friends.contains(entity.getName());
    }
}
