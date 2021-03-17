package lime.managers;

import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class FriendManager {
    ArrayList<String> friends;
    public FriendManager(){
        friends = new ArrayList<>();
    }

    public boolean isIn(String name){
        for(String entity : friends){
            if(name.equalsIgnoreCase(entity)) return true;
        }
        return false;
    }

    public void deleteFriend(String ent){
        friends.remove(ent);
    }

    public void addFriend(String ent){
        friends.add(ent);
    }

    public ArrayList<String> getFriends() {
        return friends;
    }
}
