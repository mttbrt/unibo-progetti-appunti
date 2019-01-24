package com.onlinevotingsystem.client.gui;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.onlinevotingsystem.client.model.User;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class Login extends Composite implements Foreground {
	
	private VerticalPanel vPanel = new VerticalPanel();
	private Grid gridPanel = new Grid(3, 2);
	
	private TextBox nicknameTB;
	private PasswordTextBox passwordTB;
	
	private Label errorLBL;
	private Label successLBL;
	private Label infoLBL;
	
	private ClientImplementor clientImpl;
	
	public Login(ClientImplementor clientImpl) {
		initWidget(this.vPanel);
		this.clientImpl = clientImpl;
		
		// Text boxes
		Label nickname = new Label("Nickname: ");
		nicknameTB = new TextBox();
		nicknameTB.setStyleName("form-control");
		nicknameTB.getElement().setPropertyString("placeholder", "Nickname");
		gridPanel.setWidget(0, 0, nickname);
		gridPanel.setWidget(0, 1, nicknameTB);
		
		Label password = new Label("Password: ");
		passwordTB = new PasswordTextBox();
		passwordTB.setStyleName("form-control");
		passwordTB.getElement().setPropertyString("placeholder", "Password");
		gridPanel.setWidget(1, 0, password);
		gridPanel.setWidget(1, 1, passwordTB);
		
		// Labels
		errorLBL = new Label("");
		errorLBL.setStylePrimaryName("error-label");
		errorLBL.setStyleName("alert alert-danger margin-horizontal width-label");
		successLBL = new Label("");
		successLBL.setStylePrimaryName("success-label");
		successLBL.setStyleName("alert alert-success margin-horizontal margin-vertical width-label");
		
		// Buttons
		Button loginBTN = new Button("Login");
		loginBTN.setStyleName("btn btn-success");
		loginBTN.addClickHandler(new LoginBTNClickHandler());
		gridPanel.setWidget(2, 0, loginBTN);
		
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
		nicknameTB.setText("");
		passwordTB.setText("");
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
	 * Gestisce il click del button Login: esegue l'accesso dell'utente
	 */
	private class LoginBTNClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			resetLabels();
			
			String nickname = nicknameTB.getText().trim();
			String password = passwordTB.getText().trim();
			
			String errorMsg = "";
			
			ArrayList<String> emptyFields = new ArrayList<>();
			if(nickname.equals(""))
				emptyFields.add("nickname");
			if(password.equals(""))
				emptyFields.add("password");
			
			if(emptyFields.size() > 0) {
				errorMsg += "The following fields must be filled: ";
				for(String field : emptyFields)
					errorMsg += field + " ";
			} else if((password.length() < 6)&&(nickname!="admin")) {
				errorMsg += "Password length must be at least 6 characters.";
			} else {
				clientImpl.login(nickname, password);
			}
			updateErrorLabel(errorMsg);
		}
	}
	
}

