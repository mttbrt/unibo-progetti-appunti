package com.onlinevotingsystem.test;

import java.util.ArrayList;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;
import com.onlinevotingsystem.client.service.StandardService;
import com.onlinevotingsystem.client.service.StandardServiceAsync;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StandardServiceImplementorTest extends GWTTestCase {
	
	private StandardServiceAsync standardService;
	private ServiceDefTarget target;

	/**
	 * Setup del server
	 */
	private void setUpServer() {
		standardService = GWT.create(StandardService.class);
		target = (ServiceDefTarget) standardService;
		target.setServiceEntryPoint(GWT.getModuleBaseURL() + "onlinevotingsystem/standardservice");
		delayTestFinish(10000);
	}
	
	/**
	 * Test per la registrazione di un nuovo utente
	 */
	@Test
	public void test0RegisterUser() {
		setUpServer();
		
		// Clear all data
		standardService.clearDBUsers(new DefaultCallback());
		standardService.clearDBLists(new DefaultCallback());
		standardService.clearDBElections(new DefaultCallback());
		standardService.clearDBVotes(new DefaultCallback());
		
		User userTest = new User("test", "test", "test", "1", "testpswd", "test@a.a", "1111111111111111", "test", "ID card", "1", "test", new Date(), new Date());
		standardService.registerUser(userTest, new TrueCallback());
		standardService.registerUser(userTest, new FalseCallback());
		
		User user1 = new User("a", "a", "a", "1", "aaaaaa", "aa@aa.aa", "2222222222222222", "a", "ID card", "1", "a", new Date(), new Date());
		User user2 = new User("b", "b", "b", "1", "bbbbbb", "bb@bb.bb", "3333333333333333", "b", "ID card", "1", "b", new Date(), new Date());
		User user3 = new User("c", "c", "c", "1", "cccccc", "cc@cc.cc", "4444444444444444", "c", "ID card", "1", "c", new Date(), new Date());
		standardService.registerUser(user1, new TrueCallback());
		standardService.registerUser(user2, new TrueCallback());
		standardService.registerUser(user3, new TrueCallback());
	}

	/**
	 * Test per la lettura di un utente dal database
	 */
	@Test
	public void test1GetUser() {
		setUpServer();
		standardService.getUser("test", new UserCallback());
		standardService.getUser("Test", new NullCallback());
		standardService.getUser("", new NullCallback());
	}
	
	/**
	 * Test per il login di un utente
	 */
	@Test
	public void test2Login() {
		setUpServer();
		standardService.login("test", "testpswd", new UserCallback());
		standardService.login("test", "Testpswd", new NullCallback());
		standardService.login("", "", new NullCallback());
	}
		
	/**
	 * Test per la modifica di un utente
	 */
	@Test
	public void test3EditUser() {
		setUpServer();
		User existingUser = new User("test", "test", "test", "1", "testpswd", "test@a.a", "1111111111111111", "test", "ID card", "1", "test", new Date(), new Date());
		existingUser.setMunicipalOfficial(true);
		User nonExistingUser = new User("nobody", "nobody", "nobody", "1", "nobody", "nobody@a.a", "0000000000000000", "nobody", "ID card", "1", "nobody", new Date(), new Date());
		standardService.editUser(existingUser, new TrueCallback());
		standardService.editUser(nonExistingUser, new FalseCallback());
	}
		
	/**
	 * Test per la registrazione di una nuova elezione
	 */
	@Test
	public void test4RegisterElection() {
		setUpServer();
		Election electionTest = new Election("Registration Test", new Date(System.currentTimeMillis() - 24*60*60*1000), new Date(System.currentTimeMillis() + 24*60*60*1000));
		standardService.registerElection(electionTest, new TrueCallback());
		standardService.registerElection(electionTest, new FalseCallback());
	}
	
	/**
	 * Test per la lettura di un' elezione
	 */
	@Test
	public void test5GetElection() {
		setUpServer();
		standardService.getElection("Registration Test", new ElectionCallback());
		standardService.getElection("registration test", new NullCallback());
		standardService.getElection("", new NullCallback());
	}
	
	/**
	 * Test per la modifica di un'elezione
	 */
	@Test
	public void test6EditElection() {
		setUpServer();
		Election existingElection = new Election("Registration Test", new Date(System.currentTimeMillis() - 24*60*60*1000), new Date(System.currentTimeMillis() + 24*60*60*1000));
		Election notExistingElection = new Election("Not Exists", new Date(), new Date());
		standardService.editElection(existingElection, new TrueCallback());
		standardService.editElection(notExistingElection, new FalseCallback());
	}
	
	/**
	 * Test per la registrazione di una nuova lista
	 */
	@Test
	public void test7RegisterList() {
		setUpServer();
		User user1 = new User("a", "a", "a", "1", "aaaaaa", "aa@aa.aa", "2222222222222222", "a", "ID card", "1", "a", new Date(), new Date());
		User user2 = new User("b", "b", "b", "1", "bbbbbb", "bb@bb.bb", "3333333333333333", "b", "ID card", "1", "b", new Date(), new Date());
		User user3 = new User("c", "c", "c", "1", "cccccc", "cc@cc.cc", "4444444444444444", "c", "ID card", "1", "c", new Date(), new Date());
		
		ArrayList<User> users = new ArrayList<>();
		users.add(user1); users.add(user2); users.add(user3);
		List list = new List("listid", "Test", "symbol", user1, users);
		list.setMayor(user3);
		
		standardService.registerList("Registration Test", "a", list, new TrueCallback());
		standardService.registerList("Registration Test", "a", list, new FalseCallback());
	}
		
	/**
	 * Test per la modifica di una lista
	 */
	@Test
	public void test8EditList() {
		setUpServer();
		User user1 = new User("a", "a", "a", "1", "aaaaaa", "aa@aa.aa", "2222222222222222", "a", "ID card", "1", "a", new Date(), new Date());
		User user2 = new User("b", "b", "b", "1", "bbbbbb", "bb@bb.bb", "3333333333333333", "b", "ID card", "1", "b", new Date(), new Date());
		User user3 = new User("c", "c", "c", "1", "cccccc", "cc@cc.cc", "4444444444444444", "c", "ID card", "1", "c", new Date(), new Date());
		
		ArrayList<User> users = new ArrayList<>();
		users.add(user1); users.add(user2); users.add(user3);
		List existingList = new List("listid", "Test", "symbol", user1, users);
		existingList.setStatus(List.APPROVED_STATUS);
		List notExistingList = new List("NotExistTest", "symbol", user2, users);
		
		standardService.editList(existingList, new TrueCallback());
		standardService.editList(notExistingList, new FalseCallback());
	}
	
	/**
	 * Test per la registrazione di un voto
	 */
	@Test
	public void test9RegisterVote() {
		setUpServer();
		Vote vote = new Vote("Registration Test", "listid", "a");
		standardService.registerVote(vote, "test", new TrueCallback());
		standardService.registerVote(vote, "test", new FalseCallback());
	}

	@Override
	public String getModuleName() {
		return "com.onlinevotingsystem.OnlineVotingSystemJUnit";
	}

	
	/* Callback classes */
	
	private class DefaultCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) { }

		@Override
		public void onSuccess(Object result) { finishTest(); }
	}

	private class TrueCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			fail("Request failure: " + caught.getMessage());
		}

		@Override
		public void onSuccess(Object result) {
			assertTrue("Must be true", (boolean) result);
			finishTest();
		}
	}

	private class FalseCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			fail("Request failure: " + caught.getMessage());
		}

		@Override
		public void onSuccess(Object result) {
			assertFalse("Must be false", (boolean) result);
			finishTest();
		}
	}

	private class NullCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			fail("Request failure: " + caught.getMessage());
		}

		@Override
		public void onSuccess(Object result) {
			assertNull("Must be null", result);
			finishTest();
		}
	}

	private class UserCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			fail("Request failure: " + caught.getMessage());
		}

		@Override
		public void onSuccess(Object result) {
			assertTrue("Must be of type User", result instanceof User);
			finishTest();
		}
	}
	
	private class ElectionCallback implements AsyncCallback<Object> {
		@Override
		public void onFailure(Throwable caught) {
			fail("Request failure: " + caught.getMessage());
		}

		@Override
		public void onSuccess(Object result) {
			assertTrue("Must be of type Election", result instanceof Election);
			finishTest();
		}
	}
	
}
