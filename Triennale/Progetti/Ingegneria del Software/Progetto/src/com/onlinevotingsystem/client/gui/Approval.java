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
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Approval extends Composite implements Foreground{
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(2, 2);
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ArrayList<List> allLists = new ArrayList<>();
	private ArrayList<List> selectedLists = new ArrayList<>();
	private ClientImplementor clientImpl;
	private User sessionUser;

	public Approval (ClientImplementor clientImpl, User sessionUser) {
		initWidget(this.vPanel);
		this.sessionUser = sessionUser;
		this.clientImpl = clientImpl;
		
		clientImpl.getPendingLists();
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-vertical margin-horizontal width-label");
		
		// Buttons
		Button approveBTN = new Button("Approve");
		approveBTN.setStyleName("btn btn-success");
		approveBTN.addClickHandler(new ApproveBTNClickHandler());
		gridPanel.setWidget(1, 0, approveBTN);
		
		Button rejectBTN = new Button("Reject");
		rejectBTN.setStyleName("btn btn-danger");
		rejectBTN.addClickHandler(new RejectBTNClickHandler());
		gridPanel.setWidget(1, 1, rejectBTN);
		
		gridPanel.setStyleName("padding-table");
		vPanel.add(gridPanel);
		vPanel.add(successLBL);
		vPanel.add(errorLBL);
		
		Button loadBTN = new Button("Load");
		loadBTN.setStyleName("btn btn-warning margin-vertical margin-horizontal");
		loadBTN.addClickHandler(new LoadBTNClickHandler());
		vPanel.add(loadBTN);
		
		Button clearBTN = new Button("Clear");
		clearBTN.setStyleName("btn btn-default margin-horizontal");
		clearBTN.addClickHandler(new ClearBTNClickHandler());
		vPanel.add(clearBTN);
				
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
	
	@Override
	public void onUpdateForeground(User sessionUser) {
		clientImpl.getPendingLists();
	}
	
	/**
	 * Imposta l'elenco delle liste in attesa di approvazione
	 * @param lists		liste in attesa (pending)
	 */
	public void setListsList (ArrayList<List> lists) {
		VerticalPanel listsCheckBoxes = new VerticalPanel();
		VerticalPanel listsData = new VerticalPanel();
		int k = 0;
		for(List list : lists) {
			CheckBox cb = new CheckBox();
			cb.setTitle(Integer.toString(k));
			allLists.add(k, list);
			cb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					List selectedList = allLists.get(Integer.parseInt(((CheckBox)event.getSource()).getTitle()));
					if(((CheckBox)event.getSource()).getValue())
						selectedLists.add(selectedList);
					else
						selectedLists.remove(selectedList);
				}
		    });
			listsCheckBoxes.add(cb);
			listsData.add(new Label(list.getName() + " [" + list.getMayor().getNickname() + " - members:" + list.getMembers().size() + "]"));
			k++;
		}
		gridPanel.setWidget(0, 0, listsCheckBoxes);
		gridPanel.setWidget(0, 1, listsData);
	}
	
	/**
	 * Gestisce il click del button Approve: cambia lo stato della lista
	 */
	private class ApproveBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today= new Date();
			//check if admin
			if(sessionUser == null) {
				for(List list : selectedLists) {
					list.setStatus(List.APPROVED_STATUS);
					clientImpl.editList(list);
				}
			}else {
				//check if the document is valid
				if(!sessionUser.getDocument().getEDate().before(today)) {
					for(List list : selectedLists) {
						list.setStatus(List.APPROVED_STATUS);
						clientImpl.editList(list);
					}
				}else {
					updateErrorLabel("Your document is expired!");
				}
			}
			selectedLists.clear();
		}
	}
	
	/**
	 * Gestisce il click del button Reject: cambia lo stato della lista
	 */
	private class RejectBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today= new Date();
			//check if admin
			if(sessionUser == null) {
				for(List list : selectedLists) {
					list.setStatus(List.REJECTED_STATUS);
					clientImpl.editList(list);
				}
			}else {
				//check if the document is valid
				if(!sessionUser.getDocument().getEDate().before(today)) {
					for(List list : selectedLists) {
						list.setStatus(List.REJECTED_STATUS);
						clientImpl.editList(list);
					}
				}else {
					updateErrorLabel("Your document is expired!");
				}
			}
			selectedLists.clear();
		}
	}
	
	/**
	 * Gestisce il click del button Load: carica dal DB e mostra le proprieta' aggiornate delle liste create
	 */
	private class LoadBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.getListsAsString();
		}
	}
	
	/**
	 * Pulisce il DB delle liste create
	 */
	private class ClearBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.clearDBLists();
		}
	}


	
}