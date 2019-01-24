package com.onlinevotingsystem.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.onlinevotingsystem.client.model.BasicUser;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;

@RemoteServiceRelativePath("basicservice")
public interface StandardService extends RemoteService {
	
	void clearDBUsers();
	
	void clearDBLists();
	
	void clearDBElections();
	
	void clearDBVotes();
	
	BasicUser login(String nickname, String password);
	
	boolean registerList(String electionSubject, String creatorNickname, List list);
	
	boolean registerElection(Election election);
	
	boolean registerUser(User user);
	
	boolean registerVote(Vote vote, String voter);
	
	boolean editUser(User user);
	
	boolean editElection(Election election);
	
	boolean editList(List list);
	
	ArrayList<User> getUnlistedUsersInElection(String electionSubject);
	
	ArrayList<User> getUsers();
	
	BasicUser getUser(String nickname);
	
	String getUsersAsString();
	
	ArrayList<Election> getElections();
	
	Election getElection(String subject);
	
	String getElectionsAsString();
	
	ArrayList<List> getLists();
	
	List getList(String id);
	
	String getListsAsString();
	
	String getVotesAsString();
	
	ArrayList<Election> getActiveElections();
	
	ArrayList<List> getUserCreatedLists(User profile);
	
	ArrayList<Election> getUpcomingElections();
	
	ArrayList<Election> getUnvotedElections(User profile);
	
	ArrayList<List> getPendingLists();
	
	ArrayList<List> getElectionLists(String electionSubject);
	
	ArrayList<String> getResults();
}
