package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.model.Document;
import com.onlinevotingsystem.client.model.List;
import com.onlinevotingsystem.client.service.ClientImplementor;


public class Profile extends Composite implements Foreground{

	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(2, 2);
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ClientImplementor clientImpl;
	private User sessionUser;
	
	public Profile(ClientImplementor clientImplInput, User sessionUserInput) {
		initWidget(this.vPanel);
		this.clientImpl = clientImplInput;
		this.sessionUser = sessionUserInput;
		VerticalPanel userData=new VerticalPanel();
		
		userData.add(new Label("Name: "+sessionUser.getName()));
		userData.add(new Label("Surname: "+sessionUser.getSurname()));
		userData.add(new Label("Phone: "+sessionUser.getPhone()));
		userData.add(new Label("Email: "+sessionUser.getEmail()));
		userData.add(new Label("CF: "+sessionUser.getCf()));
		userData.add(new Label("Address: "+sessionUser.getAddress()));
		Document doc = sessionUser.getDocument();
		userData.add(new Label("Document details: \n"+
								"\n Body: "+ doc.getBody()+
								"\n Type: "+doc.getType()+
								"\n Number: "+doc.getNumber()+
								"\n Release Date: "+doc.getRDate()+
								"\n Expiry Date: "+doc.getEDate())
				);
		gridPanel.setWidget(0, 0, userData);

		clientImpl.getUserCreatedLists(sessionUser);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-horizontal margin-vertical width-label");
		
		gridPanel.setStyleName("padding-table");
		vPanel.add(gridPanel);
		vPanel.add(successLBL);
		vPanel.add(errorLBL);
		
		infoLBL = new Label("--");
		infoLBL.setStyleName("alert alert-info margin-horizontal");
		vPanel.add(infoLBL);
	}
	
	@Override
	public void onUpdateForeground(User sessionUser) {
		if(!this.sessionUser.equals(sessionUser) || this.sessionUser == null)
			this.sessionUser = sessionUser;
		clientImpl.getUserCreatedLists(sessionUser);
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
	 * Imposta l'elenco delle liste relative all'utente loggato, permettendo di visualizzarne le proprieta
	 * @param listTot		insieme di liste
	 */
	public void setListsList(ArrayList<List> listTot) {
		VerticalPanel userLists=new VerticalPanel();
		if(listTot.size() == 0) {
			userLists.add(new Label("There aren't created lists!"));
		} else {
			Label lists = new Label("Here are your lists: ");
			lists.setStyleName("bold");
			userLists.add(lists);
			String status;
			for(List list : listTot) {
				userLists.add(new Label("*****************"));
				userLists.add(new Label("Name: " + list.getName()));
				userLists.add(new Label("Symbol: " + list.getSymbol()));
				userLists.add(new Label("Mayor: " + list.getMayor().getName() + " " + list.getMayor().getSurname()));
				userLists.add(new Label("Number of Members (mayor excluded): " + (list.getMembers().size() > 0 ? list.getMembers().size() - 1 : 0)));
				if(list.getStatus() == 0)
					status = "PENDING";
				else if(list.getStatus() == 1)
					status = "APPROVED";
				else
					status = "REJECTED";
				userLists.add(new Label("Status: "+status));
			}
		}
		gridPanel.setWidget(1, 0, userLists);
		updateInfoLabel(listTot.toString());
	}
	
}
