package EscapeServer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.Arrays;
import java.util.Random;

class World {
    static Long seed;
    private Integer layerAmount;
    Random rand;

    //The data of following lists should be saved in the database.
    private List<Equipment> dBEquipments;
    private List<Room> dBRooms;

    //mongoDB
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> mongoDBEquipments;
    private MongoCollection<Document> mongoDBDoors;
    private MongoCollection<Document> mongoDBMonsters;
    private MongoCollection<Document> mongoDBChests;
    private MongoCollection<Document> mongoDBPlayers;
    private MongoCollection<Document> mongoDBBackpacks;
    private MongoCollection<Document> mongoDBRooms;
    private MongoCollection<Document> mongoDBHScore;
    private MongoCollection<Document> mongoDBPublicChatHistory;

    World(Long seed, Integer layerAmount) {
        World.seed = seed;
        this.layerAmount = layerAmount;
        this.rand = new Random(seed);
        this.dBRooms = new ArrayList<Room>();
        this.dBEquipments = new ArrayList<Equipment>();

        try {
            // connect to mongoDB service
            this.mongoClient = new MongoClient("159.65.33.86", 27017);
            // Connect to database
            this.mongoDatabase = this.mongoClient.getDatabase("GetOutWorld");

            this.mongoDBPlayers = this.mongoDatabase.getCollection("Players");
            this.mongoDBPlayers.drop();


            this.mongoDBRooms = this.mongoDatabase.getCollection("Rooms");
            this.mongoDBRooms.drop();
            this.mongoDBRooms = this.mongoDatabase.getCollection("Rooms");

            this.mongoDBEquipments = this.mongoDatabase.getCollection("Equipments");
            this.mongoDBEquipments.drop();
            this.mongoDBEquipments = this.mongoDatabase.getCollection("Equipments");

            this.mongoDBDoors = this.mongoDatabase.getCollection("Doors");
            this.mongoDBDoors.drop();
            this.mongoDBDoors = this.mongoDatabase.getCollection("Doors");

            this.mongoDBMonsters = this.mongoDatabase.getCollection("Monsters");
            this.mongoDBMonsters.drop();
            this.mongoDBMonsters = this.mongoDatabase.getCollection("Monsters");

            this.mongoDBChests = this.mongoDatabase.getCollection("Chests");
            this.mongoDBChests.drop();
            this.mongoDBChests = this.mongoDatabase.getCollection("Chests");

            this.mongoDBBackpacks = this.mongoDatabase.getCollection("Backpacks");
            this.mongoDBBackpacks.drop();

            this.mongoDBHScore = this.mongoDatabase.getCollection("HighestScoreList");
            this.mongoDBHScore.drop();
            this.mongoDBHScore = this.mongoDatabase.getCollection("HighestScoreList");

            this.mongoDBPublicChatHistory = this.mongoDatabase.getCollection("PublicChatHistory");
            this.mongoDBPublicChatHistory.drop();
            this.mongoDBPublicChatHistory = this.mongoDatabase.getCollection("PublicChatHistory");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        equipmentsGeneration();
        monstersGeneration();
        chestsGeneration();
        worldGeneration();
        storeRooms();
        storeEquipments();
        storeHighScoreTable(this.mongoDBHScore);
        storeChatHistory(this.mongoDBPublicChatHistory);
    }


    static Door dBGetDoor(MongoCollection<Document> mongoDBDoors, Integer doorReference) {
        /*
        private Integer reference;
        private Integer roomReference;
        private Boolean sound;
        private Integer openCount;
        */
        BasicDBObject queryObject = new BasicDBObject("reference", doorReference);
        FindIterable<Document> doorObj = mongoDBDoors.find(queryObject);
        MongoCursor<Document> doorCursor = doorObj.iterator();
        Document doorDoc = doorCursor.next();
        Door ret = new Door(
                doorDoc.getInteger("reference"),
                doorDoc.getInteger("roomReference"),
                doorDoc.getBoolean("sound"),
                doorDoc.getInteger("openCount")
        );
        return ret;
    }


    static Chest dBGetChest(MongoCollection<Document> mongoDBChests, Integer chestReference) {
        BasicDBObject queryObject = new BasicDBObject("reference", chestReference);
        FindIterable<Document> chestObj = mongoDBChests.find(queryObject);
        MongoCursor<Document> chestCursor = chestObj.iterator();
        Document chestDoc = chestCursor.next();
        Chest ret = new Chest(
                chestDoc.getInteger("reference"),
                chestDoc.getInteger("level"),
                chestDoc.getInteger("equipmentReference")
        );
        return ret;
    }


    static Equipment dBGetEquipment(MongoCollection<Document> mongoDBEquipments, Integer equipmentReference) {
        //System.out.println("Equipref: " + equipmentReference);
        BasicDBObject queryObject = new BasicDBObject("reference", equipmentReference);
        FindIterable<Document> equipmentObj = mongoDBEquipments.find(queryObject);
        MongoCursor<Document> equipmentCursor = equipmentObj.iterator();
        Document equipmentDoc = equipmentCursor.next();
        Equipment ret = new Equipment(
                equipmentDoc.getInteger("reference"),
                equipmentDoc.getInteger("usage"),
                equipmentDoc.getInteger("status"),
                equipmentDoc.getInteger("level")
        );
        return ret;
    }


    static Room dBGetRoom(MongoCollection<Document> mongoDBRooms, Integer roomReference) {
        BasicDBObject queryObject = new BasicDBObject("reference", roomReference);
        FindIterable<Document> roomObj = mongoDBRooms.find(queryObject);
        MongoCursor<Document> roomCursor = roomObj.iterator();
        Document roomDoc = roomCursor.next();
        Room ret = new Room(
                roomDoc.getInteger("reference"),
                new Integer[2],
                roomDoc.getInteger("object"),
                roomDoc.getBoolean("isMonster"),
                roomDoc.getString("userID"),
                roomDoc.getBoolean("locked"),
                roomDoc.getInteger("level")
        );
        List<Integer> location = (List<Integer>) roomDoc.get("location", List.class);
        List<Integer> groundList = (List<Integer>) roomDoc.get("groundEquipList", List.class);
        //System.out.println(location);
        ret.setGroundEquipList(groundList);
        ret.getLocation()[0] = location.get(0);
        ret.getLocation()[1] = location.get(1);
        return ret;
    }


    static void dBAddDoor(MongoCollection<Document> mongoDBDoors, Door door) {
        /*
        private Integer reference;
        private Integer roomReference;
        private Boolean sound;
        private Integer openCount;
        */
        Document doc = new Document("reference", door.getReference());
        doc.append("roomReference", door.getRoomReference());
        doc.append("sound", door.getSound());
        doc.append("openCount", door.getOpenCount());
        mongoDBDoors.insertOne(doc);
    }

    static void storeChatHistory(MongoCollection<Document> mongoDBPublicChatHistory) {
        Document doc = new Document("reference", 0);
        List<String> history = new ArrayList<>();
        doc.append("history", history);
        mongoDBPublicChatHistory.insertOne(doc);
    }

    static void storeHighScoreTable(MongoCollection<Document> mongoDBHScore) {
        Document doc = new Document("reference", 0);
        doc.append("s0", 0);
        doc.append("s1", 0);
        doc.append("s2", 0);
        doc.append("s3", 0);
        doc.append("s4", 0);
        doc.append("s5", 0);
        doc.append("s6", 0);
        doc.append("s7", 0);
        doc.append("s8", 0);
        doc.append("s9", 0);
        doc.append("n0", "Empty");
        doc.append("n1", "Empty");
        doc.append("n2", "Empty");
        doc.append("n3", "Empty");
        doc.append("n4", "Empty");
        doc.append("n5", "Empty");
        doc.append("n6", "Empty");
        doc.append("n7", "Empty");
        doc.append("n8", "Empty");
        doc.append("n9", "Empty");
        mongoDBHScore.insertOne(doc);
    }

    static void dBAddChest(MongoCollection<Document> mongoDBChests, Chest chest) {
        /*
        private Integer reference;
        private Integer level;
        private Integer equipmentReference;
         */
        Document doc = new Document("reference", chest.getReference());
        doc.append("level", chest.getLevel());
        doc.append("equipmentReference", chest.getEquipmentReference());
        mongoDBChests.insertOne(doc);
    }

    static void dBAddEquipment(MongoCollection<Document> mongoDBEquipments, Equipment equipment) {
        /*
        private Integer reference;
        private Integer usage;
        private Integer status;
        private Integer level;
         */
        Document doc = new Document("reference", equipment.getReference());
        doc.append("usage", equipment.getUsage());
        doc.append("status", equipment.getStatus());
        doc.append("level", equipment.getLevel());
        mongoDBEquipments.insertOne(doc);
    }


    static void dBAddMonster(MongoCollection<Document> mongoDBMonsters, Monster monster) {

        Document doc = new Document("reference", monster.getReference());
        doc.append("reference", monster.getReference());
        doc.append("level", monster.getLevel());
        doc.append("health", monster.getHealth());
        doc.append("armor", monster.getArmor());
        doc.append("attack", monster.getAttack());
        doc.append("equipmentReferences", Arrays.asList(monster.getEquipmentReferences()));
        mongoDBMonsters.insertOne(doc);

    }

    static Monster dBGetMonster(MongoCollection<Document> mongoDBMonsters, Integer monsterReference) {
        BasicDBObject queryObject = new BasicDBObject("reference", monsterReference);
        FindIterable<Document> monsterObj = mongoDBMonsters.find(queryObject);
        MongoCursor<Document> monsterCursor = monsterObj.iterator();
        if (!monsterCursor.hasNext()) {
            return null;
        }
        Document monsterDoc = monsterCursor.next();
        Monster ret = new Monster(
                monsterDoc.getInteger("reference"),
                monsterDoc.getInteger("level"),
                monsterDoc.getInteger("health"),
                monsterDoc.getInteger("armor"),
                monsterDoc.getInteger("attack")
        );
        List<Integer> equipmentSlot = (List<Integer>) monsterDoc.get("equipmentReferences");
        //System.out.println(equipmentSlot);
        ret.getEquipmentReferences()[0] = equipmentSlot.get(0);
        ret.getEquipmentReferences()[1] = equipmentSlot.get(1);
        return ret;
    }
    static void dBAddRoom(MongoCollection<Document> mongoDBRooms, Room room) {

        Document doc = new Document("reference", room.getReference());
        doc.append("location", Arrays.asList(room.getLocation()));
        doc.append("object", room.getObject());
        doc.append("isMonster", room.getMonster());
        doc.append("userID", room.getUserID());
        doc.append("locked", room.getLocked());
        doc.append("level", room.getLevel());
        doc.append("groundEquipList", room.getGroundEquipList());
        mongoDBRooms.insertOne(doc);
    }


    //Print [amount] rooms, start from [start].
    @SuppressWarnings("Duplicates")
    void getRoomsInformation(Integer start, Integer end) {
        Room tempRoom;
        for (int i = start; i < start + end; ++i) {
            tempRoom = dBGetRoom(mongoDBRooms, i);
            tempRoom.printInformation();
            System.out.println("This room has 6 doors to: " +
                    dBGetDoor(mongoDBRooms, 6 * i).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i).getSound() ? "S" : "") + ", " +
                    dBGetDoor(mongoDBRooms, 6 * i + 1).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i + 1).getSound() ? "S" : "") + ", " +
                    dBGetDoor(mongoDBRooms, 6 * i + 2).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i + 2).getSound() ? "S" : "") + ", " +
                    dBGetDoor(mongoDBRooms, 6 * i + 3).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i + 3).getSound() ? "S" : "") + ", " +
                    dBGetDoor(mongoDBRooms, 6 * i + 4).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i + 4).getSound() ? "S" : "") + ", " +
                    dBGetDoor(mongoDBRooms, 6 * i + 5).getRoomReference() + (dBGetDoor(mongoDBRooms, 6 * i + 5).getSound() ? "S" : ""));
            Equipment equipment = dBGetEquipment(mongoDBEquipments, dBGetChest(mongoDBChests, tempRoom.getObject()).getEquipmentReference());
            System.out.println("This room has a " + (tempRoom.getMonster() ? "Monster" : "Chest which contains " + (equipment.getUsage() == 0 ? "a weapon" : "a shield") + " with " + equipment.getStatus() + (equipment.getUsage() == 0 ? " damage" : " defense")) + "." + "\n");
        }
    }


    private void storeRooms() {
        for (int i = 0; i < this.dBRooms.size(); ++i) {
            dBAddRoom(mongoDBRooms, this.dBRooms.get(i));
        }
    }

    private void storeEquipments() {
        for (int i = 0; i < this.dBEquipments.size(); ++i) {
            dBAddEquipment(mongoDBEquipments, this.dBEquipments.get(i));
        }
    }

    //Generate equipments in the world
    private void equipmentsGeneration() {
        this.dBEquipments.add(new Equipment(0, 0, 20, 0));
        int ref = 1;
        for (int i = 1; i < this.layerAmount; ++i) {
            double basicStatus = Math.pow((i + 1), 1.8) * 10;
            for (int j = 0; j < i * 6 * 3; ++j) {
                int eqStatus = (int) (Math.sqrt(Math.pow(0.95, i) * basicStatus) * this.rand.nextGaussian() + basicStatus);
                while (eqStatus < 0) {
                    eqStatus = (int) (Math.sqrt(Math.pow(0.95, i) * basicStatus) * this.rand.nextGaussian() + basicStatus);
                }
                int prob = this.rand.nextInt(5);
                if (prob < 3) {
                    prob = 0;
                } else {
                    prob = 1;
                }
                this.dBEquipments.add(new Equipment(ref, prob, eqStatus, i));
                ref++;
            }
        }
        System.out.println("# of equip: " + ref);
    }

    //Generate chests in the world
    private void chestsGeneration() {
        dBAddChest(mongoDBChests, new Chest(0, 0, 0));
        int ref = 1;
        for (int i = 1; i < this.layerAmount; ++i) {
            for (int j = 0; j < i * 6 * 3; ++j) {
                //Integer reference, Integer level, Integer equipmentReference
                dBAddChest(mongoDBChests, new Chest(ref, i, ref));
                ref++;
            }
        }
    }

    //Generate monsters in the world
    private void monstersGeneration() {
        int ref = 1;
        int upper;
        int minimum = 1;
        for (int i = 1; i < this.layerAmount; ++i) {
            double basicHealth = Math.pow((i + 5), 1.6) * 70;
            for (int j = 0; j < i * 6 * 3; ++j) {
                int monHealth = (int) (Math.sqrt(Math.pow(0.95, i) * basicHealth) * this.rand.nextGaussian() + basicHealth);
                upper = this.rand.nextInt(i * 6 * 3);
                int attack = 0;
                int armor = 0;
                int ref1 = upper + minimum;
                upper = this.rand.nextInt(i * 6 * 3);
                int ref2 = upper + minimum;
                //System.out.println("ref1: "+ref1+"; ref2: "+ ref2);
                int usage1 = this.dBEquipments.get(ref1).getUsage();
                int usage2 = this.dBEquipments.get(ref2).getUsage();
                //0=attack, 1=armor
                if (usage1 == 0) {
                    attack += this.dBEquipments.get(ref1).getStatus();
                } else {
                    armor += dBEquipments.get(ref1).getStatus();
                }
                if (usage2 == 0) {
                    attack += this.dBEquipments.get(ref2).getStatus();
                } else {
                    armor += this.dBEquipments.get(ref2).getStatus();
                }

                if (attack == 0) {
                    attack += (int) (Math.sqrt(Math.pow(0.95, i)) * this.rand.nextGaussian() + Math.pow(i, 1.8) * 5);
                }
                //decide how many equipment a monster take
                int prob = this.rand.nextInt(10);
                if (prob < 8) {
                    ref1 = -1;
                    if (prob < 2) {
                        ref2 = -1;
                    }
                }// 1/5 both, 3/5 only ref2, 1/5 none
                Monster monster = new Monster(ref, i, monHealth, armor, attack);
                //get range of ref of equipments
                Integer[] equip = new Integer[2];
                equip[0] = ref1;
                equip[1] = ref2;
                monster.setEquipmentReferences(equip);
                dBAddMonster(mongoDBMonsters, monster);
                ref++;
            }
            minimum += i * 6 * 3;
        }
    }

    //Generate rooms in the world
    private Integer roomsGeneration() {
        int ref = 0;
        Integer[] location = new Integer[2];
        Integer x = 0;
        Integer y = 0;
        location[0] = x;
        location[1] = y;
        Room tempRoom = new Room(ref, location, 0, false, "", false, 0);
        tempRoom.setObject(ref);
        this.dBRooms.add(tempRoom);
        int first = 0;
        int test;
        int minimum = 1;
        int prob = 0;
        for (int i = 1; i <= this.layerAmount; ++i) {
            y++;
            for (int j = 0; j < i * 6; ++j) {
                ref++;
                location = new Integer[2];
                location[0] = x;
                location[1] = y;
                prob = this.rand.nextInt(4);
                boolean isMonster;
                isMonster = prob != 0;
                prob = this.rand.nextInt(i * 6 * 3);
                tempRoom = new Room(ref, location, (prob + minimum), isMonster, "", false, i);
                this.dBRooms.add(tempRoom);
                //Set the location for next room
                if (j == 0) first = ref;
                test = (ref - first) / i;
                if (test == 0) {
                    x++;
                    y--;
                } else if (test == 1) {
                    y--;
                } else if (test == 2) {
                    x--;
                } else if (test == 3) {
                    x--;
                    y++;
                } else if (test == 4) {
                    y++;
                } else if (test == 5) {
                    x++;
                }
                //End of Set the location for next room
            }
            minimum += i * 6 * 3;
        }
        for (int i = 0; i < this.dBRooms.size(); ++i) {
            doorsGeneration(this.dBRooms.get(i));
        }
        return ref;
    }

    private void doorsGeneration(Room room) {
        //Integer reference, Integer roomReference, Boolean sound, Integer openCount
        Room tempRoom;
        int ref = room.getReference();
        for (int i = 0; i < this.dBRooms.size(); ++i) {
            tempRoom = this.dBRooms.get(i);
            if (tempRoom.getLocation()[0].equals(room.getLocation()[0]) && tempRoom.getLocation()[1].equals(room.getLocation()[1] + 1)) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6, tempRoom.getReference(), tempRoom.getMonster(), 0));
            } else if (tempRoom.getLocation()[0].equals(room.getLocation()[0] + 1) && tempRoom.getLocation()[1].equals(room.getLocation()[1])) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6 + 1, tempRoom.getReference(), tempRoom.getMonster(), 0));
            } else if (tempRoom.getLocation()[0].equals(room.getLocation()[0] + 1) && tempRoom.getLocation()[1].equals(room.getLocation()[1] - 1)) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6 + 2, tempRoom.getReference(), tempRoom.getMonster(), 0));
            } else if (tempRoom.getLocation()[0].equals(room.getLocation()[0]) && tempRoom.getLocation()[1].equals(room.getLocation()[1] - 1)) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6 + 3, tempRoom.getReference(), tempRoom.getMonster(), 0));
            } else if (tempRoom.getLocation()[0].equals(room.getLocation()[0] - 1) && tempRoom.getLocation()[1].equals(room.getLocation()[1])) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6 + 4, tempRoom.getReference(), tempRoom.getMonster(), 0));
            } else if (tempRoom.getLocation()[0].equals(room.getLocation()[0] - 1) && tempRoom.getLocation()[1].equals(room.getLocation()[1] + 1)) {
                dBAddDoor(mongoDBDoors, new Door(ref * 6 + 5, tempRoom.getReference(), tempRoom.getMonster(), 0));
            }
        }
    }

    private void worldGeneration() {
        roomsGeneration();
    }

    static void dBUpdateChest(MongoCollection<Document> mongoDBChests, Chest chest) {
        //private Integer equipmentReference;
        BasicDBObject updateDocument = new BasicDBObject("$set", new BasicDBObject("equipmentReference", chest.getEquipmentReference()));
        mongoDBChests.updateOne(new BasicDBObject("reference", chest.getReference()), updateDocument);
    }

    @SuppressWarnings("Duplicates")
    static void dBUpdateRoom(MongoCollection<Document> mongoDBRooms, Room room) {
        /*private Integer reference;
        private Integer[] location;
        private Integer object;
        private Boolean isMonster;
        private Integer player;
        private Boolean locked;
        private Integer level;*/
        BasicDBObject updateDocument = new BasicDBObject("$set", new BasicDBObject("object", room.getObject())
                .append("userID", room.getUserID())
                .append("locked", room.getLocked())
                .append("groundEquipList", room.getGroundEquipList()));
        mongoDBRooms.updateOne(new BasicDBObject("reference", room.getReference()), updateDocument);
    }

    @SuppressWarnings("Duplicates")
    static void dBUpdatePlayer(MongoCollection<Document> mongoDBPlayers, Player player) {
        BasicDBObject updateDocument = new BasicDBObject("$set", new BasicDBObject("roomReference", player.getRoomReference())
                .append("name", player.getName())
                .append("score", player.getScore())
                .append("highScore", player.getHighScore())
                .append("experience", player.getExperience())
                .append("level", player.getLevel())
                .append("maxHealth", player.getMaxHealth())
                .append("health", player.getHealth())
                .append("armor", player.getArmor())
                .append("attack", player.getAttack())
                .append("actionCount", player.getActionCount())
                .append("equipmentReferences", Arrays.asList(player.getEquipmentReferences()))
                .append("maxSlotAmount", player.getMaxSlotAmount())
                .append("keys", player.getKeys())
                .append("friendIDsList", player.getFriendIDsList())
                .append("friendNamesList", player.getFriendNamesList()));
        mongoDBPlayers.updateOne(new BasicDBObject("userID", player.getUserID()), updateDocument);
    }

    static void dBAddPlayer(MongoCollection<Document> mongoDBPlayers, Player player) {
        /*
        private Integer reference;
        private Integer roomReference;
        private String name;
        private String userID;
        private Integer experience;
        private Integer level;
        private Integer health;
        private Integer armor;
        private Integer attack;
        private Integer actionCount;
        private Integer[] equipmentReferences;
        private Integer maxSlotAmount;
        private Integer backPackReference;
        private List<Integer> keys;
         */
        player.addKey(2);
        player.addKey(2);
        Document doc = new Document("roomReference", player.getRoomReference());
        doc.append("name", player.getName());
        doc.append("userID", player.getUserID());
        doc.append("score", player.getScore());
        doc.append("highScore", player.getHighScore());
        doc.append("experience", player.getExperience());
        doc.append("level", player.getLevel());
        doc.append("maxHealth", player.getMaxHealth());
        doc.append("health", player.getHealth());
        doc.append("armor", player.getArmor());
        doc.append("attack", player.getAttack());
        doc.append("actionCount", player.getActionCount());
        doc.append("equipmentReferences", Arrays.asList(player.getEquipmentReferences()));
        doc.append("maxSlotAmount", player.getMaxSlotAmount());
        doc.append("keys", player.getKeys());
        doc.append("friendIDsList", player.getFriendIDsList());
        doc.append("friendNamesList", player.getFriendNamesList());
        mongoDBPlayers.insertOne(doc);
        /*FindIterable<Document> findIterable = mongoDBPlayers.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while(mongoCursor.hasNext()){
            System.out.println(mongoCursor.next());
        }*/
    }

    static Player dBGetPlayer(MongoCollection<Document> mongoDBPlayers, String userID) {
        BasicDBObject queryObject = new BasicDBObject("userID", userID);
        FindIterable<Document> playerObj = mongoDBPlayers.find(queryObject);
        MongoCursor<Document> playerCursor = playerObj.iterator();
        if (!playerCursor.hasNext()) {
            return null;
        }
        Document playerDoc = playerCursor.next();
        Player ret = new Player(
                playerDoc.getString("name"),
                playerDoc.getString("userID"),
                playerDoc.getInteger("score"),
                playerDoc.getInteger("highScore"),
                playerDoc.getInteger("actionCount"),
                playerDoc.getInteger("experience"),
                playerDoc.getInteger("level"),
                playerDoc.getInteger("maxHealth"),
                playerDoc.getInteger("health"),
                playerDoc.getInteger("armor"),
                playerDoc.getInteger("attack")
        );
        ret.setRoomReference(playerDoc.getInteger("roomReference"));
        List<Integer> equipmentSlot = (List<Integer>) playerDoc.get("equipmentReferences", List.class);
        List<Integer> keys = (List<Integer>) playerDoc.get("keys", List.class);
        List<String> friendIDsList = (List<String>) playerDoc.get("friendIDsList", List.class);
        List<String> friendNamesList = (List<String>) playerDoc.get("friendNamesList", List.class);
        ret.getEquipmentReferences()[0] = equipmentSlot.get(0);
        ret.getEquipmentReferences()[1] = equipmentSlot.get(1);
        ret.setKeys(keys);
        ret.setFriendIDsList(friendIDsList);
        ret.setFriendNamesList(friendNamesList);
        return ret;
    }

    static Player dBGetPlayerByName(MongoCollection<Document> mongoDBPlayers, String name) {
        BasicDBObject queryObject = new BasicDBObject("name", name);
        FindIterable<Document> playerObj = mongoDBPlayers.find(queryObject);
        MongoCursor<Document> playerCursor = playerObj.iterator();
        if (!playerCursor.hasNext()) {
            return null;
        }
        Document playerDoc = playerCursor.next();
        Player ret = new Player(
                playerDoc.getString("name"),
                playerDoc.getString("userID"),
                playerDoc.getInteger("score"),
                playerDoc.getInteger("highScore"),
                playerDoc.getInteger("actionCount"),
                playerDoc.getInteger("experience"),
                playerDoc.getInteger("level"),
                playerDoc.getInteger("maxHealth"),
                playerDoc.getInteger("health"),
                playerDoc.getInteger("armor"),
                playerDoc.getInteger("attack")
        );
        ret.setRoomReference(playerDoc.getInteger("roomReference"));
        List<Integer> equipmentSlot = (List<Integer>) playerDoc.get("equipmentReferences", List.class);
        List<Integer> keys = (List<Integer>) playerDoc.get("keys", List.class);
        List<String> friendIDsList = (List<String>) playerDoc.get("friendIDsList", List.class);
        List<String> friendNamesList = (List<String>) playerDoc.get("friendNamesList", List.class);
        ret.getEquipmentReferences()[0] = equipmentSlot.get(0);
        ret.getEquipmentReferences()[1] = equipmentSlot.get(1);
        ret.setKeys(keys);
        ret.setFriendIDsList(friendIDsList);
        ret.setFriendNamesList(friendNamesList);
        return ret;
    }

    static Backpack dBGetBackpack(MongoCollection<Document> mongoDBBackpacks, String userID) {
        BasicDBObject queryObject = new BasicDBObject("userID", userID);
        FindIterable<Document> backpackObj = mongoDBBackpacks.find(queryObject);
        MongoCursor<Document> backpackCursor = backpackObj.iterator();
        if (!backpackCursor.hasNext()) {
            return null;
        }
        Document backpackDoc = backpackCursor.next();
        Backpack ret = new Backpack(
                backpackDoc.getString("userID"),
                backpackDoc.getInteger("slotAmount")
        );
        List<Integer> equipmentList = (List<Integer>) backpackDoc.get("equipmentList", List.class);
        for (int i = 0; i < equipmentList.size(); ++i) {
            ret.addEquipment(equipmentList.get(i));
        }
        return ret;
    }

    static void dBAddBackpack(MongoCollection<Document> mongoDBBackpacks, Backpack backpack) {
        /*
        private Integer reference;
        private Integer slotAmount;
        private List<Integer> equipmentList;
        */
        Document doc = new Document("userID", backpack.getUserID());
        doc.append("slotAmount", backpack.getSlotAmount());
        doc.append("equipmentList", backpack.getEquipmentList());
        mongoDBBackpacks.insertOne(doc);
    }
    @SuppressWarnings("Duplicates")
    static void dBUpdateBackpack(MongoCollection<Document> mongoDBBackpacks, Backpack backpack) {
        /*private Integer reference;
        private Integer slotAmount;
        private List<Integer> equipmentList;*/
        BasicDBObject updateDocument = new BasicDBObject("$set", new BasicDBObject("slotAmount", backpack.getSlotAmount())
                .append("equipmentList", backpack.getEquipmentList()));
        mongoDBBackpacks.updateOne(new BasicDBObject("userID", backpack.getUserID()), updateDocument);
    }

}
