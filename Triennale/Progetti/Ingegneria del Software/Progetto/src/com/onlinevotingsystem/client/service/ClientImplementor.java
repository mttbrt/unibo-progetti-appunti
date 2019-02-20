package com.onlinevotingsystem.client.service;

import java.util.ArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.onlinevotingsystem.client.gui.Main;
import com.onlinevotingsystem.client.model.BasicUser;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;
import com.onlinevotingsystem.client.service.StandardService;
import com.onlinevotingsystem.client.service.StandardServiceAsync;
import com.onlinevotingsystem.client.service.ClientInterface;

public class ClientImplementor implements ClientInterface {
	
	private StandardServiceAsync serviceAsync;
	private Main main;
	
	public ClientImplementor(String url) {
		serviceAsync = GWT.create(StandardService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) serviceAsync;
		endpoint.setServiceEntryPoint(url); // URL is the servlet url
				
		main = new Main(this);
	}
	
	public Main getMainGUI() {
		return main;
	}
	
	@Override
	public void clearDBUsers() {
		serviceAsync.clearDBUsers(new DefaultCallback());
	}
	
	@Override
	public void clearDBLists() {
		serviceAsync.clearDBLists(new DefaultCallback());
	}
	
	@Override
	public void clearDBElections() {
		serviceAsync.clearDBElections(new DefaultCallback());
	}
	
	@Override
	public void clearDBVotes() {
		serviceAsync.clearDBVotes(new DefaultCallback());
	}
	
	@Override
	public void login(String nickname, String password) {
		serviceAsync.login(nickname, password, new LoginCallback());
	}
	
	@Override
	public void registerList(String electionSubject, String creatorNickname, List list) {
		serviceAsync.registerList(electionSubject, creatorNickname, list, new SaveInDBCallback());
	}
	
	@Override
	public void registerElection(Election election) {
		serviceAsync.registerElection(election, new SaveInDBCallback());
	}
	
	@Override
	public void registerUser(User user) {
		serviceAsync.registerUser(user, new SaveInDBCallback());
	}
	
	@Override
	public void registerVote(Vote vote, String voter) {
		serviceAsync.registerVote(vote, voter, new SaveInDBCallback());
	}
	
	@Override
	public void editUser(User user) {
		serviceAsync.editUser(user, new EditCallback());
	}
	
	@Override
	public void editElection(Election election) {
		serviceAsync.editElection(election, new EditCallback());
	}
	
	@Override
	public void editList(List list) {
		serviceAsync.editList(list, new EditCallback());
	}

	@Override
	public void getUnlistedUsersInElection(String electionSubject) {
		serviceAsync.getUnlistedUsersInElection(electionSubject, new UsersCallback());
	}
	
	@Override
	public void getElectionLists(String electionSubject) {
		serviceAsync.getElectionLists(electionSubject, new ListsCallback());
	}
		
	@Override
	public void getPendingLists() {
		serviceAsync.getPendingLists(new ListsCallback());
	}
	
	@Override
	public void getUsers() {
		serviceAsync.getUsers(new UsersCallback());
	}
	
	@Override
	public void getUser(String nickname) {
		serviceAsync.getUser(nickname, new SingleUserCallback());
	}
	
	@Override
	public void getUsersAsString() {
		serviceAsync.getUsersAsString(new DefaultCallback());
	}
	
	@Override
	public void getElections() {
		serviceAsync.getElections(new ElectionsCallback());
	}
	
	@Override
	public void getElection(String subject) {
		serviceAsync.getElection(subject, new SingleElectionCallback());
	}
	
	@Override
	public void getElectionsAsString() {
		serviceAsync.getElectionsAsString(new DefaultCallback());
	}
	
	@Override
	public void getLists() {
		serviceAsync.getLists(new ListsCallback());
	}
	
	@Override
	public void getList(String id) {
		serviceAsync.getList(id, new SingleListCallback());
	}
	
	@Override
	public void getListsAsString() {
		serviceAsync.getListsAsString(new DefaultCallback());
	}
	
	@Override
	public void getVotesAsString() {
		serviceAsync.getVotesAsString(new DefaultCallback());
	}
	
	@Override
	public void getActiveElections() {
		serviceAsync.getActiveElections(new ElectionsCallback());
	}
	
	@Override
	public void getUserCreatedLists(User profile) {
		serviceAsync.getUserCreatedLists(profile, new ListsCallback());
	}
	
	@Override
	public void getUpcomingElections() {
		serviceAsync.getUpcomingElections(new ElectionsCallback());
	}
	
	@Override
	public void getUnvotedElections(User profile) {
		serviceAsync.getUnvotedElections(profile, new ElectionsCallback());
	}
	
	@Override
	public void getResults() {
		serviceAsync.getResults(new ResultsCallback());
	}
	
	private class DefaultCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof String)
				main.displayMsg(Main.NEUTRAL_CODE, (String) result);
			else if(result instanceof User)
				main.displayMsg(Main.NEUTRAL_CODE, ((User) result).toString());
			else if(result instanceof Election)
				main.displayMsg(Main.NEUTRAL_CODE, ((Election) result).toString());
		}
	}
	
	private class SaveInDBCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof Boolean)
				if((boolean) result)
					main.displayMsg(Main.SUCCESS_CODE, "Element successfully registered.");
				else
					main.displayMsg(Main.ERROR_CODE, "Element already exists.");
		}
	}
	
	private class LoginCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof BasicUser) {
				main.loginUser((BasicUser) result);
				main.displayMsg(Main.SUCCESS_CODE, "Successfully logged in.");
			} else
				main.displayMsg(Main.ERROR_CODE, "Username or password wrong.");
				
		}
	}
	
	private class EditCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof Boolean)
				if((boolean) result)
					main.displayMsg(Main.SUCCESS_CODE, "Element successfully edited.");
				else
					main.displayMsg(Main.ERROR_CODE, "Element does not exist.");
		}
	}
	
	private class UsersCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof ArrayList<?>)
				main.setUsersList((ArrayList<User>) result);
		}
	}
	
	private class ElectionsCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof ArrayList<?>)
				main.setElectionsList((ArrayList<Election>) result);
		}
	}
	
	private class ListsCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof ArrayList<?>)
				main.setListsList((ArrayList<List>) result);
		}
	}
	
	private class SingleUserCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof User)
				main.setSpecificUser((User) result);
		}
	}
	
	private class SingleElectionCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof Election)
				main.setSpecificElection((Election) result);
		}
	}
	
	private class SingleListCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof List)
				main.setSpecificList((List) result);
		}
	}
	
	private class ResultsCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			System.out.println("An error has occured.");
		}

		@Override
		public void onSuccess(Object result) {
			if(result instanceof ArrayList<?>)
				main.setResults((ArrayList<String>) result);
		}
	}
	
}
