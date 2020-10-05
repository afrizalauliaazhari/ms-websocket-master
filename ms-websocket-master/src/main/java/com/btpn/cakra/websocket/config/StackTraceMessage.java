package com.btpn.cakra.websocket.config;

import java.io.*;

public final class StackTraceMessage {

	StackTraceMessage() {
	}

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}
}
