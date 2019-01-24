package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Results extends Composite implements Foreground {

	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(2, 1);
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ClientImplementor clientImpl;
	
	public Results (ClientImplementor clientImplInput) {
		initWidget(this.vPanel);
		this.clientImpl = clientImplInput;
		Label elRes = new Label("Elections results:");
		elRes.setStyleName("margin-vertical margin-horizontal bold");
		vPanel.add(elRes);
		
		clientImpl.getResults();
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-vertical margin-horizontal width-label");
		
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
		clientImpl.getResults();
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
	 * Imposta l'elenco dei risultati
	 * @param results		risultati elezioni (winners)
	 */
	public void editResults(ArrayList<String> results) {
		VerticalPanel result=new VerticalPanel();
		if(results.size() == 0) {
			result.add(new Label("There aren't elections!"));
		} else {
			result.add(new Label("Here are the election results: "));
			for(String winner: results) {
				result.add(new Label("*****************"));
				result.add(new Label("Election: " + winner));
			}
		}
		gridPanel.setWidget(1, 0, result);
		updateInfoLabel(results.toString());
		
	}
	
}
