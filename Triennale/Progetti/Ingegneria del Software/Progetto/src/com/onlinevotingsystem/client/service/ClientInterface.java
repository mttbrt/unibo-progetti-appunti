package com.onlinevotingsystem.client.service;

import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;

public interface ClientInterface {
	
	void clearDBUsers();
	
	void clearDBLists();
	
	void clearDBElections();
	
	void clearDBVotes();
	
	void login(String nickname, String password);
	
	void registerList(String electionSubject, String creatorNickname, List list);
	
	void registerElection(Election election);
	
	void registerUser(User user);
	
	void registerVote(Vote vote, String voter);
	
	void editUser(User user);
	
	void editElection(Election election);
	
	void editList(List list);
	
	void getUnlistedUsersInElection(String electionSubject);
	
	void getUsers();
	
	void getUser(String nickname);
	
	void getUsersAsString();
	
	void getElections();
	
	void getElection(String subject);
	
	void getElectionsAsString();
	
	void getLists();
	
	void getList(String id);
	
	void getListsAsString();
	
	void getVotesAsString();
	
	void getActiveElections();
	
	void getPendingLists();
	
	void getUserCreatedLists(User profile);
	
	void getUpcomingElections();
	
	void getUnvotedElections(User profile);
	
	void getElectionLists(String electionSubject);
	
	void getResults();
	
}
