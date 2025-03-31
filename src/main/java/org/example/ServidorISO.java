package org.example;
import java.io.IOException;

import org.jpos.iso.BaseChannel;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.channel.ASCIIChannel;

import org.jpos.iso.packager.GenericPackager;

public class ServidorISO implements ISORequestListener {

	public static void main(String[] args) throws ISOException {
		String hostname = "localhost";
		int portNumber = 5000;

		ISOPackager packager = new GenericPackager("CustomConfig.xml");
		ServerChannel channel = new ASCIIChannel(hostname, portNumber, packager);

		ISOServer server = new ISOServer(portNumber, channel, null);

		server.addISORequestListener(new ServidorISO());

		System.out.println("Servidor ISO8583 iniciado...");
		new Thread(server).start();

	}

	public boolean process(ISOSource isoSrc, ISOMsg isoMsg) {
		try {
			System.out.println("Mensagem de entrada ISO8583 ["
					+ ((BaseChannel) isoSrc).getSocket().getInetAddress()
					.getHostAddress() + "]");
			receiveMessage(isoSrc, isoMsg);
			logISOMsg(isoMsg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	private void receiveMessage(ISOSource isoSrc, ISOMsg isoMsg)
			throws ISOException, IOException {
		System.out.println("Mensagem ISO8583 recebida...");
		ISOMsg reply = (ISOMsg) isoMsg.clone();

		reply.setMTI("0210");
		reply.set(39, "00");

		isoSrc.send(reply);
	}

	private static void logISOMsg(ISOMsg msg) {
		System.out.println("----MENSAGEM ISO-----");
		try {
			System.out.println("  MTI : " + msg.getMTI());
			for (int i = 1; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					System.out.println("    Field-" + i + " : "
							+ msg.getString(i));
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("--------------------");
		}

	}

}