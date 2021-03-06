package br.unip.si.aps.moises.bus;

import java.lang.reflect.*;
import java.util.*;

import br.unip.si.aps.moises.bus.actions.*;
import br.unip.si.aps.moises.bus.services.*;
import br.unip.si.aps.moises.network.domain.NetworkProxy;
import br.unip.si.aps.moises.observer.action.MessageAction;
import br.unip.si.aps.moises.observer.listener.MessageListener;
import br.unip.si.aps.moises.util.JSONMessageUtil;
import lombok.NonNull;

public class Coreographer implements MessageListener {
	/*
	 * Singleton
	 */
	private static Coreographer instance;
		
	private Coreographer() {
		this.register = RegisterService.getInstance();
		this.send = SendService.getInstance();
		this.notifyClosedUser = NotifyClosedUserAction.getInstance();
	}
	
	public static synchronized Coreographer getInstance() {
		return instance == null ? (instance = new Coreographer()) : instance;
	}
	/*
	 * Atributos e Metodos
	 */
	private Service register;
	private Service send;
	private Action notifyClosedUser;
	
	@Override
	public void onMessage(@NonNull MessageAction action) {
		this.execService(action);
	}
	
	public void execService(@NonNull MessageAction action) {
		Method method = getMethod(action.getMessage().getJSONObject("header").getString("method"));
		if(method != null) {
			try {
				method.invoke(this, action);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				((NetworkProxy) action.getSource()).onMessage(new MessageAction(null, JSONMessageUtil.getMessageErro("Mensagem Invalida!")));				
			}
		}else
			((NetworkProxy) action.getSource()).onMessage(new MessageAction(null, JSONMessageUtil.getMessageErro("Metodo não reconhecido")));
	}

	private Method getMethod(@NonNull String methodName) {
		try {
			return Coreographer.class.getMethod(methodName, MessageAction.class);
		} catch (Exception e) {
			return null;
		}
	}	

	/*
	 * Serviços 
	 */
	public void register(@NonNull MessageAction action) {
		var data = new HashMap<String, Object>();
		var json = action.getMessage().getJSONObject("header");
		data.put("proxy", action.getSource());
		data.put("target", json.getString("from"));
		data.put("id", json.getString("id"));
		
		register.exec(data);
	}
	
	public void send(@NonNull MessageAction action) {
		var data = new HashMap<String, Object>();
		var json = action.getMessage().getJSONObject("header");

		data.put("proxy", action.getSource());
		data.put("target", json.getString("target"));
		data.put("message", action.getMessage());
		
		send.exec(data);
	}
	
	/*
	 * Ações
	 */
	public void notifyClosedUser(@NonNull List<String> idList) {
		Map<String, Object> data = new HashMap<>();
		
		idList.forEach(id -> {
			data.put("message", JSONMessageUtil.getMessageNotifyClosedUser(id));
			notifyClosedUser.triggerAction(data);
		});
	}

}
