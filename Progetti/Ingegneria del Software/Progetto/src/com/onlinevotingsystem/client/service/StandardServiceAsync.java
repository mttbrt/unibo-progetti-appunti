package com.onlinevotingsystem.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;

public interface StandardServiceAsync {
	
	void clearDBUsers(AsyncCallback callback);
	
	void clearDBElections(AsyncCallback callback);
	
	void clearDBLists(AsyncCallback callback);
	
	void clearDBVotes(AsyncCallback callback);
	
	void login(String nickname, String password, AsyncCallback callback);
	
	void registerList(String electionSubject, String creatorNickname, List list, AsyncCallback callback);
	
	void registerElection(Election election, AsyncCallback callback);
	
	void registerUser(User user, AsyncCallback callback);
	
	void registerVote(Vote vote, String voter, AsyncCallback callback);
	
	void editElection(Election election, AsyncCallback callback);
	
	void editUser(User user, AsyncCallback callback);
	
	void editList(List list, AsyncCallback callback);
		
	void getUnlistedUsersInElection(String electionSubject, AsyncCallback callback);
	
	void getUsers(AsyncCallback callback);
	
	void getUser(String nickname, AsyncCallback callback);
	
	void getUsersAsString(AsyncCallback callback);
	
	void getElections(AsyncCallback callback);
	
	void getElection(String subject, AsyncCallback callback);
	
	void getElectionsAsString(AsyncCallback callback);
	
	void getLists(AsyncCallback callback);
	
	void getList(String id, AsyncCallback callback);
	
	void getListsAsString(AsyncCallback callback);
	
	void getVotesAsString(AsyncCallback callback);
	
	void getActiveElections(AsyncCallback callback);
	
	void getUserCreatedLists(User profile, AsyncCallback callback);
	
	void getUpcomingElections(AsyncCallback callback);
	
	void getUnvotedElections(User profile, AsyncCallback callback);
	
	void getPendingLists(AsyncCallback callback);
	
	void getElectionLists(String electionSubject, AsyncCallback callback);
	
	void getResults(AsyncCallback callback);
	
}
