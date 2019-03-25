package EscapeServer;

import java.util.ArrayList;
import java.util.List;
/*Class which stores all the information regarding backpacks*/
class Backpack {

    private String userID;
    private Integer slotAmount;
    private List<Integer> equipmentList;

    Backpack(String userID, Integer slotAmount) {
        this.userID = userID;
        this.slotAmount = slotAmount;
        this.equipmentList = new ArrayList<Integer>();
    }
    //function to remove equipment from the backpack
    Integer removeEquipment(int index){
        Integer ret = this.equipmentList.get(index);
        this.equipmentList.remove(index);
        return ret;
    }
//function to add equipment from the backpack
    Boolean addEquipment(Integer equipmentReference){
        if(this.equipmentList.size() < this.slotAmount){
            this.equipmentList.add(equipmentReference);
            return true;
        }else return false;
    }
    
    //function to check if the backpack is full
    Boolean checkNotFull(){
        if(this.equipmentList.size() < this.slotAmount) return true;
        return false;
    }

    String getUserID() {
        return this.userID;
    }
    
    // backpack is reset to zero items
    void reset(){
        this.slotAmount = 4;
        this.equipmentList.clear();
    }

    Integer getSlotAmount() {
        return this.slotAmount;
    }
    //generates the list of equipment
    List<Integer> getEquipmentList() {
        return this.equipmentList;
    }

}
