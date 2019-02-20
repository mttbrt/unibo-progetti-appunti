package com.onlinevotingsystem.client.gui;

import java.util.Date;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.onlinevotingsystem.client.model.Election;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class ElectionSetup extends Composite implements Foreground {
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(4, 3);
	
	private TextBox subjectTB;
	private DatePicker startDateDP;
	private ListBox startTimeLB;
	private DatePicker endDateDP;
	private ListBox endTimeLB;
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ClientImplementor clientImpl;

	public ElectionSetup(ClientImplementor clientImpl) {
		initWidget(this.vPanel);
		this.clientImpl = clientImpl;
		
		Label lblSubject = new Label("Election subject");
		subjectTB = new TextBox();
		subjectTB.setStyleName("form-control");
		subjectTB.getElement().setPropertyString("placeholder", "Subject");
		gridPanel.setWidget(0, 0, lblSubject);
		gridPanel.setWidget(0, 1, subjectTB);
		
		Label lblStart = new Label("Start Date");
		startDateDP = new DatePicker();
		startDateDP.setStyleName("form-control");
		startDateDP.setValue(new Date(), true);
		gridPanel.setWidget(1, 0, lblStart);
		gridPanel.setWidget(1, 1, startDateDP);
		
		startTimeLB = new ListBox();
		startTimeLB.setStyleName("form-control");
		for(double i = 8; i <= 14; i+=0.5)
			startTimeLB.addItem((int)i + ":" + (i%1 == 0 ? "00" : "30"));
		gridPanel.setWidget(1, 2, startTimeLB);
		
		Label lblEnd = new Label("End Date");
		endDateDP = new DatePicker();
		endDateDP.setStyleName("form-control");
		endDateDP.setValue(new Date(), true);
		gridPanel.setWidget(2, 0, lblEnd);
		gridPanel.setWidget(2, 1, endDateDP);
						
		endTimeLB = new ListBox();
		endTimeLB.setStyleName("form-control");
		for(double i = 17; i <= 23; i+=0.5)
			endTimeLB.addItem((int)i + ":" + (i%1 == 0 ? "00" : "30"));
		gridPanel.setWidget(2, 2, endTimeLB);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-vertical margin-horizontal width-label");
		
		// Buttons
		Button saveBTN = new Button("Save");
		saveBTN.setStyleName("btn btn-success");
		saveBTN.addClickHandler(new SaveBTNClickHandler());
		gridPanel.setWidget(3, 0, saveBTN);
		
		gridPanel.setStyleName("padding-table");
		vPanel.add(gridPanel);
		vPanel.add(successLBL);
		vPanel.add(errorLBL);
		
		Button loadBTN = new Button("Load");
		loadBTN.setStyleName("btn btn-warning margin-horizontal margin-vertical");
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
	
	/**
	 * Ripristina i valori di default
	 */
	private void resetAll() {
		resetLabels();
		subjectTB.setText("");
		startDateDP.setValue(new Date());
		startTimeLB.setSelectedIndex(0);
		endDateDP.setValue(new Date());
		endTimeLB.setSelectedIndex(0);
	}

	@Override
	public void onUpdateForeground(User sessionUser) {
		resetAll();
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
	 * Crea e salva nel DB una nuova elezione
	 */
	private class SaveBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today = new Date();
			DateTimeFormat fmt = DateTimeFormat.getFormat("d");
			
			String subject = subjectTB.getText().trim();
			Date startDateTime = (Date) startDateDP.getValue();
			String startTime = startTimeLB.getSelectedItemText().trim();
			Date endDateTime = (Date) endDateDP.getValue();
			String endTime = endTimeLB.getSelectedItemText().trim();
			
			if(endDateTime.compareTo(startDateTime) >= 0) {
				String day = fmt.format(startDateTime);
				String time = startTime.replace(":", ".");
				String currentTime = String.valueOf(JsDate.create().getHours()) + "." + String.valueOf(JsDate.create().getMinutes());
				
				String[] startTimeSplitted = startTime.split(":");
				startDateTime.setHours(Integer.parseInt(startTimeSplitted[0]) - 2);
				startDateTime.setMinutes(Integer.parseInt(startTimeSplitted[1]));

				String[] endTimeSplitted = endTime.split(":");
				endDateTime.setHours(Integer.parseInt(endTimeSplitted[0]) - 2);
				endDateTime.setMinutes(Integer.parseInt(endTimeSplitted[1]));
				
				if(subject.equals("")) {
					updateErrorLabel("Subject field cannot be empty.");
				} else if (startDateTime.before(today)) {
					if((Integer.parseInt(day)<JsDate.create().getDate()) || (Double.parseDouble(time)<Double.parseDouble(currentTime))) {
						updateErrorLabel("The starting date/time must be after or equals today's date/time");
					} else {
						Election election = new Election(subject, startDateTime, endDateTime);
						clientImpl.registerElection(election);
						clientImpl.getElectionsAsString();
					}
				} else {
					Election election = new Election(subject, startDateTime, endDateTime);
					clientImpl.registerElection(election);
					clientImpl.getElectionsAsString();
				}
			} else 
				updateErrorLabel("The ending date must be after or equals the starting date.");
		}
	}
	
	/**
	 * Gestisce il click del button Load: carica dal DB e mostra le proprieta' aggiornate delle elezioni create
	 */
	private class LoadBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.getElectionsAsString();
		}
	}
	
	/**
	 * Pulisce il DB delle elezioni create
	 */
	private class ClearBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.clearDBElections();
		}
	}
	
}

