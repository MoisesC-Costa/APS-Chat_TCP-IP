package br.unip.si.aps.moises.core.bus.actions;

import br.unip.si.aps.moises.core.dto.Announce;
import br.unip.si.aps.moises.core.network.NetworkProxy;
import br.unip.si.aps.moises.observer.event.MessageEvent;

public class AnnounceAction implements Action {
	
	/*
	 * Singleton
	 */
	private static AnnounceAction instance;
	
	private AnnounceAction() {
		proxy = NetworkProxy.getInstance();
	}
	public static synchronized AnnounceAction getInstance() {
		return instance == null ? (instance = new AnnounceAction()) : instance;
	}
	
	/*
	 * Metodos e Atributos
	 */
	private NetworkProxy proxy;
	
	@Override
	public void triggerAction(Object object) {
		Announce announce = (Announce) object;
		proxy.onMessage(new MessageEvent(null, announce.getJson()));
	}

}
