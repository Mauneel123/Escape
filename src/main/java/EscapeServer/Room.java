package EscapeServer;

import java.util.ArrayList;
import java.util.List;

class Room {
    private Integer reference;
    private Integer[] location;
    private Integer object;
    private Boolean isMonster;
    private String userID;
    private Boolean locked;
    private Integer level;
    private List<Integer> groundEquipList;

    Room(Integer reference, Integer[] location, Integer object, Boolean isMonster, String userID, Boolean locked, Integer level) {
        this.reference = reference;
        this.isMonster = isMonster;
        this.object = object;
        this.location = location;
        this.userID = userID;
        this.locked = locked;
        this.level = level;
        this.groundEquipList = new ArrayList<>();
    }

    List<Integer> getGroundEquipList() {
        return groundEquipList;
    }

    void setGroundEquipList(List<Integer> groundEquipList) {
        this.groundEquipList = groundEquipList;
    }

    void addGroundEquipment(Integer equipmentReference) {
        this.groundEquipList.add(equipmentReference);
    }

    void removeGroundEquipment(Integer equipmentReference) {
        this.groundEquipList.remove(equipmentReference);
    }

    void regenerateMonster(Integer newMonsterReference){
        this.object = newMonsterReference;
    }

    void printInformation() {
        System.out.println("Room with ref [" + this.reference + "] is located at: [" + this.location[0] + ", " + this.location[1] + "]. The room's level is " + this.level + ".");
    }

    void setObject(Integer object) {
        this.object = object;
    }

    void setLocked(Boolean locked) {
        this.locked = locked;
    }

    Integer getReference() {
        return this.reference;
    }

    Integer[] getLocation() {
        return this.location;
    }

    Integer getObject() {
        return this.object;
    }

    Boolean getMonster() {
        return this.isMonster;
    }

    String getUserID() {
        return this.userID;
    }

    Boolean getLocked() {
        return this.locked;
    }

    Integer getLevel() {
        return this.level;
    }

}
