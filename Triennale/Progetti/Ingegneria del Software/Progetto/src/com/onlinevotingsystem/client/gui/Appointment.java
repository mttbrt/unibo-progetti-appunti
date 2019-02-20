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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Appointment extends Composite implements Foreground {
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(2, 2);
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ArrayList<User> allUsersList = new ArrayList<>();
	private ArrayList<User> selectedUsers = new ArrayList<>();
	private ClientImplementor clientImpl;
	private User sessionUser;
	
	public Appointment(ClientImplementor clientImpl, User sessionUser) {
		initWidget(this.vPanel);
		this.sessionUser = sessionUser;
		this.clientImpl = clientImpl;
		
		clientImpl.getUsers();
				
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-vertical margin-horizontal width-label");
		
		// Buttons
		Button appointBTN = new Button("Appoint");
		appointBTN.setStyleName("btn btn-success");
		appointBTN.addClickHandler(new AppointBTNClickHandler());
		gridPanel.setWidget(1, 0, appointBTN);
		
		Button revokeBTN = new Button("Revoke");
		revokeBTN.setStyleName("btn btn-danger");
		revokeBTN.addClickHandler(new RevokeBTNClickHandler());
		gridPanel.setWidget(1, 1, revokeBTN);
		
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
	
	@Override
	public void onUpdateForeground(User sessionUser) {
		clientImpl.getUsers();
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
	 * Imposta l'elenco degli utenti registrati al sito
	 * @param users		utenti registrati
	 */
	public void setUsersList(ArrayList<User> users) {
		VerticalPanel usersCheckBoxes = new VerticalPanel();
		VerticalPanel usersData = new VerticalPanel();
		int k = 0;
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
		usersData.add(new Label(user.getName() + " " + user.getSurname() + " [" + user.getCf() + "] " + (user.isMunicipalOfficial() ? "Municipal official" : "Standard user")));
		k++;
		}
		gridPanel.setWidget(0, 0, usersCheckBoxes);
		gridPanel.setWidget(0, 1, usersData);
		}
	
	/**
	 * Gestisce il click del button Appoint: nomina l'utente funzionario comunale
	 */
	private class AppointBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today= new Date();
			//check if admin
			if(sessionUser == null) { 
				for(User user : selectedUsers) {
					user.setMunicipalOfficial(true);
					clientImpl.editUser(user);
				}
			}else {
				//check if the document is valid
				if(!sessionUser.getDocument().getEDate().before(today)) {
					for(User user : selectedUsers) {
						user.setMunicipalOfficial(true);
						clientImpl.editUser(user);
					}
				}else {
					updateErrorLabel("Your document is expired!");
				}
			}
			selectedUsers.clear();
		}
	}
	
	/**
	 * Gestisce il click del button Revoke: revoca la nomina di funzionario comunale
	 */
	private class RevokeBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today= new Date();
			//check if admin
			if(sessionUser == null) { 
				for(User user : selectedUsers) {
					user.setMunicipalOfficial(false);
					clientImpl.editUser(user);
				}
			}else {
				//check if the document is valid
				if(!sessionUser.getDocument().getEDate().before(today)) {
					for(User user : selectedUsers) {
						user.setMunicipalOfficial(false);
						clientImpl.editUser(user);
					}
				}else {
					updateErrorLabel("Your document is expired!");
				}
			}
			selectedUsers.clear();
		}
	}
	
}

