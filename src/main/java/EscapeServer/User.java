package EscapeServer;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.resource.spi.work.WorkEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class User {
    private MyWebSocket myWebSocket;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoDBEquipments;
    private MongoCollection<Document> mongoDBDoors;
    private MongoCollection<Document> mongoDBRooms;
    private MongoCollection<Document> mongoDBMonsters;
    private MongoCollection<Document> mongoDBChests;
    private MongoCollection<Document> mongoDBPlayers;
    private MongoCollection<Document> mongoDBBackpacks;
    private MongoCollection<Document> mongoDBScore;
    private MongoCollection<Document> mongoDBPublicChatHistory;
    private MongoCollection<Document> mongoDBHScore;

    Player player = null;
    private Room currentRoom = null;
    private Backpack backpack = null;
    private Integer score;
    private Integer hscore;

    private String[] hScoreListPlayerNames;
    private Integer[] hScoreList;

    User(MyWebSocket myWebSocket) {
        this.myWebSocket = myWebSocket;
        this.hScoreListPlayerNames = new String[10];
        this.hScoreList = new Integer[10];
        try {
            // connect to mongoDB service
            this.mongoClient = new MongoClient("159.65.33.86", 27017);
            // Connect to database
            this.mongoDatabase = this.mongoClient.getDatabase("GetOutWorld");

            this.mongoDBPlayers = this.mongoDatabase.getCollection("Players");
            this.mongoDBRooms = this.mongoDatabase.getCollection("Rooms");
            this.mongoDBEquipments = this.mongoDatabase.getCollection("Equipments");
            this.mongoDBDoors = this.mongoDatabase.getCollection("Doors");
            this.mongoDBMonsters = this.mongoDatabase.getCollection("Monsters");
            this.mongoDBChests = this.mongoDatabase.getCollection("Chests");
            this.mongoDBBackpacks = this.mongoDatabase.getCollection("Backpacks");
            this.mongoDBScore = this.mongoDatabase.getCollection("Score");
            this.mongoDBHScore = this.mongoDatabase.getCollection("HighestScoreList");
            if (MyWebSocket.chatHistory == null) {
                dBGetChatHistory();
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        dBGetHScore();
    }

    void dBGetChatHistory() {
        this.mongoDBPublicChatHistory = this.mongoDatabase.getCollection("PublicChatHistory");
        BasicDBObject queryObject = new BasicDBObject("reference", 0);
        FindIterable<Document> hScoreObj = this.mongoDBPublicChatHistory.find(queryObject);
        MongoCursor<Document> hScoreCursor = hScoreObj.iterator();
        Document historyDoc = hScoreCursor.next();
        MyWebSocket.chatHistory = new ArrayList<>((List<String>) historyDoc.get("history", List.class));
    }

    void dBUpdateChatHistory() {
        BasicDBObject updateDocument = new BasicDBObject("$set", new BasicDBObject("reference", 0)
                .append("history", MyWebSocket.chatHistory));
        mongoDBPublicChatHistory.updateOne(new BasicDBObject("reference", 0), updateDocument);
    }

    @SuppressWarnings("Duplicates")
    void dBUpdateHScore() {
        Document doc = new Document("reference", 0);
        doc.append("s0", this.hScoreList[0]);
        doc.append("s1", this.hScoreList[1]);
        doc.append("s2", this.hScoreList[2]);
        doc.append("s3", this.hScoreList[3]);
        doc.append("s4", this.hScoreList[4]);
        doc.append("s5", this.hScoreList[5]);
        doc.append("s6", this.hScoreList[6]);
        doc.append("s7", this.hScoreList[7]);
        doc.append("s8", this.hScoreList[8]);
        doc.append("s9", this.hScoreList[9]);
        doc.append("n0", this.hScoreListPlayerNames[0]);
        doc.append("n1", this.hScoreListPlayerNames[1]);
        doc.append("n2", this.hScoreListPlayerNames[2]);
        doc.append("n3", this.hScoreListPlayerNames[3]);
        doc.append("n4", this.hScoreListPlayerNames[4]);
        doc.append("n5", this.hScoreListPlayerNames[5]);
        doc.append("n6", this.hScoreListPlayerNames[6]);
        doc.append("n7", this.hScoreListPlayerNames[7]);
        doc.append("n8", this.hScoreListPlayerNames[8]);
        doc.append("n9", this.hScoreListPlayerNames[9]);
        this.mongoDBHScore.replaceOne(new BasicDBObject("reference", 0), doc);
    }

    private void dBGetHScore() {
        BasicDBObject queryObject = new BasicDBObject("reference", 0);
        FindIterable<Document> hScoreObj = this.mongoDBHScore.find(queryObject);
        MongoCursor<Document> hScoreCursor = hScoreObj.iterator();
        Document hScoreDoc = hScoreCursor.next();
        this.hScoreList[0] = hScoreDoc.getInteger("s0");
        this.hScoreList[1] = hScoreDoc.getInteger("s1");
        this.hScoreList[2] = hScoreDoc.getInteger("s2");
        this.hScoreList[3] = hScoreDoc.getInteger("s3");
        this.hScoreList[4] = hScoreDoc.getInteger("s4");
        this.hScoreList[5] = hScoreDoc.getInteger("s5");
        this.hScoreList[6] = hScoreDoc.getInteger("s6");
        this.hScoreList[7] = hScoreDoc.getInteger("s7");
        this.hScoreList[8] = hScoreDoc.getInteger("s8");
        this.hScoreList[9] = hScoreDoc.getInteger("s9");
        this.hScoreListPlayerNames[0] = hScoreDoc.getString("n0");
        this.hScoreListPlayerNames[1] = hScoreDoc.getString("n1");
        this.hScoreListPlayerNames[2] = hScoreDoc.getString("n2");
        this.hScoreListPlayerNames[3] = hScoreDoc.getString("n3");
        this.hScoreListPlayerNames[4] = hScoreDoc.getString("n4");
        this.hScoreListPlayerNames[5] = hScoreDoc.getString("n5");
        this.hScoreListPlayerNames[6] = hScoreDoc.getString("n6");
        this.hScoreListPlayerNames[7] = hScoreDoc.getString("n7");
        this.hScoreListPlayerNames[8] = hScoreDoc.getString("n8");
        this.hScoreListPlayerNames[9] = hScoreDoc.getString("n9");
    }

    Backpack addPlayerandBackpack(Player player) {
        World.dBAddPlayer(this.mongoDBPlayers, player);
        Backpack ret = new Backpack(player.getUserID(), 4);
        World.dBAddBackpack(this.mongoDBBackpacks, ret);
        return ret;
    }

    @SuppressWarnings("Duplicates")
    void processMessage(String message) throws IOException {
        String[] commandList = message.split("#");
        switch (Integer.parseInt(commandList[0])) {
            case 1: {
                String userID = commandList[1];
                String name = commandList[2];
                boolean success = true;
                if (World.dBGetPlayer(mongoDBPlayers, userID) == null) {
                    success = playerSignUp(userID, name);
                    System.out.println("Someone just signed up with userID: " + userID);
                }
                success = playerLogIn(userID) && success;
                System.out.println("Someone just logged in with userID: " + userID);
                myWebSocket.sendMessage(constructResponse(success ? 1 : 2, null));
                respondLeaderBoard(10);
                respondPlayerStatus();
                break;
            }
            case 2: {
                respondPlayerStatus();
                break;
            }
            case 3: {
                int amount = Integer.parseInt(commandList[1]);
                if (amount < 11 && amount > 0) {
                    respondLeaderBoard(amount);
                }
                break;
            }
            case 4: {
                Integer number = Integer.parseInt(commandList[1]);
                List<String> ret = new ArrayList<String>();
                Integer j = number > MyWebSocket.chatHistory.size() ? MyWebSocket.chatHistory.size() : number;
                ret.add(j.toString());
                for (int i = 0; i < number && i < MyWebSocket.chatHistory.size(); i++) {
                    ret.add(MyWebSocket.chatHistory.get(i));
                }
                myWebSocket.sendMessage(constructResponse(29, ret));
                break;
            }
            case 5: {
                String msg = message.substring(6);
                MyWebSocket.chatHistory.add(this.player.getName() + ": " + msg);
                MyWebSocket.newChatAmount++;
                if (MyWebSocket.newChatAmount > 20) {
                    MyWebSocket.newChatAmount = 0;
                    dBUpdateChatHistory();
                }
                myWebSocket.sendAllMessage("00031#" + this.player.getName() + ": " + msg);
                break;
            }
            case 6: {
                List<String> parameters = new ArrayList<>();
                parameters.add("S");
                parameters.add(Integer.toString(MyWebSocket.onlineUsernames.size()));
                parameters.addAll(MyWebSocket.onlineUsernames);
                myWebSocket.sendMessage(constructResponse(4, parameters));
                break;
            }
            case 7: {
                break;
            }
            case 8: {
                List<String> friendNames = new ArrayList<>();
                List<String> friends = this.player.getFriendNamesList();
                for(int i = 0; i < friends.size(); i++){
                    if(!friends.get(i).startsWith("OUT#") && !friends.get(i).startsWith("IN#")){
                        friendNames.add(friends.get(i));
                    }
                }
                myWebSocket.sendMessage(constructResponse(6, friendNames));
                break;
            }
            case 9: {
                String username = commandList[1];
                if (this.player.getFriendNamesList().contains(username)) {
                    myWebSocket.sendMessage("00041#E");
                    break;
                }
                Player tmpPlayer = World.dBGetPlayerByName(this.mongoDBPlayers, username);
                if (tmpPlayer != null) {
                    this.player.addFriend(tmpPlayer.getUserID(), "OUT#" + username);
                    World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                    tmpPlayer.addFriend(this.player.getUserID(), "IN#" + this.player.getName());
                    World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);
                    Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                    Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                    String name;
                    MyWebSocket socket;
                    while (nameList.hasNext()) {
                        name = nameList.next();
                        socket = socketList.next();
                        System.out.println(name + "+" + tmpPlayer.getName());
                        if (name.equals(tmpPlayer.getName())) {
                            System.out.println("Found");
                            socket.sendMessage("00040#" + this.player.getName());
                            socket.clientMessage("00039#" + this.player.getUserID() + "#" + this.player.getName());
                            break;
                        }
                    }
                    myWebSocket.sendMessage("00041#S");
                } else {
                    myWebSocket.sendMessage("00041#F");
                }
                break;
            }
            case 10: {
                String username = commandList[1];
                if(!this.player.getFriendNamesList().contains(username)){
                    myWebSocket.sendMessage("00016#-1");
                }
                respondPlayerStatus(username);
                break;
            }
            case 11: {
                String username = commandList[1];
                String msg = message.substring(7 + commandList[1].length());
                if(!this.player.getFriendNamesList().contains(username)){
                    myWebSocket.sendMessage("00035#F");
                    break;
                }
                Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                String name;
                MyWebSocket socket;
                Boolean unfind = true;
                while (nameList.hasNext()) {
                    name = nameList.next();
                    socket = socketList.next();
                    if (name.equals(username)) {
                        myWebSocket.sendMessage("00035#S");
                        socket.sendMessage("00027#" + this.player.getName() + "#" + msg);
                        unfind = false;
                        break;
                    }
                }
                if (unfind) myWebSocket.sendMessage("00035#F");
                break;
            }
            case 12: {
                playerWake();
                List<String> parameters = new ArrayList<>();
                parameters.add(this.player.getName());
                myWebSocket.sendAllMessage(constructResponse(38, parameters));
                break;
            }
            case 13: {
                playerSleep();
                List<String> parameters = new ArrayList<>();
                parameters.add(this.player.getName());
                myWebSocket.sendAllMessage(constructResponse(39, parameters));
                break;
            }
            case 14: {
                int direction = Integer.parseInt(commandList[1]);
                int doorReference = this.player.getRoomReference() * 6 + direction;
                Door door = World.dBGetDoor(this.mongoDBDoors, doorReference);
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(direction));
                parameters.add(door.getSound() ? "T" : "F");
                myWebSocket.sendMessage(constructResponse(19, parameters));
                break;
            }
            case 15: {
                int direction = Integer.parseInt(commandList[1]);
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(direction));
                int result = playerMove(direction);
                parameters.add(Integer.toString(result));//-1:locked, 0, no monster, 1, win, 2,lost
                if(result == 2){
                    respawnPlayer();
                }else {
                    World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                }
                myWebSocket.sendMessage(constructResponse(24, parameters));
                break;
            }
            case 16: {
                break;
            }
            case 17: {
                int result = openCrate();//-1 no key -2 no chest
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(result));
                myWebSocket.sendMessage(constructResponse(23, parameters));
                break;
            }
            case 18: {
                break;
            }
            case 19: {
                break;
            }
            case 20: {
                break;
            }
            case 21: {
                List<String> parameters = new ArrayList<>();
                List<Integer> tmpList = this.currentRoom.getGroundEquipList();
                parameters.add(Integer.toString(tmpList.size()));
                for (int i = 0; i < tmpList.size(); i++) {
                    parameters.add(Integer.toString(tmpList.get(i)));
                }
                myWebSocket.sendMessage(constructResponse(37, parameters));
                break;
            }
            case 22: {
                int equipmentReference = Integer.parseInt(commandList[1]);
                int result = pickUpFromGround(equipmentReference);//
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(result)); //-1: cant find the eq from the ground -2:backpack is full
                myWebSocket.sendMessage(constructResponse(8, parameters));
                break;
            }
            case 23: {
                int slot = Integer.parseInt(commandList[1]);
                int equipmentReference = Integer.parseInt(commandList[2]);
                int result = equipFromBackpack(slot, equipmentReference);
                //-1: cant find item in backpack, -2; slot number is wrong, -3, level is not enough
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(result));
                myWebSocket.sendMessage(constructResponse(11, parameters));
                break;
            }
            case 24: {
                break;
            }
            case 25: {
                int equipmentReference = Integer.parseInt(commandList[1]);
                int result = dropFromBackpack(equipmentReference);
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(result));
                myWebSocket.sendMessage(constructResponse(13, parameters));
                break;
            }
            case 26: {
                int slot = Integer.parseInt(commandList[1]);
                int result = dropFromSlots(slot);
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(result));
                myWebSocket.sendMessage(constructResponse(12, parameters));
                break;
            }
            case 27: {
                int equipmentReference = Integer.parseInt(commandList[1]);
                Equipment equipment = World.dBGetEquipment(this.mongoDBEquipments, equipmentReference);
                List<String> parameters = new ArrayList<>();
                parameters.add(equipment.getUsage().toString());
                parameters.add(equipment.getLevel().toString());
                parameters.add(equipment.getStatus().toString());
                myWebSocket.sendMessage(constructResponse(20, parameters));
                break;
            }
            case 28: {

                break;
            }
            case 29: {
                int amount = this.backpack.getEquipmentList().size();
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(this.backpack.getSlotAmount()));
                parameters.add(Integer.toString(amount));
                for (int i = 0; i < amount; i++) {
                    parameters.add(Integer.toString(this.backpack.getEquipmentList().get(i)));
                }
                myWebSocket.sendMessage(constructResponse(15, parameters));
                break;
            }
            case 30: {
                int regenAmount = rest();
                List<String> parameters = new ArrayList<>();
                parameters.add(Integer.toString(regenAmount));
                myWebSocket.sendMessage(constructResponse(7, parameters));
                break;
            }
            case 31: {
                break;
            }
            case 32: {

                break;
            }
            case 33: {
                respawnPlayer();
                respondPlayerStatus();
                myWebSocket.sendMessage(constructResponse(10, null));
                break;
            }
            case 34: {
                String newName = commandList[1];
                if (World.dBGetPlayerByName(this.mongoDBPlayers, newName) != null) {
                    myWebSocket.sendMessage("00026#F");
                }
                Player tmpPlayer;
                for (int i = 0; i < this.player.getFriendIDsList().size(); i++) {
                    tmpPlayer = World.dBGetPlayer(this.mongoDBPlayers, this.player.getFriendIDsList().get(i));
                    if (tmpPlayer != null) {
                        tmpPlayer.setNewNameForFriendByID(this.player.getUserID(), newName);
                        World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);
                    }

                }

                Iterator<String> nameList;
                Iterator<MyWebSocket> socketList;
                String name, fname;
                MyWebSocket socket;
                for (int i = 0; i < this.player.getFriendNamesList().size(); i++) {
                    nameList = MyWebSocket.onlineUsernames.iterator();
                    socketList = MyWebSocket.myWebSocketSet.iterator();
                    fname = this.player.getFriendNamesList().get(i);
                    while (nameList.hasNext()) {
                        name = nameList.next();
                        socket = socketList.next();
                        if (name.equals(fname)) {
                            socket.sendMessage("00044#" + this.player.getName() + "#" + newName);
                            socket.clientMessage("00038#" + this.player.getName() + "#" + newName);
                            break;
                        }
                    }
                }

                ArrayList<String> tmpList = new ArrayList<>(MyWebSocket.onlineUsernames);

                for (int i = 0; i < tmpList.size(); i++) {
                    if (tmpList.get(i).equals(this.player.getName())) {
                        tmpList.set(i, newName);
                    }
                }

                MyWebSocket.onlineUsernames.clear();
                MyWebSocket.onlineUsernames.addAll(tmpList);

                this.player.setName(newName);
                World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                respondPlayerStatus();
                myWebSocket.sendMessage("00026#S");
                break;
            }
            case 35: {
                String username = commandList[1];
                Player tmpPlayer = World.dBGetPlayerByName(this.mongoDBPlayers, username);
                if (tmpPlayer != null) {
                    this.player.setNewNameForFriend(("IN#" + username), username);
                    World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                    tmpPlayer.setNewNameForFriend(("OUT#" + this.player.getName()), this.player.getName());
                    World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);
                    Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                    Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                    String name;
                    MyWebSocket socket;
                    while (nameList.hasNext()) {
                        name = nameList.next();
                        socket = socketList.next();
                        if (name.equals(tmpPlayer.getName())) {
                            socket.sendMessage("00042#" + this.player.getName() + "#S");
                            socket.clientMessage("00040#" + this.player.getName());
                            break;
                        }
                    }
                    myWebSocket.sendMessage("00043#S#" + username);
                } else {
                    myWebSocket.sendMessage("00043#F");
                }
                break;
            }

            case 36: {
                String username = commandList[1];
                Player tmpPlayer = World.dBGetPlayerByName(this.mongoDBPlayers, username);
                if (tmpPlayer != null) {
                    this.player.removeFriendByName("IN#" + username);
                    World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                    tmpPlayer.setNewNameForFriend("OUT#" + this.player.getName(), "F#" + this.player.getName());
                    Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                    Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                    String name;
                    MyWebSocket socket;
                    while (nameList.hasNext()) {
                        name = nameList.next();
                        socket = socketList.next();
                        if (name.equals(tmpPlayer.getName())) {
                            socket.sendMessage("00042#" + this.player.getName() + "#F");
                            socket.clientMessage("00037#" + this.player.getName());
                            tmpPlayer.removeFriendByName("F#" + this.player.getName());
                            World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);
                            break;
                        }
                    }
                    World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);
                    myWebSocket.sendMessage("00043#S");
                } else {
                    myWebSocket.sendMessage("00043#F");
                }
                break;
            }

            case 37: {
                String username = commandList[1];
                this.player.removeFriendByName("OUT#" + username);
                break;
            }

            case 38: {
                String oldname = commandList[1];
                String newname = commandList[2];
                this.player.setNewNameForFriend(oldname, newname);
                break;
            }

            case 39: {
                String id = commandList[1];
                String newname = commandList[2];
                this.player.addFriend(id, "IN#" + newname);
                break;
            }

            case 40: {
                String name = commandList[1];
                this.player.setNewNameForFriend("OUT#" + name, name);
                break;
            }

            case 41: {
                String name = commandList[1];
                if (!this.player.getFriendNamesList().contains(name)) {
                    myWebSocket.sendMessage("00045#F");
                    break;
                }
                this.player.removeFriendByName(name);
                World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
                Player tmpPlayer = World.dBGetPlayerByName(this.mongoDBPlayers, name);
                tmpPlayer.removeFriendByName(this.player.getName());
                World.dBUpdatePlayer(this.mongoDBPlayers, tmpPlayer);

                Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                String nname;
                MyWebSocket socket;
                while (nameList.hasNext()) {
                    nname = nameList.next();
                    socket = socketList.next();
                    if (nname.equals(tmpPlayer.getName())) {
                        socket.clientMessage("00042#" + this.player.getName());
                        socket.sendMessage("00048#" + this.player.getName());

                        break;
                    }
                }
                myWebSocket.sendMessage("00045#S#" + name);
                break;
            }

            case 42: {
                String username = commandList[1];
                this.player.removeFriendByName(username);
                break;
            }

            case 43:{
                //46+number+list
                String username = commandList[1];
                if(!this.player.getFriendNamesList().contains(username)){
                    myWebSocket.sendMessage("00047#F");
                    break;
                }
                Iterator<String> nameList = MyWebSocket.onlineUsernames.iterator();
                Iterator<MyWebSocket> socketList = MyWebSocket.myWebSocketSet.iterator();
                String name;
                MyWebSocket socket;
                Boolean unfind = true;
                while (nameList.hasNext()) {
                    name = nameList.next();
                    socket = socketList.next();
                    if (name.equals(username)) {
                        myWebSocket.sendMessage("00047#S");
                        List<String> tmpList = new ArrayList<>();
                        Integer amount = this.backpack.getEquipmentList().size();
                        tmpList.add(amount.toString());
                        for(int i = 0; i < amount; i++) {
                            tmpList.add(this.backpack.getEquipmentList().get(i).toString());
                        }
                        socket.sendMessage(constructResponse(46, tmpList));
                        unfind = false;
                        break;
                    }
                }
                if (unfind) myWebSocket.sendMessage("00047#F");
                break;
            }

            case 99997:{
                int roomReference = Integer.parseInt(commandList[1]);
                int newMonsterReference = Integer.parseInt(commandList[2]);
                Room room = World.dBGetRoom(this.mongoDBRooms, roomReference);
                room.regenerateMonster(newMonsterReference);
                World.dBUpdateRoom(this.mongoDBRooms, room);
                System.out.println("Admin asked to regenerate the monster succeed.");
                break;
            }

            case 99998:{
                String username = commandList[1];
                Player player = World.dBGetPlayerByName(this.mongoDBPlayers, username);
                player.setRoomReference(0);
                player.setHealth(player.getMaxHealth());
                World.dBUpdatePlayer(this.mongoDBPlayers, player);
                System.out.println("Admin asked to re-spawn player succeed.");
                break;
            }

            case 99999: {
                int ringAmount = Integer.parseInt(commandList[1]);
                long seed = Integer.parseInt(commandList[2]);
                System.out.println("Admin asked to regenerate the world.");
                new World(seed, ringAmount);
                System.out.println("Regeneration process finished.");
                myWebSocket.sendMessage(constructResponse(99999, null));

                break;
            }
            default: {
                break;
            }
        }
    }

    private String constructResponse(Integer index, List<String> parameters) {
        String ret = "";
        index += 100000;
        ret += index.toString().substring(1);
        if (parameters != null) for (int i = 0; i < parameters.size(); i++) {
            ret += "#";
            ret += parameters.get(i);
        }
        return ret;
    }

    private boolean playerSignUp(String userID, String name) {
        //String name, String userID, Integer actionCount, Integer experience, Integer level, Integer health, Integer armor, Integer attack, Integer backPackReference
        this.player = new Player(name, userID, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        this.player.reset();
        this.backpack = addPlayerandBackpack(this.player);
        return true;
    }

    private boolean playerLogIn(String userID) {
        this.player = World.dBGetPlayer(this.mongoDBPlayers, userID);
        this.backpack = World.dBGetBackpack(this.mongoDBBackpacks, userID);
        this.currentRoom = World.dBGetRoom(this.mongoDBRooms, this.player.getRoomReference());
        this.score = player.getScore();
        this.hscore = player.getHighScore();
        return true;
    }

    private void lockRoom() {
        this.currentRoom.setLocked(true);
        World.dBUpdateRoom(this.mongoDBRooms, this.currentRoom);

    }

    private void unlockRoom() {
        this.currentRoom.setLocked(false);
        World.dBUpdateRoom(this.mongoDBRooms, this.currentRoom);
    }

    private void playerWake() {
        MyWebSocket.myWebSocketSet.add(myWebSocket);
        MyWebSocket.onlineUsernames.add(this.player.getName());
        lockRoom();
    }

    private void playerSleep() {
        World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
        World.dBUpdateBackpack(this.mongoDBBackpacks, this.backpack);
        dBUpdateHScore();
        MyWebSocket.myWebSocketSet.remove(myWebSocket);
        MyWebSocket.onlineUsernames.remove(this.player.getName());
        unlockRoom();
    }

    private void respawnPlayer() throws IOException {
        unlockRoom();
        this.player.reset();
        this.backpack.reset();
        this.score = 0;
        World.dBUpdatePlayer(this.mongoDBPlayers, this.player);
        respondPlayerStatus();
        World.dBUpdateBackpack(this.mongoDBBackpacks, this.backpack);
    }

    private void respondPlayerStatus() throws IOException {
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(this.player.getName());
        parameters.add(this.player.getRoomReference().toString());
        parameters.add(this.currentRoom.getLocation()[0].toString());
        parameters.add(this.currentRoom.getLocation()[1].toString());
        parameters.add(this.player.getExperience().toString());
        parameters.add(this.player.getLevel().toString());
        parameters.add(this.player.getHealth().toString());
        parameters.add(this.player.getMaxHealth().toString());
        parameters.add(this.player.getArmor().toString());
        parameters.add(this.player.getAttack().toString());
        parameters.add(this.player.getEquipmentReferences()[0].toString());
        parameters.add(this.player.getEquipmentReferences()[1].toString());
        parameters.add(this.player.getScore().toString());
        parameters.add(this.player.getHighScore().toString());
        String response = constructResponse(3, parameters);
        myWebSocket.sendMessage(response);
    }

    private void respondPlayerStatus(String username) throws IOException {
        Player fplayer = World.dBGetPlayerByName(this.mongoDBPlayers, username);
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(fplayer.getName());
        parameters.add(fplayer.getRoomReference().toString());
        Room room = World.dBGetRoom(this.mongoDBRooms, fplayer.getRoomReference());
        parameters.add(room.getLocation()[0].toString());
        parameters.add(room.getLocation()[1].toString());
        parameters.add(fplayer.getExperience().toString());
        parameters.add(fplayer.getLevel().toString());
        parameters.add(fplayer.getHealth().toString());
        parameters.add(fplayer.getMaxHealth().toString());
        parameters.add(fplayer.getArmor().toString());
        parameters.add(fplayer.getAttack().toString());
        parameters.add(fplayer.getEquipmentReferences()[0].toString());
        parameters.add(fplayer.getEquipmentReferences()[1].toString());
        parameters.add(fplayer.getScore().toString());
        parameters.add(fplayer.getHighScore().toString());
        String response = constructResponse(3, parameters);
        myWebSocket.sendMessage(response);
    }

    private void respondLeaderBoard(Integer number) throws IOException {
        if (number > 10) {
            System.err.println("Wrong parameter for the leaderboard request!");
            return;
        }
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(number.toString());
        for (int i = 0; i < number; i++) {
            parameters.add(this.hScoreListPlayerNames[i]);
            parameters.add(this.hScoreList[i].toString());
        }
        String response = constructResponse(30, parameters);
        myWebSocket.sendMessage(response);
    }

    private Integer playerMove(Integer direction) throws IOException {
        Integer roomReference = this.player.getRoomReference();
        Integer doorReference = roomReference * 6 + direction;
        Integer targetRoomReference = World.dBGetDoor(this.mongoDBDoors, doorReference).getRoomReference();
        Room targetRoom = World.dBGetRoom(this.mongoDBRooms, targetRoomReference);
        if (targetRoom.getLocked()) {
            return -1;
        }
        unlockRoom();
        World.dBUpdateRoom(this.mongoDBRooms, this.currentRoom);
        this.player.setRoomReference(targetRoomReference);
        this.currentRoom = targetRoom;
        changeScore();
        BasicDBObject newDocument =
                new BasicDBObject().append("$inc",
                        new BasicDBObject().append("openCount", 1));
        this.mongoDBDoors.updateOne(new BasicDBObject("reference", doorReference), newDocument);
        this.mongoDBDoors.updateOne(new BasicDBObject("reference", targetRoomReference * 6 + (direction + 3) % 6), newDocument);
        if (targetRoom.getMonster()) {
            boolean result = fightMonster();
            //targetRoom.regenerateMonster();
            World.dBUpdateRoom(this.mongoDBRooms, targetRoom);
            return result ? 1 : 2;
        }
        lockRoom();
        return 0;
    }

    private boolean fightMonster() throws IOException {
        changeScore();
        Room room = World.dBGetRoom(this.mongoDBRooms, this.player.getRoomReference());
        Monster monster = World.dBGetMonster(this.mongoDBMonsters, room.getObject());
        String monsterStatus = "99998#<br>";
        monsterStatus += "The monster has the following status:";
        monsterStatus += "<br>  Level: " + monster.getLevel();
        monsterStatus += "<br>  Health: " + monster.getHealth();
        monsterStatus += "<br>  Attack: " + monster.getAttack();
        monsterStatus += "<br>  Armor: " + monster.getArmor();
        myWebSocket.sendMessage(monsterStatus);
        double mhealth = monster.getHealth();
        double mhealthtemp = mhealth;
        double mattack = monster.getAttack();
        double marmor = monster.getArmor();
        double phealth = this.player.getHealth();
        double phealthtemp = phealth;
        double pattack = this.player.getAttack();
        double parmor = this.player.getArmor();
        int ref1 = this.player.getEquipmentReferences()[0];
        int ref2 = this.player.getEquipmentReferences()[1];
        int critical = 0;
        if (ref1 != -1) {
            Equipment equip1 = World.dBGetEquipment(this.mongoDBEquipments, ref1);
            if (equip1.getUsage() == 0) {
                pattack += equip1.getStatus();
                critical += equip1.getStatus() / 2;
            } else {
                parmor += equip1.getStatus();
            }
        }
        if (ref2 != -1) {
            Equipment equip2 = World.dBGetEquipment(this.mongoDBEquipments, ref2);
            if (equip2.getUsage() == 0) {
                pattack += equip2.getStatus();
                critical += equip2.getStatus() / 2;
            } else {
                parmor += equip2.getStatus();
            }
        }
        String playerStatus = "99998#<br>";
        playerStatus += "You have the following status:";
        playerStatus += "<br>  Level: " + this.player.getLevel();
        playerStatus += "<br>  Health: " + phealth;
        playerStatus += "<br>  Attack: " + pattack;
        playerStatus += "<br>  Armor: " + parmor;
        myWebSocket.sendMessage(playerStatus);
        int index = 0;
        String fightProcedure = "";
        Random rand = new Random();
        while (mhealthtemp > 0 && phealthtemp > 0) {
            index++;
            int prob = rand.nextInt(10);
            double mhurt = pattack * (1.0 - (marmor / mhealth));
            if (prob < 5) {
                //mhurt = pattack * (1 + (double) (prob + 1) / 10.0) - marmor * 0.7;
                mhurt += critical;
                fightProcedure += "==Critical!==";
            }
            //double phurt = mattack - parmor * 0.7;
            double phurt = mattack * (1.0 - (parmor / phealth));
            if (mhurt < 0) {
                mhurt = 0;
            }
            if (phurt < 0) {
                phurt = 0;
            }
            fightProcedure = "99998#";
            fightProcedure += "  Monster hurt: " + mhurt;
            fightProcedure += "<br>  Player hurt: " + phurt;
            fightProcedure += "<br>=====================";
            myWebSocket.sendMessage(fightProcedure);
            mhealthtemp -= mhurt;
            phealthtemp -= phurt;
            if (index > 700) {
                break;
            }
        }
        if (mhealthtemp <= 0) {
            this.player.setHealth((int) phealthtemp);
            double adjExp = (monster.getAttack() + monster.getArmor() - pattack - parmor) / (double) pattack;
            int pLevel = this.player.getLevel();
            if (adjExp <= -1) {
                adjExp = -0.9;
            }
            if (monster.getEquipmentReferences()[0] != -1) {
                this.currentRoom.addGroundEquipment(monster.getEquipmentReferences()[0]);
            }
            if (monster.getEquipmentReferences()[1] != -1) {
                this.currentRoom.addGroundEquipment(monster.getEquipmentReferences()[1]);
            }
            //            怪物难度                  升级所需经验                            一个怪物的比重
            double exp = (1 + adjExp) * (Math.pow((pLevel + 4), 2.8) - Math.pow((pLevel + 3), 2.8)) / 4.0;
            String fightWin = "99998#<br>";
            fightWin += "  You successfully killed the monster! Congratulations!";
            fightWin += "<br>  The monster may dropped some equipments on the ground, you may check it after.";
            fightWin += "<br>  This fight grant you " + ((Integer) (int) exp).toString() + " experience. ";
            if (this.player.addExperience((int) exp)) {
                fightWin += "<br>Level up! Your new level is [" + this.player.getLevel() + "]! You are now stronger than ever before!";
            }
            fightWin += "<br>  You now have " + this.player.getExperience() + " total experience.";
            fightWin += "<br>  After this fight, you have " + this.player.getHealth() + " health points left. Take some rest and recover some health!";
            int prob = rand.nextInt(10);
            if (prob < 4) {
                fightWin += "<br>The died monster dropped a lvl.[" + monster.getLevel() + "] key and you picked it up!";
                this.player.addKey(monster.getLevel());
            } else if (prob < 7) {
                fightWin += "<br>The died monster dropped a lvl.[" + (monster.getLevel() + 1) + "] key and you picked it up!";
                this.player.addKey(monster.getLevel() + 1);
            } else if (prob < 8) {
                fightWin += "<br>The died monster dropped a lvl.[" + (monster.getLevel() + 2) + "] key and you picked it up!";
                this.player.addKey(monster.getLevel() + 2);
            }
            myWebSocket.sendMessage(fightWin);
            return true;
        }
        String fightLose = "99998#<br>";
        fightLose += "  You were killed by the monster... There's no chance for come back.";
        fightLose += "<br> Be more prepared next time. One more life faded.";
        fightLose += "<br> You will be reborn with nothing but only keys you have, but your story is forever remembered.";
        fightLose += "Before you died, monster left " + mhealthtemp + " of health.";
        myWebSocket.sendMessage(fightLose);
        respawnPlayer();
        return false;
    }

    private void changeScore() throws IOException {
        this.player.incrementActionCount();
        this.score = (this.currentRoom.getLevel() * 20 - this.player.getActionCount());
        if (this.score > this.hscore) {
            this.hscore = this.score;
            updateHScoreList();
        }
    }

    private void updateHScoreList() throws IOException {
        for (int i = 0; i < 10; ++i) {
            if (this.hscore > this.hScoreList[i]) {
                for (int j = 9; j > i; --j) {
                    this.hScoreList[j] = this.hScoreList[j - 1];
                    this.hScoreListPlayerNames[j] = this.hScoreListPlayerNames[j - 1];
                }
                this.hScoreList[i] = this.hscore;
                this.hScoreListPlayerNames[i] = this.player.getName();
                dBUpdateHScore();
                respondLeaderBoard(10);
                String ret = "99999#<br> Player " + this.player.getName() + " get a new high score! [" + this.hscore + "] Congrats!!!";
                myWebSocket.sendAllMessage(ret);
                i = 10;
            }
        }
    }

    private int pickUpFromGround(Integer equipmentReference) {
        List<Integer> tmpList = this.currentRoom.getGroundEquipList();
        if (!tmpList.contains(equipmentReference)) {
            return -1;
        }
        if (this.backpack.addEquipment(equipmentReference)) {
            tmpList.remove(equipmentReference);
            return equipmentReference;
        }
        return -2;
    }

    private int equipFromBackpack(int slot, Integer equipmentReference) {
        if (!this.backpack.getEquipmentList().contains(equipmentReference)) {
            return -1;
        }
        if (slot < 0 || slot >= this.player.getMaxSlotAmount()) {
            return -2;
        }
        Equipment equipment = World.dBGetEquipment(this.mongoDBEquipments, equipmentReference);
        if (equipment.getLevel() > this.player.getLevel()) {
            return -3;
        }
        int drop = this.player.equip(slot, equipmentReference);
        if (drop >= 0) {
            this.backpack.getEquipmentList().remove(equipmentReference);
            this.backpack.addEquipment(drop);
        }
        return equipmentReference;
    }

    private int dropFromBackpack(Integer equipmentReference) {
        if (!this.backpack.getEquipmentList().contains(equipmentReference)) {
            return -1;
        }
        this.backpack.getEquipmentList().remove(equipmentReference);
        this.currentRoom.getGroundEquipList().add(equipmentReference);
        return equipmentReference;
    }

    private int dropFromSlots(int slot) {
        if (slot < 0 || slot >= this.player.getMaxSlotAmount()) {
            return -2;
        }
        int equipmentReference = this.player.unEquip(slot);//-1 if its already empty
        this.currentRoom.getGroundEquipList().add(equipmentReference);
        return equipmentReference;
    }

    private Integer openCrate() throws IOException {
        if (this.currentRoom.getMonster()) return -2;
        Integer crateReference = this.currentRoom.getObject();
        Chest chest = World.dBGetChest(this.mongoDBChests, crateReference);
        Integer chestLevel = chest.getLevel();
        Boolean keyUsed = this.player.useKey(chestLevel);
        if (keyUsed) {
            int equipmentReference = chest.getEquipmentReference();
            this.currentRoom.getGroundEquipList().add(equipmentReference);
            World.dBUpdateRoom(this.mongoDBRooms, this.currentRoom);
            chest.regenerateEquipment();
            World.dBUpdateChest(this.mongoDBChests, chest);
            changeScore();
            return equipmentReference;
        }
        return -1;
    }

    private int rest() {
        int regen = -1;
        if (this.player.getHealth() < this.player.getMaxHealth()) {
            this.player.incrementActionCount();
            regen = this.player.addHealth(this.player.getMaxHealth() / 5);
        }
        return regen;
    }
}
