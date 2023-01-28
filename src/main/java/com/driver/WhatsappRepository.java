package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, User> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new LinkedHashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    // create user
    public String createUser(String name, String mobile) throws Exception{
        if(!userMobile.containsKey(mobile)){
            User newUser = new User(name,mobile);
            userMobile.put(mobile,newUser);
            return "SUCCESS";
        }
        else {
            throw new Exception("User already exists");
        }
    }

    // Create a Group with users
    public Group createGroup(List<User> users){
        int size = users.size();
        User admin = users.get(0);
        if(size==2){
            String name = users.get(1).getName();
            Group newGroup = new Group(name,size);
            groupUserMap.put(newGroup,users);
            adminMap.put(newGroup,admin);
            return newGroup;
        }
        else{
            customGroupCount++;
            String name = "Group "+customGroupCount;
            Group newGroup = new Group(name,size);
            groupUserMap.put(newGroup,users);
            adminMap.put(newGroup,admin);
            return newGroup;
        }
    }
    // Create a Message
    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        messageId++;
        Message newMessage = new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!groupUserMap.get(group).contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        else{
            senderMap.put(message,sender);
            if(groupMessageMap.containsKey(group)){
                List<Message> messageList = groupMessageMap.get(group);
                messageList.add(message);
                groupMessageMap.put(group,messageList);
                return messageList.size();

            }
            else{
                List<Message> messageList = new ArrayList<Message>();
                messageList.add(message);
                groupMessageMap.put(group,messageList);
                return messageList.size();
            }

        }
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(! (adminMap.get(group) == approver)){
            throw new Exception("Approver does not have rights");
        }
        if(!groupUserMap.get(group).contains(user)){
            throw new Exception("User is not a participant");
        }
        else{
            adminMap.put(group,user);
            return "SUCCESS";
        }

    }

    public int removeUser(User user) throws Exception{
        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
        boolean flag = false;
        for(Map.Entry <Group,List<User> > grp: groupUserMap.entrySet()){
            if(grp.getValue().contains(user)){
                flag = true;
                if(adminMap.get(grp.getKey())==user){
                    throw new Exception("Cannot remove admin");
                }
                else{
                    grp.getValue().remove(grp.getValue().indexOf(user));
                    int userSize = grp.getValue().size();
                    grp.getKey().setNumberOfParticipants(userSize);
                    for(Map.Entry <Message, User> msg: senderMap.entrySet()){
                        if(msg.getValue() == user){
                            groupMessageMap.get(grp).remove(groupMessageMap.get(grp).indexOf(msg.getKey()));
                            senderMap.remove(msg.getKey());
                        }
                    }
                    int msgGrp = groupMessageMap.get(grp).size();
                    int allGrp = 0;
                    for(Map.Entry <Group, List<Message> > grpMes: groupMessageMap.entrySet()){
                        allGrp+=grpMes.getValue().size();
                    }
                    return userSize+msgGrp+allGrp;
                }
            }
        }
            throw new Exception("User not found");
    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception
        int k=0;
        for(Map.Entry <Message,User> msg : senderMap.entrySet()){
            Date curr = msg.getKey().getTimestamp();
            if( (curr.after(start) || curr.equals(start)) && (curr.before(end) || curr.equals(end))  ){
                k++;
                if(k==K){
                    return msg.getKey().getContent();
                }
            }
            if(curr.after(end)) break;
        }
        throw new Exception("K is greater than the number of messages");
    }

}
