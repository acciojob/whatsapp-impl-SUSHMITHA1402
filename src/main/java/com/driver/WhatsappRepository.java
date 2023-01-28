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
        this.senderMap = new HashMap<Message, User>();
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

}
