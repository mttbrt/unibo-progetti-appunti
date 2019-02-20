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
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Registration extends Composite implements Foreground {
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(15, 2);
	
	private TextBox nicknameTB;
	private TextBox nameTB;
	private TextBox surnameTB;
	private TextBox phoneTB;
	private PasswordTextBox passwordTB;
	private TextBox emailTB;
	private TextBox cfTB;
	private TextBox addressTB;
	private ListBox typeLB;
	private TextBox numberTB;
	private TextBox bodyTB;
	private DatePicker rDateDP;
	private DatePicker eDateDP;
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ClientImplementor clientImpl;
	
	public Registration(ClientImplementor clientImpl) {
		initWidget(this.vPanel);
		this.clientImpl = clientImpl;
		
		// Text boxes
		Label lbl1 = new Label("Nickname: ");
		nicknameTB = new TextBox();
		nicknameTB.setStyleName("form-control");
		nicknameTB.getElement().setPropertyString("placeholder", "Nickname");
		gridPanel.setWidget(0, 0, lbl1);
		gridPanel.setWidget(0, 1, nicknameTB);
		
		Label lbl2 = new Label("Name: ");
		nameTB = new TextBox();
		nameTB.setStyleName("form-control");
		nameTB.getElement().setPropertyString("placeholder", "Name");
		gridPanel.setWidget(1, 0, lbl2);
		gridPanel.setWidget(1, 1, nameTB);
						
		Label lbl3 = new Label("Surname: ");
		surnameTB = new TextBox();
		surnameTB.setStyleName("form-control");
		surnameTB.getElement().setPropertyString("placeholder", "Surname");
		gridPanel.setWidget(2, 0, lbl3);
		gridPanel.setWidget(2, 1, surnameTB);
		
		Label lbl4 = new Label("Phone: ");
		phoneTB = new TextBox();
		phoneTB.setStyleName("form-control");
		phoneTB.getElement().setPropertyString("placeholder", "Phone number");
		gridPanel.setWidget(3, 0, lbl4);
		gridPanel.setWidget(3, 1, phoneTB);
		
		Label lbl5 = new Label("Password: ");
		passwordTB = new PasswordTextBox();
		passwordTB.setStyleName("form-control");
		passwordTB.getElement().setPropertyString("placeholder", "Password");
		gridPanel.setWidget(4, 0, lbl5);
		gridPanel.setWidget(4, 1, passwordTB);
		
		Label lbl6 = new Label("Email: ");
		emailTB = new TextBox();
		emailTB.setStyleName("form-control");
		emailTB.getElement().setPropertyString("placeholder", "Email");
		gridPanel.setWidget(5, 0, lbl6);
		gridPanel.setWidget(5, 1, emailTB);
		
		Label lbl7 = new Label("Fiscal code: ");
		cfTB = new TextBox();
		cfTB.setStyleName("form-control");
		cfTB.getElement().setPropertyString("placeholder", "Fiscal code");
		gridPanel.setWidget(6, 0, lbl7);
		gridPanel.setWidget(6, 1, cfTB);
		
		Label lbl8 = new Label("Address: ");
		addressTB = new TextBox();
		addressTB.setStyleName("form-control");
		addressTB.getElement().setPropertyString("placeholder", "Address");
		gridPanel.setWidget(7, 0, lbl8);
		gridPanel.setWidget(7, 1, addressTB);
		
		Label lbl9 = new Label("Document details:");
		lbl9.setStyleName("bold");
		gridPanel.setWidget(8, 0, lbl9);
		
		Label lbl10 = new Label("Type: ");
		typeLB = new ListBox();
		typeLB.setStyleName("form-control");
		typeLB.addItem("ID card");
		typeLB.addItem("Passport");
		gridPanel.setWidget(9, 0, lbl10);
		gridPanel.setWidget(9, 1, typeLB);
		
		Label lbl11 = new Label("Number: ");
		numberTB = new TextBox();
		numberTB.setStyleName("form-control");
		numberTB.getElement().setPropertyString("placeholder", "Number");
		gridPanel.setWidget(10, 0, lbl11);
		gridPanel.setWidget(10, 1, numberTB);
		
		Label lbl12 = new Label("Body");
		bodyTB = new TextBox();
		bodyTB.setStyleName("form-control");
		bodyTB.getElement().setPropertyString("placeholder", "Body");
		gridPanel.setWidget(11, 0, lbl12);
		gridPanel.setWidget(11, 1, bodyTB);
		
		Label lbl13 = new Label("Release Date");
		rDateDP = new DatePicker();
		rDateDP.setStyleName("form-control");
		rDateDP.setValue(new Date(), true);
		gridPanel.setWidget(12, 0, lbl13);
		gridPanel.setWidget(12, 1, rDateDP);
		
		Label lbl14 = new Label("Expiration Date");
		eDateDP = new DatePicker();
		eDateDP.setStyleName("form-control");
		eDateDP.setValue(new Date(), true);
		gridPanel.setWidget(13, 0, lbl14);
		gridPanel.setWidget(13, 1, eDateDP);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-horizontal margin-vertical width-label");
		
		// Buttons
		Button saveBTN = new Button("Sign in");
		saveBTN.setStyleName("btn btn-success");
		saveBTN.addClickHandler(new SaveBTNClickHandler());
		gridPanel.setWidget(14, 0, saveBTN);
		
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
		nicknameTB.setText("");
		nameTB.setText("");
		surnameTB.setText("");
		phoneTB.setText("");
		passwordTB.setText("");
		emailTB.setText("");
		cfTB.setText("");
		addressTB.setText("");
		numberTB.setText("");
		bodyTB.setText("");
		typeLB.setSelectedIndex(0);
		rDateDP.setValue(new Date());
		eDateDP.setValue(new Date());
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
	 * Gestisce il click del button Save: dopo una serie di controlli, permette la registrazione dell'utente base al sito
	 *
	 */
	private class SaveBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			Date today = new Date();
			
			String nickname = nicknameTB.getText().trim();
			String name = nameTB.getText().trim();
			String surname = surnameTB.getText().trim();
			String phone = phoneTB.getText().trim();
			String password = passwordTB.getText().trim();
			String email = emailTB.getText().trim();
			String cf = cfTB.getText().trim();
			String address = addressTB.getText().trim();
			String number = numberTB.getText().trim();
			String body = bodyTB.getText().trim();
			String type = typeLB.getSelectedItemText();
			Date rDate = (Date) rDateDP.getValue();
			Date eDate = (Date) eDateDP.getValue();
			
			String errorMsg = "";
			
			ArrayList<String> emptyFields = new ArrayList<>();
			if(nickname.equals(""))
				emptyFields.add("nickname");
			if(name.equals(""))
				emptyFields.add("name");
			if(surname.equals(""))
				emptyFields.add("surname");
			if(phone.equals(""))
				emptyFields.add("phone");
			if(password.equals(""))
				emptyFields.add("password");
			if(email.equals(""))
				emptyFields.add("email");
			if(cf.equals(""))
				emptyFields.add("cf");
			if(address.equals(""))
				emptyFields.add("address");
			if(type.equals(""))
				emptyFields.add("document-type");
			if(number.equals(""))
				emptyFields.add("document-number");
			if(body.equals(""))
				emptyFields.add("document-body");
			if(rDate.equals(""))
				emptyFields.add("document-releasedate");
			if(eDate.equals(""))
				emptyFields.add("document-expirationdate");
			
			if(emptyFields.size() > 0) {
				errorMsg += "The following fields must be filled: ";
				for(String field : emptyFields)
					errorMsg += field + " ";
			} else if(password.length() < 6) {
				errorMsg += "Password length must be at least 6 characters.";
			} else if(cf.length() != 16) {
				errorMsg += "Fiscal code length must be 16 characters.";
			} else if(!phone.matches("[0-9]+")) {
				errorMsg += "Phone number must contain only numbers.";
			} else if(!email.contains("@") || !email.contains(".") || email.length() < 8) {
				errorMsg += "Email format not valid.";
			} else if(rDate.after(today)) {
				errorMsg += "Document release date must be before today";
			} else if(eDate.before(today)) {
				errorMsg += "Document expiration date must be after today";
			} else {
				User user = new User(nickname, name, surname, phone, password, email, cf, address, type, number, body, rDate, eDate);
				clientImpl.registerUser(user);
			}
			updateErrorLabel(errorMsg);
		}
	}
	
	/**
	 * Gestisce il click del button Load: carica dal DB e mostra le proprieta' aggiornate degli utenti registrati
	 */
	private class LoadBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.getUsersAsString();
		}
	}
	
	/**
	 * Pulisce il DB degli utenti registrati
	 */
	private class ClearBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			clientImpl.clearDBUsers();
		}
	}
	
}

