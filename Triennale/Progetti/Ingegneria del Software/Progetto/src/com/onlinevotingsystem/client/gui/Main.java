package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.Admin;
import com.onlinevotingsystem.client.model.BasicUser;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Main extends Composite {
	
	// Codes to display messages
	public static final int SUCCESS_CODE = 0;
	public static final int ERROR_CODE = 1;
	public static final int NEUTRAL_CODE = 2;
	
	// Codes to recognise the clicked buttons
	private static final String REGISTER_BTN = "register";
	private static final String LOGIN_BTN = "login";
	private static final String LOGOUT_BTN = "logout";
	private static final String APPOINT_BTN = "appoint";
	private static final String ELECTION_BTN = "electionSetup";
	private static final String LIST_BTN = "listSetup";
	private static final String APPROV_BTN = "approv";
	private static final String PROFILE_BTN = "profile";
	private static final String VOTE_BTN = "voteSetup";
	private static final String RESULTS_BTN = "results";
	
	// Main UI structure
	private VerticalPanel wrapper = new VerticalPanel();
	private VerticalPanel navbarPanel = new VerticalPanel();
	private VerticalPanel contentPanel = new VerticalPanel();
	
	// UI Fragments
	private Foreground foreground = null; // The visible fragment
	private Registration registration = null;
	private Login login = null;
	private Appointment appointment = null;
	private ElectionSetup electionSetup = null;
	private ListSetup listSetup = null;
	private Profile profile = null;
	private Approval approval = null;
	private VoteSetup voteSetup = null;
	private Results results = null;
	
	// Client implementor
	private ClientImplementor clientImpl;
	
	// User status variables
	private boolean isAdmin = false;
	private User sessionUser = null;
	
	public Main(ClientImplementor clientImpl) {
		initWidget(wrapper);
		wrapper.add(navbarPanel);
		wrapper.add(contentPanel);
		this.clientImpl = clientImpl;
		
		setupNavbar();
	}
	
	/**
	 * Richiama il metodo override nel foreground definito, 
	 * rispetto al codice (tipo di messaggio) che gli viene passato come paramentro.
	 * Inoltra un messaggio da stampare nella label.
	 * @param code		tipo di messaggio
	 * @param msg		messaggio
	 */
	public void displayMsg(int code, String msg) {
		switch(code) {
			case SUCCESS_CODE: 
				foreground.updateSuccessLabel(msg);
				break;
			case ERROR_CODE:
				foreground.updateErrorLabel(msg);
				break;
			case NEUTRAL_CODE:
				foreground.updateInfoLabel(msg);
				break;
		}
	}
	
	/**
	 * Permette l'accesso alle funzionalita' relative all'utente che effettua il login
	 * @param user		utente generale
	 */
	public void loginUser(BasicUser user) {
		if(user instanceof User) {
			sessionUser = (User) user;
			isAdmin = false;
			if(sessionUser.isMunicipalOfficial())
				displayMsg(NEUTRAL_CODE, "Municipal official user");
			else
				displayMsg(NEUTRAL_CODE, "Standard user");
		} else if(user instanceof Admin) {
			sessionUser = null;
			isAdmin = true;
			displayMsg(NEUTRAL_CODE, "Admin user");
		} else {
			sessionUser = null;
			isAdmin = false;
			displayMsg(ERROR_CODE, "Login error");
		}
		setupNavbar();
	}
	
	/**
	 * Richiama il metodo relativo all'elenco degli utenti registrati
	 * @param users		utenti registrati
	 */
	public void setUsersList(ArrayList<User> users) {
		if(appointment != null && foreground == appointment)
			appointment.setUsersList(users);
		else if(listSetup != null && foreground == listSetup)
			listSetup.setUsersList(users);
	}
	
	/**
	 * Richiama il metodo relativo all'elenco delle elezioni create
	 * @param elections		elezioni create
	 */
	public void setElectionsList(ArrayList<Election> elections) {
		if(listSetup != null && foreground == listSetup)
			listSetup.setElectionsList(elections);
		else if(voteSetup != null && foreground == voteSetup)
			voteSetup.setElectionsList(elections);
	}
	
	/**
	 * Richiama il metodo relativo al salvataggio della lista (associata al sindaco)
	 * @param user		candidato sindaco
	 */
	public void setSpecificUser(User user) {
		if(listSetup != null && foreground == listSetup)
			listSetup.setSpecificUser(user);
	}
	
	/**
	 * Richiama il metodo relativo all'elenco delle liste create in attesa di approvazione
	 * @param lists		liste in attesa
	 */
	public void setListsList(ArrayList<List> lists) {
		if(profile != null && foreground == profile)
			profile.setListsList(lists);
		else if(approval != null && foreground == approval)
			approval.setListsList(lists);
		else if(voteSetup != null && foreground == voteSetup)
			voteSetup.setElectionLists(lists);
	}	
	
	public void setSpecificElection(Election election) {

	}
	
	/**
	 * Richiama il metodo relativo all'elenco dei candidati per una specifica lista
	 * @param list		lista
	 */
	public void setSpecificList(List list) {
		if(voteSetup != null && foreground == voteSetup)
			voteSetup.setSelectedList(list);
	}
	
	/**
	 * Richiama il metodo relativo all'elenco dei risultati
	 * @param result		risultati elezioni
	 */
	public void setResults(ArrayList<String> result) {
		if(results != null && foreground == results)
			results.editResults(result);
	}
	
	/**
	 * Imposta la barra di navigazione: insieme di buttons che esprimono le funzionalita a cui può accedere l'utente
	 */
	private void setupNavbar() {
		// Clean old navbar
		if(navbarPanel.getWidgetCount() > 0)
			for(int i = 0; i < navbarPanel.getWidgetCount(); i++)
				navbarPanel.remove(i);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		
		Button registerBTN = new Button("Register");
		registerBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
		registerBTN.setTitle(REGISTER_BTN);
		registerBTN.addClickHandler(new ButtonClickHandler());
		hPanel.add(registerBTN);
		
		Button loginBTN = new Button("Login");
		loginBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
		loginBTN.setTitle(LOGIN_BTN);
		loginBTN.addClickHandler(new ButtonClickHandler());
		hPanel.add(loginBTN);
		
		Button resultsBTN = new Button("Results");
		resultsBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
		resultsBTN.setTitle(RESULTS_BTN);
		resultsBTN.addClickHandler(new ButtonClickHandler());
		hPanel.add(resultsBTN);
		
		if(isAdmin) { // Admin buttons
			Button appointBTN = new Button("Appoint");
			appointBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
			appointBTN.setTitle(APPOINT_BTN);
			appointBTN.addClickHandler(new ButtonClickHandler());
			hPanel.add(appointBTN);
		}
		
		if((sessionUser != null && sessionUser.isMunicipalOfficial()) || isAdmin) { // Municipal official buttons
			Button electionBTN = new Button("New Election");
			electionBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
			electionBTN.setTitle(ELECTION_BTN);
			electionBTN.addClickHandler(new ButtonClickHandler());
			hPanel.add(electionBTN);
			
			Button approvalBTN = new Button("Approve Lists");
			approvalBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
			approvalBTN.setTitle(APPROV_BTN);
			approvalBTN.addClickHandler(new ButtonClickHandler());
			hPanel.add(approvalBTN);
		}
		
		if(sessionUser != null) { // Logged in user buttons
			if(!sessionUser.isMunicipalOfficial()) { // NOt municipal official
				Button listBTN = new Button("New List");
				listBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
				listBTN.setTitle(LIST_BTN);
				listBTN.addClickHandler(new ButtonClickHandler());
				hPanel.add(listBTN);
			}
			
			Button profileBTN = new Button("My Profile");
			profileBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
			profileBTN.setTitle(PROFILE_BTN);
			profileBTN.addClickHandler(new ButtonClickHandler());
			hPanel.add(profileBTN);
			
			Button voteBTN = new Button("Vote");
			voteBTN.setStyleName("btn btn-primary margin-horizontal margin-vertical");
			voteBTN.setTitle(VOTE_BTN);
			voteBTN.addClickHandler(new ButtonClickHandler());
			hPanel.add(voteBTN);
			
		}
		
		Button logoutBTN = new Button("Logout");
		logoutBTN.setStyleName("btn btn-danger margin-horizontal margin-vertical");
		logoutBTN.setTitle(LOGOUT_BTN);
		logoutBTN.addClickHandler(new ButtonClickHandler());
		hPanel.add(logoutBTN);
		
		navbarPanel.add(hPanel);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Registrazione
	 */
	private void setupRegistration() {
		if(registration == null) 
			registration = new Registration(clientImpl);
		else
			registration.onUpdateForeground(sessionUser);
		
		foreground = registration;
		setupForeground(registration);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Login
	 */
	private void setupLogin() {
		if(login == null)
			login = new Login(clientImpl);	
		else
			login.onUpdateForeground(sessionUser);
		
		foreground = login;
		setupLogout();
		setupForeground(login);
	}
	
	/**
	 * Logout utente e ripristino barra di navigazione di default
	 */
	private void setupLogout() {
		sessionUser = null;
		isAdmin = false;
		setupNavbar();
		setupForeground(null);
		displayMsg(SUCCESS_CODE, "Successfully logged out");
	}
	
	/**
	 * Creazione/Aggiornamento Gui Nomina
	 */
	private void setupAppointment() {
		if(appointment == null)
			appointment = new Appointment(clientImpl, sessionUser);
		else
			appointment.onUpdateForeground(sessionUser);
			
		foreground = appointment;
		setupForeground(appointment);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Creazione elezione
	 */
	private void setupNewElection() {
		if(listSetup == null)
			electionSetup = new ElectionSetup(clientImpl);
		else
			electionSetup.onUpdateForeground(sessionUser);
		
		foreground = electionSetup;
		setupForeground(electionSetup);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Creazione lista
	 */
	private void setupNewList() {
		if(listSetup == null)
			listSetup = new ListSetup(clientImpl, sessionUser);
		else
			listSetup.onUpdateForeground(sessionUser);
		
		foreground = listSetup;
		setupForeground(listSetup);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Approvazione lista
	 */
	private void setupApproval() {
		if(approval == null)
			approval = new Approval(clientImpl, sessionUser);
		else
			approval.onUpdateForeground(sessionUser);
		
		foreground = approval;
		setupForeground(approval);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Profilo utente
	 */
	private void setupProfile() {
		if(profile == null)
			profile = new Profile(clientImpl, sessionUser);
		else
			profile.onUpdateForeground(sessionUser);
		
		foreground = profile;
		setupForeground(profile);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Votazione
	 */
	private void setupVote() {
		if(voteSetup == null)
			voteSetup = new VoteSetup(clientImpl, sessionUser);
		else
			voteSetup.onUpdateForeground(sessionUser);
		
		foreground = voteSetup;
		setupForeground(voteSetup);
	}
	
	/**
	 * Creazione/Aggiornamento Gui Risultati
	 */
	private void setupResults() {
		if(results == null)
			results = new Results(clientImpl);
		else
			results.onUpdateForeground(sessionUser);
		
		foreground = results;
		setupForeground(results);
	}
		
	/**
	 * Rimuove tutti gli elementi nel content panel e aggiunge l'elemento composto specificato come parametro
	 * @param element		insieme di elementi grafici
	 */
	private void setupForeground(Composite element) {
		int widgets = contentPanel.getWidgetCount();
		if(widgets > 0)
			for(int i = 0; i < widgets; i++)
				contentPanel.remove(i); 
		if(element != null)
			contentPanel.add(element);
	}
	
	/**
	 * Gestisce il click dei buttons nella barra di navigazione: richiama il metodo setup relativo al button cliccato
	 *
	 */
	private class ButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			Button clicked = (Button) event.getSource();
			switch(clicked.getTitle()) {
				case REGISTER_BTN: 
					setupRegistration();
					break;
				case LOGIN_BTN: 
					setupLogin();
					break;
				case LOGOUT_BTN: 
					setupLogout();
					break;
				case APPOINT_BTN: 
					setupAppointment();
					break;
				case ELECTION_BTN: 
					setupNewElection();
					break;
				case LIST_BTN: 
					setupNewList();
					break;
				case APPROV_BTN: 
					setupApproval();
					break;
				case PROFILE_BTN: 
					setupProfile();
					break;
				case VOTE_BTN: 
					setupVote();
					break;
				case RESULTS_BTN: 
					setupResults();
					break;
			}
		}
	}
	
}

