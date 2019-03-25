package EscapeServer;

import java.util.ArrayList;
import java.util.List;

class Backpack {

    private String userID;
    private Integer slotAmount;
    private List<Integer> equipmentList;

    Backpack(String userID, Integer slotAmount) {
        this.userID = userID;
        this.slotAmount = slotAmount;
        this.equipmentList = new ArrayList<Integer>();
    }

    Integer removeEquipment(int index){
        Integer ret = this.equipmentList.get(index);
        this.equipmentList.remove(index);
        return ret;
    }

    Boolean addEquipment(Integer equipmentReference){
        if(this.equipmentList.size() < this.slotAmount){
            this.equipmentList.add(equipmentReference);
            return true;
        }else return false;
    }
    Boolean checkNotFull(){
        if(this.equipmentList.size() < this.slotAmount) return true;
        return false;
    }

    String getUserID() {
        return this.userID;
    }

    void reset(){
        this.slotAmount = 4;
        this.equipmentList.clear();
    }

    Integer getSlotAmount() {
        return this.slotAmount;
    }

    List<Integer> getEquipmentList() {
        return this.equipmentList;
    }

}
