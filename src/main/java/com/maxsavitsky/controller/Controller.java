package com.maxsavitsky.controller;

import com.maxsavitsky.Main;
import com.maxsavteam.ciconia.annotation.Component;
import com.maxsavteam.ciconia.annotation.Mapping;

@Mapping
@Component
public class Controller {

	@Mapping("/messages-broadcasting-port")
	public int getPortForMessagesBroadcasting(){
		return Main.getProgramArguments().getMessagesBroadcastingPort();
	}

}
