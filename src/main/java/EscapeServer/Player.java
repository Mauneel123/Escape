package EscapeServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Player {
    private String name;
    private String userID;
    private Integer roomReference;
    private Integer score;
    private Integer highScore;
    private Integer experience;
    private Integer level;
    private Integer health;
    private Integer maxHealth;
    private Integer armor;
    private Integer attack;
    private Integer actionCount;
    private Integer[] equipmentReferences;
    private Integer maxSlotAmount;
    private List<Integer> keys;
    private List<String> friendIDsList;
    private List<String> friendNamesList;


    void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    Integer getHighScore() {
        return highScore;
    }

    public void setHighScore(Integer highScore) {
        this.highScore = highScore;
    }

    Player(String name, String userID, Integer score, Integer highScore, Integer actionCount, Integer experience, Integer level, Integer maxHealth, Integer health, Integer armor, Integer attack) {
        this.roomReference = 0;
        this.maxSlotAmount = 2;
        this.name = name;
        this.userID = userID;
        this.actionCount = actionCount;
        this.experience = experience;
        this.level = level;
        this.health = health;
        this.maxHealth = maxHealth;
        this.armor = armor;
        this.attack = attack;
        this.equipmentReferences = new Integer[maxSlotAmount];
        this.keys = new ArrayList<Integer>();
        this.equipmentReferences[0] = -1;
        this.equipmentReferences[1] = -1;
        this.friendIDsList = new ArrayList<String>();
        this.friendNamesList = new ArrayList<String>();
        this.score = score;
        this.highScore = highScore;
    }

    List<String> getFriendIDsList() {
        return friendIDsList;
    }

    void setFriendIDsList(List<String> friendIDsList) {
        this.friendIDsList = friendIDsList;
    }

    List<String> getFriendNamesList() {
        return friendNamesList;
    }

    void setFriendNamesList(List<String> friendNamesList) {
        this.friendNamesList = friendNamesList;
    }

    void setNewNameForFriendByID(String userId, String newName){
        for(int i = 0; i < this.friendNamesList.size(); i++){
            if(this.friendIDsList.get(i).equals(userId)){
                this.friendNamesList.set(i, newName);
                break;
            }
        }
    }

    void setNewNameForFriend(String oldName, String newName){
        for(int i = 0; i < this.friendNamesList.size(); i++){
            if(this.friendNamesList.get(i).equals(oldName)){
                this.friendNamesList.set(i, newName);
                break;
            }
        }
    }

    void removeFriendByName(String name){
        if(this.friendIDsList.isEmpty()){
            return;
        }
        int index = this.friendNamesList.indexOf(name);
        if(index < 0){
            return;
        }
        this.friendIDsList.remove(index);
        this.friendNamesList.remove(index);
    }

    boolean addFriend(String friendsID, String friendsName){
        if(this.friendIDsList.size() > 20){
            return false;
        }
        this.friendIDsList.add(friendsID);
        this.friendNamesList.add(friendsName);
        return true;
    }

    boolean removeFriend(String friendsID){
        if(this.friendIDsList.isEmpty()){
            return false;
        }
        int index = this.friendIDsList.indexOf(friendsID);
        if(index < 0){
            return false;
        }
        this.friendIDsList.remove(index);
        this.friendNamesList.remove(index);
        return true;
    }

    Integer getMaxHealth() {
        return this.maxHealth;
    }

    void addMaxHealth(Integer maxHealth) {
        this.maxHealth += maxHealth;
    }

    Integer addHealth(Integer health) {
        int diff = this.maxHealth - this.health;
        int ret = this.health + health;
        if (health > diff) {
            this.health = this.maxHealth;
            return diff;
        } else {
            this.health += health;
            return health;
        }
    }

    void reset() {
        this.level = 10;
        double basicStatus = Math.pow(level, 1.8) * 10;
        this.roomReference = 0;
        this.maxSlotAmount = 2;
        this.actionCount = 0;
        this.experience = 0;
        Random rand = new Random();
        this.health = (int) (Math.pow(level + 4, 1.6) * 80);
        this.maxHealth = (int) (Math.pow(level + 4, 1.6) * 80);
        this.armor = (int) (Math.sqrt(Math.pow(0.95, level) * basicStatus) * rand.nextGaussian() + basicStatus);
        this.attack = (int) (Math.sqrt(Math.pow(0.95, level) * basicStatus) * rand.nextGaussian() + basicStatus);
        this.equipmentReferences[0] = -1;
        this.equipmentReferences[1] = -1;
        if (this.keys.isEmpty()){
            this.keys.add(2);
            this.keys.add(2);
        }
    }

    String getUserID() {
        return this.userID;
    }

    Integer getActionCount() {
        return this.actionCount;
    }

    void incrementActionCount() {
        this.actionCount++;
    }

    Integer getMaxSlotAmount() {
        return this.maxSlotAmount;
    }

    Integer getExperience() {
        return this.experience;
    }

    boolean addExperience(Integer experienceAdd) {
        this.experience += experienceAdd;
        int newLevel = (int) Math.pow(this.experience, 1 / 2.8) - 2;
        if (newLevel > this.level) {
            levelUp(newLevel);
            return true;
        }
        return false;
    }

    void addKey(Integer key) {
        this.keys.add(key);
        Collections.sort(this.keys);
    }

    Boolean useKey(Integer key) {
        int use = key;
        for (int i = 0; i < this.keys.size(); ++i) {
            int ikey = this.keys.get(i);
            if (ikey >= use) {
                this.keys.remove(i);
                return true;
            }
        }
        return false;
    }

    Integer unEquip(Integer slotNumber) {
        if (slotNumber >= 0 && slotNumber < maxSlotAmount) {
            int equipRef = this.equipmentReferences[slotNumber];
            this.equipmentReferences[slotNumber] = -1;
            return equipRef;
        }
        return -2;
    }

    Integer equip(Integer slotNumber, Integer equipmentReference) {
        if (slotNumber >= 0 && slotNumber < maxSlotAmount) {
            int ret = this.equipmentReferences[slotNumber];
            this.equipmentReferences[slotNumber] = equipmentReference;
            return ret;
        }
        return -2;
    }

    List<Integer> getKeys() {
        return keys;
    }

    Integer getRoomReference() {
        return roomReference;
    }

    void setRoomReference(Integer roomReference) {
        this.roomReference = roomReference;
    }

    String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }


    void levelUp(Integer newLevel) {

        this.level = newLevel;
        int level = this.level;
        this.maxHealth = (int) (Math.pow(level + 4, 1.6) * 80);
        Random rand = new Random();
        double basicStatus = Math.pow(level, 1.8) * 10;
        this.armor = (int) (Math.sqrt(Math.pow(0.95, level) * basicStatus) * rand.nextGaussian() + basicStatus);
        this.attack = (int) (Math.sqrt(Math.pow(0.95, level) * basicStatus) * rand.nextGaussian() + basicStatus);

    }

    Integer getLevel() {
        return this.level;
    }

    void setLevel(Integer level) {
        this.level = level;
    }

    Integer getHealth() {
        return this.health;
    }

    void setHealth(Integer health) {
        this.health = health;
    }

    Integer getArmor() {
        return this.armor;
    }

    void setArmor(Integer armor) {
        this.armor = armor;
    }

    Integer getAttack() {
        return this.attack;
    }

    void setAttack(Integer attack) {
        this.attack = attack;
    }

    Integer[] getEquipmentReferences() {
        return this.equipmentReferences;
    }

}
