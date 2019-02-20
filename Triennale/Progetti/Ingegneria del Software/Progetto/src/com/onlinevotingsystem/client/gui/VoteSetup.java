package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Vote;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class VoteSetup extends Composite implements Foreground{
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(7, 2);
	
	private ListBox electionLB;
	private ListBox listLB;
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private User sessionUser;
	private ClientImplementor clientImpl;
	private ArrayList<RadioButton> radioButtons = new ArrayList<>();
	
	public VoteSetup (ClientImplementor clientImpl, User sessionUser) {
		initWidget(this.vPanel);
		this.sessionUser = sessionUser;
		this.clientImpl = clientImpl;
		
		clientImpl.getUnvotedElections(sessionUser);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-horizontal margin-vertical width-label");
		
		// Buttons
		Button voteBTN = new Button("Vote");
		voteBTN.setStyleName("btn btn-success");
		voteBTN.addClickHandler(new VoteBTNClickHandler());
		gridPanel.setWidget(6, 0, voteBTN);
		
		gridPanel.setStyleName("padding-table");
		vPanel.add(gridPanel);
		vPanel.add(successLBL);
		vPanel.add(errorLBL);
		
		infoLBL = new Label("--");
		infoLBL.setStyleName("alert alert-info margin-horizontal");
		vPanel.add(infoLBL);
	}
	
	/**
	 * Pulisce il contenuto delle labels
	 */
	private void resetLabels() {
		updateErrorLabel("");
		updateSuccessLabel("");
		updateInfoLabel("");
	}
	
	/**
	 * Ripristina i valori di default
	 */
	private void resetAll() {
		resetLabels();
		electionLB.setSelectedIndex(0);
		listLB.setSelectedIndex(0);
		clearListData();
	}
	
	@Override
	public void onUpdateForeground(User sessionUser) {
		if(!this.sessionUser.equals(sessionUser) || this.sessionUser == null)
			this.sessionUser = sessionUser;
		resetAll();
		clientImpl.getUnvotedElections(sessionUser);
	}

	@Override
	public void updateInfoLabel(String txt) {
		infoLBL.setText(txt);
	}

	@Override
	public void updateErrorLabel(String txt) {
		errorLBL.setText(txt);
	}

	@Override
	public void updateSuccessLabel(String txt) {
		successLBL.setText(txt);
	}
	
	/**
	 * Imposta l'elenco delle elezioni create
	 * @param elections		elezioni create
	 */
	public void setElectionsList(ArrayList<Election> elections) {
		// Election choice
		Label lblElection = new Label("Election");
		electionLB = new ListBox();
		electionLB.setStyleName("form-control");
		for(Election election : elections)
			electionLB.addItem(election.getSubject());
		gridPanel.setWidget(0, 0, lblElection);
		gridPanel.setWidget(0, 1, electionLB);	
		
		electionLB.addClickHandler(new ElectionClickHandler());
		updateElectionLists();
		updateListCandidates();
	}
	
	/**
	 * Imposta l'elenco delle liste approvate per l'elezione selezionata
	 * @param lists		insieme di liste relative all'elezione
	 */
	public void setElectionLists(ArrayList<List> lists) {
		// List choice
		Label lblList = new Label("List");
		listLB = new ListBox();
		listLB.setStyleName("form-control");
		for(List list : lists)
			if(list.getStatus() == List.APPROVED_STATUS)
				listLB.addItem("name: " + list.getName() + " - " + "symbol: " + list.getSymbol(), list.getId());
		gridPanel.setWidget(1, 0, lblList);
		gridPanel.setWidget(1, 1, listLB);
		
		listLB.addClickHandler(new ListClickHandler());
		updateListCandidates();
	}
	
	/**
	 * Imposta l'elenco dei candidati per la lista selezionata
	 * @param list		lista
	 */
	public void setSelectedList(List list) {
		gridPanel.setWidget(2, 0, new Label("Mayor"));
		RadioButton rb = new RadioButton("candidate");
		rb.setValue(true);
		rb.setFormValue(list.getMayor().getNickname());
		radioButtons.add(rb);
		gridPanel.setWidget(3, 0, rb);
		gridPanel.setWidget(3, 1, new Label(list.getMayor().getName() + " " + list.getMayor().getSurname()));
		
		gridPanel.setWidget(4, 0, new Label("Members"));
		VerticalPanel membersRadioButtons = new VerticalPanel();
		VerticalPanel membersData = new VerticalPanel();
		ArrayList<User> members = list.getMembers();
		if(members != null && members.size() > 0)
			for(User member : members)
				if(!member.getNickname().equals(list.getMayor().getNickname())) {
					RadioButton rbCan = new RadioButton("candidate");
					rbCan.setFormValue(member.getNickname());
					radioButtons.add(rbCan);
					membersRadioButtons.add(rbCan);
					membersData.add(new Label(member.getName() + " " + member.getSurname()));
				}
		gridPanel.setWidget(5, 0, membersRadioButtons);
		gridPanel.setWidget(5, 1, membersData);
	}
	
	/**
	 * Aggiorna l'elenco delle liste
	 */
	private void updateElectionLists() {
		clearListData();		
		String electionSubject = electionLB.getSelectedItemText().trim();
		if(!electionSubject.equals(""))
			clientImpl.getElectionLists(electionSubject);
		else
			updateErrorLabel("No election available.");
	}
	
	/**
	 * Aggiorna l'elenco dei candidati
	 */
	private void updateListCandidates() {
		clearListData();
		String listID = listLB.getValue(listLB.getSelectedIndex());
		if(!listID.equals(""))
			clientImpl.getList(listID);
		else
			updateErrorLabel("No list available.");
	}

	/**
	 * Pulisce l'elenco dei candidati
	 */
	private void clearListData() {
		radioButtons.clear();
		for(int i = 2; i < 6; i++) 
			for(int j = 0; j < 2; j++)
				gridPanel.clearCell(i, j);
	}
	
	/**
	 * Gestisce il click su una voce della listBox Elezioni
	 */
	private class ElectionClickHandler implements ClickHandler {
		@Override
        public void onClick(ClickEvent event) {
			 updateElectionLists();
        }
	}
	
	/**
	 * Gestisce il click su una voce della listBox Liste
	 */
	private class ListClickHandler implements ClickHandler {
		@Override
        public void onClick(ClickEvent event) {
			updateListCandidates();
        }
	}
	
	/**
	 * Gestisce il click del button Vote: crea e registra il voto
	 */
	private class VoteBTNClickHandler implements ClickHandler {
		@Override
        public void onClick(ClickEvent event) {
			resetLabels();
			String election = electionLB.getSelectedItemText().trim();
			String list = listLB.getValue(listLB.getSelectedIndex());
			String candidate = "";
			Date today= new Date();
			//check if the document is valid
			if(!sessionUser.getDocument().getEDate().before(today)) {
				if(radioButtons.size() > 0) {
					for(RadioButton rb : radioButtons)
						if(rb.getValue()) {
							candidate = rb.getFormValue();
							break;
						}					
					Vote vote = new Vote(election, list, candidate);
					clientImpl.registerVote(vote, sessionUser.getNickname());
				}
			}else {
				updateErrorLabel("Your document is expired!");
			}
			updateElectionLists();
			clientImpl.getUnvotedElections(sessionUser);
			clientImpl.getVotesAsString();
        }
	}
}
