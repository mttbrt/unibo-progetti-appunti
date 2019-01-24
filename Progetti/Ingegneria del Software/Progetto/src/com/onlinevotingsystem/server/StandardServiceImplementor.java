package com.onlinevotingsystem.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.onlinevotingsystem.client.model.Admin;
import com.onlinevotingsystem.client.model.BasicUser;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Utils;
import com.onlinevotingsystem.client.model.Vote;
import com.onlinevotingsystem.client.service.StandardService;

@SuppressWarnings("serial")
public class StandardServiceImplementor extends RemoteServiceServlet implements StandardService {

	/**
	 * Elimina tutti gli utenti salvati nel database
	 */
	@Override
	public void clearDBUsers() {
		DB db = getDB();
		Map<Long, BasicUser> users = db.getTreeMap("users");
		users.clear();
		db.commit();
	}
	
	/**
	 * Elimina tutte le liste salvate nel database
	 */
	@Override
	public void clearDBLists() {
		DB db = getDB();
		Map<Long, List> lists = db.getTreeMap("lists");
		lists.clear();
		db.commit();
	}
	
	/**
	 * Elimina tutte le elezioni salvate nel database
	 */
	@Override
	public void clearDBElections() {
		DB db = getDB();
		Map<Long, Election> elections = db.getTreeMap("elections");
	    elections.clear();
		db.commit();
	}
	
	/**
	 * Elimina tutti i voti salvati nel database
	 */
	@Override
	public void clearDBVotes() {
		DB db = getDB();
		Map<Long, Election> votes = db.getTreeMap("votes");
		votes.clear();
		db.commit();
	}
	
	/**
	 * Effettua il login di un utente
	 * 
	 * @param nickname	nickname utente
	 * @param password	password per il login
	 * @return 			null o il BasicUser in caso di successo
	 */
	@Override
	public BasicUser login(String nickname, String password) {
		BasicUser user = getUser(nickname);
		if(user != null) {
			if(user.getPassword().equals(Utils.MD5(password)))
				return user;
			else
				return null;
		} else
			return null;
	}
	
	/**
	 * Registra una lista nel database
	 * 
	 * @param electionSubject	oggetto dell'elezione
	 * @param creatorNickname	nickname del creatore
	 * @param list				lista da inserire
	 * @return 					true in caso di successo nell'inserimento, false altrimenti
	 */
	@Override
	public boolean registerList(String electionSubject, String creatorNickname, List list) {
		if(getList(list.getId()) == null) {
			// Check if list with this name exists in this election
			ArrayList<List> listsInElection = getElectionLists(electionSubject);
			boolean exists = false;
			for(List singleList : listsInElection)
				if(singleList.getName().equals(list.getName())) {
					exists = true; 
					break;
				}
			
			if(!exists) {
				if(list.getId().equals(""))
					list.setId(UUID.randomUUID().toString()); // Deve essere fatto qui perchè nel client non è disponibile la classe UUID
				
				DB db = getDB();
				Map<Long, List> lists = db.getTreeMap("lists");
				long hash = (long) list.getId().hashCode();
				lists.put(hash, list);
				db.commit();
			
				// Aggiunta lista nell'elezione
				Election election = getElection(electionSubject);
				if(election != null) {
					election.addListToElection(list.getId());
					if(editElection(election)) {
						// Aggiunta lista creata nell'utente
						User creator = (User) getUser(creatorNickname);
						if(creator != null) {
							creator.addCreatedList(list.getId());
							if(editUser(creator)) {
								// Aggiunta appartenenza a lista nei membri della lista
								for(User member : list.getMembers()) {
									User user = (User) getUser(member.getNickname());
									if(user != null && !user.getNickname().equals(list.getMayor().getNickname()) && !user.getNickname().equals(creator.getNickname())) {
										user.addUserToList(list.getId());
										editUser(user);
									} else if(user != null && user.getNickname().equals(creator.getNickname())) {
										creator.addUserToList(list.getId());
										editUser(creator);
									}
								}
								// Aggiunta appartenenza a lista al sindaco
								User mayor = list.getMayor();
								if(mayor != null)
									if(mayor.getNickname().equals(creator.getNickname())) {
										creator.addUserToList(list.getId());
										if(editUser(creator))
											return true;
									} else {
										mayor.addUserToList(list.getId());
										if(editUser(mayor))
											return true;
									}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Registra un'elezione nel database
	 * 
	 * @param election	elezione da inserire
	 * @return 			true in caso di successo nell'inserimento, false altrimenti
	 */
	@Override
	public boolean registerElection(Election election) {
		if(!electionExists(election)) {
			DB db = getDB();
			Map<Long, Election> elections = db.getTreeMap("elections");
		    long hash = (long) election.getSubject().hashCode();
		    elections.put(hash, election);
		    db.commit();
		    return true;
	    } else
	      return false;  
	}
	
	/**
	 * Registra un utente nel database
	 * 
	 * @param user	utente da inserire
	 * @return 		true in caso di successo nell'inserimento, false altrimenti
	 */
	@Override
	public boolean registerUser(User user) {
		if(!userExists(user)) {
			DB db = getDB();
			Map<Long, BasicUser> users = db.getTreeMap("users");
			long hash = (long) user.getNickname().hashCode();
			users.put(hash, user);
			db.commit();
			return true;
		} else
			return false;	
	}

	/**
	 * Registra un voto nel database
	 * 
	 * @param vote	voto da inserire
	 * @param voter	nickname dell'utente che ha effettuato il voto
	 * @return 		true in caso di successo nell'inserimento, false altrimenti
	 */
	@Override
	public boolean registerVote(Vote vote, String voter) {
		if(!voteExists(vote, voter)) {
			vote.setId(UUID.randomUUID().toString());
			DB db = getDB();
			Map<Long, Vote> votes = db.getTreeMap("votes");
			long hash = (long) vote.getId().hashCode();
			votes.put(hash, vote);
			db.commit();
			
			User voterUser = (User) getUser(voter);
			if(voterUser != null) {
				voterUser.addVotedElection(vote.getElection());
				if(editUser(voterUser))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Modifica un utente salvato nel database
	 * 
	 * @param user	utente da modificare
	 * @return 		true in caso di successo della modifica, false altrimenti
	 */
	@Override
	public boolean editUser(User user) {
		if(userExists(user)) {
			DB db = getDB();
			Map<Long, BasicUser> users = db.getTreeMap("users");
			long hash = (long) user.getNickname().hashCode();
			users.put(hash, user);
			db.commit();
			return true;
		} else
			return false;
	}
	
	/**
	 * Modifica un'elezione salvata nel database
	 * 
	 * @param election	elezione da modificare
	 * @return 			true in caso di successo della modifica, false altrimenti
	 */
	@Override
	public boolean editElection(Election election) {
		if(electionExists(election)) {
			DB db = getDB();
			Map<Long, Election> elections = db.getTreeMap("elections");
			long hash = (long) election.getSubject().hashCode();
			elections.put(hash, election);
			db.commit();
			return true;
		} else
			return false;
	}

	/**
	 * Modifica una lista salvata nel database
	 * 
	 * @param list		lista da modificare
	 * @return 			true in caso di successo della modifica, false altrimenti
	 */
	@Override
	public boolean editList(List list) {
		if(listExists(list)) {
			DB db = getDB();
			Map<Long, List> lists = db.getTreeMap("lists");
			long hash = (long) list.getId().hashCode();
			lists.put(hash, list);
			db.commit();
			return true;
		} else
			return false;
	}
	
	/**
	 * Restituisce gli utenti che non fanno già parte di una lista all'interno di una certa elezione
	 * 
	 * @param electionSubject		oggetto dell'elezione per cui si vuole filtrare
	 * @return 						arraylist di utenti
	 */
	@Override
	public ArrayList<User> getUnlistedUsersInElection(String electionSubject) {
		DB db = getDB();
		ArrayList<User> usersList = new ArrayList<>();
		
		// Add all existing users
		Map<Long, BasicUser> users = db.getTreeMap("users");
		for(Map.Entry<Long, BasicUser> user : users.entrySet())
			if(user.getValue() instanceof User)
				usersList.add((User) user.getValue());
		
		// Take the selected election and filter lists, remove members from all users list
		Election election = getElection(electionSubject);
		if(election != null) {
			ArrayList<String> lists = election.getLists();
			if(lists != null)
				for(String listID : lists) {
					List list = getList(listID);
					if(list != null) {
						usersList.remove(list.getMayor());
						for(User listedUser : list.getMembers())
							usersList.remove(listedUser);
					}
				}
		}
		
		return usersList;
	}
	
	/**
	 * Restituisce tutte le liste associate ad una certa elezione specificata
	 * 
	 * @param electionSubject		oggetto dell'elezione di cui si vogliono le liste
	 * @return 						arraylist di liste
	 */
	@Override
	public ArrayList<List> getElectionLists(String electionSubject) {
		ArrayList<String> electionListsID = new ArrayList<>();
		ArrayList<List> electionLists = new ArrayList<>();
		Election election = getElection(electionSubject);
		if(election != null) {
			electionListsID = election.getLists();
			for(String IDList : electionListsID) {
				List list = getList(IDList);
				electionLists.add(list);
			}
		}
				
		return electionLists;	
	}
	
	/**
	 * Restituisce tutte le liste in stato pending presenti nel sistema
	 * 
	 * @return 						arraylist di liste
	 */
	@Override
	public ArrayList<List> getPendingLists() {
		DB db = getDB();
		ArrayList<List> pendingLists = new ArrayList<>();
		ArrayList<Election> elections = getUpcomingElections();
		
		// Add all existing lists
		Map<Long, List> lists = db.getTreeMap("lists");
		for(Map.Entry<Long, List> list : lists.entrySet())
			if(list.getValue().getStatus() == List.PENDING_STATUS)
				electionLabel: for(Election election : elections) // Controllo se la lista è relativa ad un'elezione che deve ancora avvenire e il suo stato è pending
					if(election.getLists().contains(list.getValue().getId())) {
						pendingLists.add(list.getValue());
						break electionLabel;
					}
		
		return pendingLists;
	}
	
	/**
	 * Restituisce tutti gli utenti presenti nel sistema
	 * 
	 * @return 	arraylist di utenti
	 */
	@Override
	public ArrayList<User> getUsers() {
		DB db = getDB();
		ArrayList<User> usersList = new ArrayList<>();
		Map<Long, BasicUser> users = db.getTreeMap("users");
		for(Map.Entry<Long, BasicUser> user : users.entrySet())
			if(user.getValue() instanceof User)
				usersList.add((User) user.getValue());
		return usersList;
	}
	
	/**
	 * Restituisce un utente specificandone il nickname
	 * 
	 * @param nickname	nickname dell'utente che si desidera
	 * @return 			basicuser relativo all'utente o null se non presente
	 */
	@Override
	public BasicUser getUser(String nickname) {
		DB db = getDB();
		Map<Long, BasicUser> users = db.getTreeMap("users");
		long hashCode = (long) nickname.hashCode();
		if(users.containsKey(hashCode))
			return users.get(hashCode);
		else
			return null;
	}

	/**
	 * Restituisce la lista di tutti gli utenti nel sistema sotto forma di stringa
	 * 
	 * @return		stringa contenente gli utenti del sistema
	 */
	@Override
	public String getUsersAsString() {
		DB db = getDB();
		Map<Long, BasicUser> users = db.getTreeMap("users");
		String elements = "";
		for(Map.Entry<Long, BasicUser> user : users.entrySet())
			elements += user.getValue().toString() + " ### ";
		return elements;
	}
	
	/**
	 * Restituisce tutte le elezioni presenti nel sistema
	 * 
	 * @return		arraylist di elezioni
	 */
	@Override
	public ArrayList<Election> getElections() {
		DB db = getDB();
		ArrayList<Election> electionsList = new ArrayList<>();
		Map<Long, Election> elections = db.getTreeMap("elections");
		for(Map.Entry<Long, Election> election : elections.entrySet())
			electionsList.add(election.getValue());
		return electionsList;
	}
	
	/**
	 * Restituisce un'elezione specificandone l'oggetto
	 * 
	 * @param subject	l'oggetto dell'elezione richiesta
	 * @return			l'elezione specificata o null se non presente
	 */
	@Override
	public Election getElection(String subject) {
		DB db = getDB();
		Map<Long, Election> elections = db.getTreeMap("elections");
		long hashCode = (long) subject.hashCode();
		if(elections.containsKey(hashCode))
			return elections.get(hashCode);
		else
			return null;
	}
	
	/**
	 * Restituisce tutte le elezioni presenti nel sistema sotto forma di stringa
	 * 
	 * @return		stringa contenente tutte le elezioni
	 */
	@Override
	public String getElectionsAsString() {
		DB db = getDB();
		Map<Long, Election> elections = db.getTreeMap("elections");
		String elements = "";
		for(Map.Entry<Long, Election> election : elections.entrySet())
			elements += election.getValue().toString() + " ### ";
		return elements;
	}
	
	/**
	 * Restituisce tutte le liste presenti nel sistema
	 * 
	 * @return		arraylist di liste
	 */
	@Override
	public ArrayList<List> getLists() {
		DB db = getDB();
		ArrayList<List> listsList = new ArrayList<>();
		Map<Long, List> lists = db.getTreeMap("lists");
		for(Map.Entry<Long, List> list : lists.entrySet())
			listsList.add(list.getValue());
		return listsList;
	}
	
	/**
	 * Restituisce una lista specificandone l'id
	 * 
	 * @param id	l'id della lista richiesta
	 * @return		la lista richiesta o null se non presente
	 */
	@Override
	public List getList(String id) {
		DB db = getDB();
		Map<Long, List> lists = db.getTreeMap("lists");
		long hashCode = (long) id.hashCode();
		if(lists.containsKey(hashCode))
			return lists.get(hashCode);
		else
			return null;
	}
	
	/**
	 * Restituisce le liste presenti nel sistema sotto forma di stringa
	 * 
	 * @return		stringa contenente le liste
	 */
	@Override
	public String getListsAsString() {
		DB db = getDB();
		Map<Long, List> lists = db.getTreeMap("lists");
		String elements = "";
		for(Map.Entry<Long, List> list : lists.entrySet())
			elements += list.getValue().toString() + " ### ";
		return elements;
	}
	
	/**
	 * Restituisce un'elezione specificandone l'oggetto
	 * 
	 * @return		l'elezione specificata o null se non presente
	 */
	private Vote getVote(String id) {
		DB db = getDB();
		Map<Long, Vote> votes = db.getTreeMap("votes");
		long hashCode = (long) id.hashCode();
		if(votes.containsKey(hashCode))
			return votes.get(hashCode);
		else
			return null;
	}
	
	/**
	 * Restituisce tutti i voti presenti nel sistema
	 * 
	 * @return		arraylist di voti
	 */
	private ArrayList<Vote> getVotes() {
		DB db = getDB();
		ArrayList<Vote> votesList = new ArrayList<>();
		Map<Long, Vote> votes = db.getTreeMap("votes");
		for(Map.Entry<Long, Vote> vote : votes.entrySet())
			votesList.add(vote.getValue());
		return votesList;
	}
	
	/**
	 * Restituisce tutti voti presenti nel sistema sotto forma di stringa
	 * 
	 * @return			stringa contenente tutti i voti
	 */
	@Override
	public String getVotesAsString() {
		DB db = getDB();
		Map<Long, Vote> votes = db.getTreeMap("votes");
		String elements = "";
		for(Map.Entry<Long, Vote> vote : votes.entrySet())
			elements += vote.getValue().toString() + " | ";
		return elements;
	}
	
	/**
	 * Restituisce tutte le elezioni che sono in questo momento in corso
	 * 
	 * @return			arraylist di elezioni
	 */
	@Override
	public ArrayList<Election> getActiveElections() {
		DB db = getDB();
		Date today = getToday();
		ArrayList<Election> electionsList = new ArrayList<>();
		Map<Long, Election> elections = db.getTreeMap("elections");
		for(Map.Entry<Long, Election> election : elections.entrySet())
			if(election.getValue().getStartDateTime().before(today) && election.getValue().getEndDateTime().after(today))
				electionsList.add(election.getValue());
		return electionsList;
	}
	
	/**
	 * Restituisce tutte le liste create da un particolare utente
	 * 
	 * @param profile	l'utente di cui si vogliono le liste create
	 * @return			arraylist di liste
	 */
	@Override
	public ArrayList<List> getUserCreatedLists(User profile) {
		User user = (User) getUser(profile.getNickname());
		ArrayList<String> userListsIDs = user.getCreatedLists();
		ArrayList<List> userLists = new ArrayList<>();
		for(String listID : userListsIDs)
			userLists.add(getList(listID));
		return userLists;
	}
	
	/**
	 * Restituisce tutte le elezioni non ancora iniziate
	 * 
	 * @return			arraylist di elezioni
	 */
	@Override
	public ArrayList<Election> getUpcomingElections() {
		DB db = getDB();
		Date today = getToday();
		ArrayList<Election> electionsList = new ArrayList<>();
		Map<Long, Election> elections = db.getTreeMap("elections");
		for(Map.Entry<Long, Election> election : elections.entrySet())
			if(election.getValue().getStartDateTime().after(today))
				electionsList.add(election.getValue());
		return electionsList;
	}
	
	/**
	 * Restituisce tutte le elezioni concluse
	 * 
	 * @return			arraylist di elezioni
	 */
	public ArrayList<Election> getConcludedElections() {
		DB db = getDB();
		Date today = getToday();
		ArrayList<Election> electionsList = new ArrayList<>();
		Map<Long, Election> elections = db.getTreeMap("elections");
		for(Map.Entry<Long, Election> election : elections.entrySet())
			if(election.getValue().getEndDateTime().before(today))
				electionsList.add(election.getValue());
		return electionsList;
	}
	
	/**
	 * Restituisce tutte le elezioni in cui un utente non ha ancora espresso il proprio voto
	 * 
	 * @param profile	l'utente di cui si vogliono le elezioni non votate
	 * @return			arraylist di elezioni
	 */
	@Override
	public ArrayList<Election> getUnvotedElections(User profile) {
		User user = (User) getUser(profile.getNickname());
		ArrayList<String> userElectionsSubjs = user.getVotedElections();
		ArrayList<Election> availableElections = getActiveElections();
		for(String electionSubj : userElectionsSubjs)
			availableElections.remove(getElection(electionSubj));
		return availableElections;
	}
	
	/**
	 * Restituisce i risultati di tutte le elezioni concluse, ordinando in modo decrescente per numero di voti le liste 
	 * e i candidati di ogni lista
	 * 
	 * @return			arraylist di risultati sotto forma di stringa
	 */
	@Override
	public ArrayList<String> getResults() {
		ArrayList<String> results = new ArrayList<>();
		
		ArrayList<VotedElection> computingElections = new ArrayList<>();
		for(Election el : getConcludedElections())
			computingElections.add(new VotedElection(el.getSubject()));
		
		// Setup results data structure
		for(Vote vote : getVotes()) {
			int electionIndex = computingElections.indexOf(new VotedElection(vote.getElection()));
			if(electionIndex > -1) { // It is a vote which belongs to a not computed election
				int listIndex = computingElections.get(electionIndex).getVotedLists().indexOf(new VotedList(vote.getList()));
				if(listIndex > -1) { // It is a list already scheduled
					computingElections.get(electionIndex).getVotedLists().get(listIndex).addVote();
				} else {
					computingElections.get(electionIndex).addVotedList(vote.getList());
					listIndex = computingElections.get(electionIndex).getVotedLists().indexOf(new VotedList(vote.getList()));
				}
				
				int candidateIndex = computingElections.get(electionIndex).getVotedLists().get(listIndex).getVotedCandidates().indexOf(new VotedCandidate(vote.getCandidate()));
				if(candidateIndex > -1) // It is a candidate already scheduled
					computingElections.get(electionIndex).getVotedLists().get(listIndex).getVotedCandidates().get(candidateIndex).addVote();
				else
					computingElections.get(electionIndex).getVotedLists().get(listIndex).addVotedCandidate(vote.getCandidate());
			}
		}
			
		// Sort elements and build return object
		for(VotedElection ve : computingElections) {
			String result = ve.getElectionSubj();
			
			Collections.sort(ve.getVotedLists(), new Comparator<VotedList>() {
			    @Override
			    public int compare(VotedList list1, VotedList list2) {
			        if (list1.getVotes() > list2.getVotes())
			            return 1;
			        if (list1.getVotes() < list2.getVotes())
			            return -1;
			        return 0;
			    }
			});
			
			for(VotedList vl : ve.getVotedLists()) {
				result += " " + vl.getVotes() + ":" + getList(vl.getListID()).getName() + " |";
				
				Collections.sort(vl.getVotedCandidates(), new Comparator<VotedCandidate>() {
				    @Override
				    public int compare(VotedCandidate candidate1, VotedCandidate candidate2) {
				        if (candidate1.getVotes() > candidate2.getVotes())
				            return 1;
				        if (candidate1.getVotes() < candidate2.getVotes())
				            return -1;
				        return 0;
				    }
				});
				
				for(VotedCandidate vc : vl.getVotedCandidates())
					result += " " + vc.getVotes() + ":" + vc.getNickname();
			}
			
			results.add(result + " # ");
		}

		return results;
	}
	
	/**
	 * Restituisce la data di oggi presente e modificabile nel file today.txt
	 * 
	 * @return			oggetto Date rappresentante data e ora in corso
	 */
	private Date getToday() {
		checkActualDatetime();
		Date result = new Date();
		try {
			DataInputStream in = new DataInputStream(new FileInputStream("today.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line, content = "";
			
			while ((line = br.readLine()) != null)
				content += line;
			
			try {
			    result = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(content.trim());
			} catch(ParseException e) {
				System.err.println("Error: " + e.getMessage());
			}
			in.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
				
		return result;
	}
	
	/**
	 * Restituisce la presenza o meno di un utente nel database, controllando sia nickname che codice fiscale
	 * 
	 * @param checkUser	l'utente di cui si vuole verificare l'esistenza
	 * @return			true se l'elemento esiste nel database, false altrimenti
	 */
	private boolean userExists(User checkUser) {
		DB db = getDB();
		Map<Long, BasicUser> users = db.getTreeMap("users");
		for(Map.Entry<Long, BasicUser> user : users.entrySet())
			if(user.getValue() instanceof User && (((User)user.getValue()).getCf().equals(checkUser.getCf()) || ((User)user.getValue()).getNickname().equals(checkUser.getNickname())))
				return true;
		return false;
	}
	
	/**
	 * Restituisce la presenza o meno di un'elezione nel database in base all'oggetto 
	 * 
	 * @param checkElection	l'elezione di cui si vuole verificare l'esistenza
	 * @return				true se l'elemento esiste nel database, false altrimenti
	 */
	private boolean electionExists(Election checkElection) {
		if(getElection(checkElection.getSubject()) != null)
			return true;
		return false;
	}
	
	/**
	 * Restituisce la presenza o meno di una lista nel database in base all'id
	 * 
	 * @param checkList		la lista di cui si vuole verificare l'esistenza
	 * @return				true se l'elemento esiste nel database, false altrimenti
	 */
	private boolean listExists(List checkList) {
		if(getList(checkList.getId()) != null)
			return true;
		return false;
	}
	
	/**
	 * Restituisce la presenza o meno di un voto nel database in base all'utente votante
	 * 
	 * @param vote		oggetto voto
	 * @param voter		nickname del votante
	 * @return			true se l'elemento esiste nel database, false altrimenti
	 */
	private boolean voteExists(Vote vote, String voter) {
		if(getUnvotedElections((User) getUser(voter)).contains(getElection(vote.getElection())))
			return false;
		return true;
	}
	
	/**
	 * Controlla che l'admin sia presente nel database, in caso contrario lo aggiunge
	 * 
	 * @param db		database
	 */
	private void checkAdminInDB(DB db) {
		Map<Long, BasicUser> users = db.getTreeMap("users");
		long hashCode = (long) "admin".hashCode();
		if(!users.containsKey(hashCode))
			users.put(hashCode, new Admin("admin", "admin"));
	}
	
	/**
	 * Controlla che la data e l'ora presenti nel file today.txt siano valide, in caso contrario le riscrive
	 * 
	 */
	private void checkActualDatetime() {
		try {
			DataInputStream in = new DataInputStream(new FileInputStream("today.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line, content = "";
			Date result = new Date();
			
			while ((line = br.readLine()) != null)
				content += line;
			
			try {
			    result =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(content.trim());
			} catch(ParseException e) {
				System.err.println("Error: " + e.getMessage());

				FileWriter writeOut = new FileWriter(new File("today.txt"), false);
				writeOut.write(result.getDate() + "-" + (1 + result.getMonth()) + "-" + (1900 + result.getYear()) + " " + result.getHours() + ":" + result.getMinutes() + ":" + result.getSeconds());
				writeOut.close();
			}
			in.close();
		} catch (IOException e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Restituisce un oggetto database rappresentante la struttura mapDB
	 * 
	 * @return			database
	 */
	private DB getDB() {
		ServletContext context = this.getServletContext();
		synchronized (context) {
			DB db = (DB)context.getAttribute("DB");
			if(db == null) {
				db = DBMaker.newFileDB(new File("db")).closeOnJvmShutdown().make();
				context.setAttribute("DB", db);
			}
			checkAdminInDB(db);
			checkActualDatetime();
			return db;
		}
	}

	/**
	 * Classe privata con la funzione di organizzare il conteggio di un elezione
	 * 
	 */
	private class VotedElection {
		private String electionSubj;
	    private ArrayList<VotedList> votedLists = new ArrayList<>();
	        
	    public VotedElection(String subject) {
	    	this.electionSubj = subject;
	    }
	    
	    public String getElectionSubj() {
	    	return electionSubj;
	    }
	    
	    public ArrayList<VotedList> getVotedLists(){
	    	return votedLists;
		}
	
	    public void setElectionSubj(String electionSubj) {
	    	this.electionSubj = electionSubj;
	    }
	
	    public void addVotedList(String listID) {
	    	votedLists.add(new VotedList(listID));
	    }
	    
	    public ArrayList<String> getVotedListsAsArrayOfStrings() {
	    	ArrayList<String> votedListsR = new ArrayList<>();
	    	for(VotedList list:votedLists)
	    		votedListsR.add(list.getListID());
	    	return votedListsR;
	    }
	    
	    public VotedList getVotedList(String id) {
	    	for(VotedList list:votedLists)
	    		if(list.getListID().equals(id))
	    			return list;
	    	return null;
	    }
	    
	    @Override
		public boolean equals(Object obj) {
			if(obj instanceof VotedElection && ((VotedElection) obj).getElectionSubj().equals(electionSubj))
				return true;
			else
				return false;
		}
	}
	
	/**
	 * Classe privata con la funzione di organizzare il conteggio delle liste
	 * 
	 */
	private class VotedList {
    	private String listID;
    	private int votes;
    	private ArrayList<VotedCandidate> votedCandidates = new ArrayList<>();
          
    	public VotedList(String listID) {
    		this.listID = listID;
    		this.votes = 1;
    		for(User candidate : getList(listID).getMembers())
    			this.votedCandidates.add(new VotedCandidate(candidate.getNickname()));
    	}

    	public String getListID() {
    		return listID;
    	}

    	public void setListID(String listID) {
    		this.listID = listID;
    	}

    	public int getVotes() {
    		return votes;
    	}

    	public void addVote() {
    		this.votes++;
    	}

    	public void addVotedCandidate(String nickname) {
    		votedCandidates.add(new VotedCandidate(nickname));
    	}
      
    	public ArrayList<VotedCandidate> getVotedCandidates() {
    		return votedCandidates;
    	}
      
    	public VotedCandidate getVotedCandidate(String nick) {
    		for(VotedCandidate candidate:votedCandidates)
	    		if(candidate.getNickname().equals(nick))
	    			return candidate;
	    	return null;
    	}

    	@Override
		public boolean equals(Object obj) {
			if(obj instanceof VotedList && ((VotedList) obj).getListID().equals(listID))
				return true;
			else
				return false;
		}
    }
	
	/**
	 * Classe privata con la funzione di organizzare il conteggio dei candidati
	 * 
	 */
	private class VotedCandidate {
		private String nickname;
		private int votes;
    
		public VotedCandidate(String nickname) {
			this.nickname = nickname;
			this.votes = 1;
		}

		public String getNickname() {
			return nickname;
		}
    
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
    
		public int getVotes() {
			return votes;
		}
    
		public void addVote() {
			this.votes++;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof VotedCandidate && ((VotedCandidate) obj).getNickname().equals(nickname))
				return true;
			else
				return false;
		}
	}
	
}
