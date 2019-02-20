package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class ListSetup extends Composite implements Foreground {
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(7, 2);
	
	private ListBox electionLB;
	private TextBox nameTB;
	private TextBox symbolTB;
	private ListBox mayorLB;
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ArrayList<User> allUsersList = new ArrayList<>();
	private ArrayList<User> selectedUsers = new ArrayList<>();
	private User sessionUser;
	private ClientImplementor clientImpl;

	public ListSetup(ClientImplementor clientImpl, User sessionUser) {
		initWidget(this.vPanel);
		this.sessionUser = sessionUser;
		this.clientImpl = clientImpl;
		
		clientImpl.getUpcomingElections();
		
		Label lblName = new Label("List name");
		nameTB = new TextBox();
		nameTB.setStyleName("form-control");
		nameTB.getElement().setPropertyString("placeholder", "Name");
		gridPanel.setWidget(1, 0, lblName);
		gridPanel.setWidget(1, 1, nameTB);
		
		Label lblSymbol = new Label("Symbol");
		symbolTB = new TextBox();
		symbolTB.setStyleName("form-control");
		symbolTB.getElement().setPropertyString("placeholder", "Symbol description");
		gridPanel.setWidget(2, 0, lblSymbol);
		gridPanel.setWidget(2, 1, symbolTB);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-horizontal margin-vertical width-label");
		
		// Buttons
		Button saveBTN = new Button("Save");
		saveBTN.setStyleName("btn btn-success");
		saveBTN.addClickHandler(new SaveBTNClickHandler());
		gridPanel.setWidget(6, 0, saveBTN);
		
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
		nameTB.setText("");
		symbolTB.setText("");
		mayorLB.setSelectedIndex(0);
	}
	
	@Override
	public void onUpdateForeground(User sessionUser) {
		if(!this.sessionUser.equals(sessionUser) || this.sessionUser == null)
			this.sessionUser = sessionUser;
		resetAll();
		clientImpl.getUpcomingElections();
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
		updateUnlistedUsers();
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
		
		electionLB.addClickHandler(new ListClickHandler());
		updateUnlistedUsers();
	}
	
	/**
	 * Imposta l'elenco degli utenti registrati
	 * @param users		utenti registrati
	 */
	public void setUsersList(ArrayList<User> users) {
		// Mayor choice
		Label lblMayor = new Label("Mayor");
		mayorLB = new ListBox();
		mayorLB.setStyleName("form-control");
		for(User user : users)
			mayorLB.addItem(user.getName() + " " + user.getSurname() + " [" + user.getCf() + "]", user.getNickname());
		gridPanel.setWidget(3, 0, lblMayor);
		gridPanel.setWidget(3, 1, mayorLB);
		
		// Members choice
		gridPanel.setWidget(4, 0, new Label("Members"));
		gridPanel.setWidget(4, 1, new Label("ATTENTION: the mayor is already counted as member."));
		VerticalPanel usersCheckBoxes = new VerticalPanel();
		VerticalPanel usersData = new VerticalPanel();
		allUsersList.clear();
		selectedUsers.clear();
		int k = 0;
		if(users != null && users.size() > 0) {
			for(User user : users) {
				CheckBox cb = new CheckBox();
				cb.setTitle(Integer.toString(k));
				allUsersList.add(k, user);
				cb.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						User selectedUser = allUsersList.get(Integer.parseInt(((CheckBox)event.getSource()).getTitle()));
						if(((CheckBox)event.getSource()).getValue())
							selectedUsers.add(selectedUser);
						else
							selectedUsers.remove(selectedUser);
					}
			    });
				usersCheckBoxes.add(cb);
				usersData.add(new Label(user.getName() + " " + user.getSurname() + " [" + user.getCf() + "] "));
				k++;
			}
		}
		gridPanel.setWidget(5, 0, usersCheckBoxes);
		gridPanel.setWidget(5, 1, usersData);
	}
	
	/**
	 * Crea e salva nel DB una nuova lista (associata al candidato sindaco)
	 * @param mayor		candidato al ruolo di sindaco
	 */
	public void setSpecificUser(User mayor) {
		List list = new List(nameTB.getText().trim(), symbolTB.getText().trim(), mayor, selectedUsers);
		clientImpl.registerList(electionLB.getSelectedValue(), sessionUser.getNickname(), list);
	}
	
	/**
	 * Aggiorna gli utenti non candidati ad una lista
	 */
	private void updateUnlistedUsers() {
		String electionSubject = electionLB.getSelectedItemText().trim();
		if(!electionSubject.equals(""))
			clientImpl.getUnlistedUsersInElection(electionSubject);
		else
			updateErrorLabel("No election available.");
	}
	
	/**
	 * Verifica se la lista che si vuole creare rispetta alcuni vincoli
	 */
	private class SaveBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today= new Date();
			//check if the document is valid
			if(!sessionUser.getDocument().getEDate().before(today)) {			
				String electionSubject = electionLB.getSelectedValue();
				String name = nameTB.getText().trim();
				String symbol = symbolTB.getText().trim();
				String mayorNickname = mayorLB.getSelectedValue();
				
				if(electionSubject.equals(""))
					updateErrorLabel("No election available.");
				else if(name.equals(""))
					updateErrorLabel("List name cannot be empty.");
				else if(symbol.equals(""))
					updateErrorLabel("List symbol description cannot be empty.");
				else if(selectedUsers.size() < 1)
					updateErrorLabel("List must contain at least one selected user.");
				else
					clientImpl.getUser(mayorNickname);
			}else {
				updateErrorLabel("Your document is expired!");
			}
		}
	}
	
	/**
	 * Gestisce il click su una voce della listBox
	 */
	private class ListClickHandler implements ClickHandler {
		@Override
        public void onClick(ClickEvent event) {
			updateUnlistedUsers();
        }
	}
	
}

