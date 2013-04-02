package org.universAAL.ltba.pir;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.Timer;

import es.tsb.ltba.nomhad.gateway.NomhadGateway;

public class ShoppingController implements ActionListener{

	private int times;
	private static ShoppingController INSTANCE;
	private Timer t;

	private ShoppingController()  {
		super();
		INSTANCE = this;
		t = new Timer(24 * 60 * 60 * 1000, this);
		Calendar today = new GregorianCalendar();
		Calendar startTime = new GregorianCalendar(today.get(Calendar.YEAR),
				today.get(Calendar.MONTH), today.get(Calendar.DATE), 12, 00);
		t.setInitialDelay((int) (startTime.getTimeInMillis() - System
				.currentTimeMillis()));
		t.start();
	}

	public static ShoppingController getInstance() {
		if (INSTANCE == null)
			return new ShoppingController();
		else
			return INSTANCE;
	}

	public int addBackFromShopping() {
		times++;
		return times;
	}

	public void actionPerformed(ActionEvent e) {
		System.out.println("Sending to Nomhad awakening times>" + times );
		NomhadGateway.getInstance().putMeasurement("192.168.238.40", "A100",
				"123456", "ACTIVITIES", "AWAKENING_COUNT", new String("" + times));
		times = 0;
	}
	
}