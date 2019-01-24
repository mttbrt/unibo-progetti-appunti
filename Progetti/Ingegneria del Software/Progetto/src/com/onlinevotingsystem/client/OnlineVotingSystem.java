package com.onlinevotingsystem.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.onlinevotingsystem.client.service.ClientImplementor;

public class OnlineVotingSystem implements EntryPoint {
	
	public void onModuleLoad() {
		ClientImplementor clientImpl = new ClientImplementor(GWT.getModuleBaseURL() + "standardservice");
		RootPanel.get().add(clientImpl.getMainGUI());
	}
	
}
