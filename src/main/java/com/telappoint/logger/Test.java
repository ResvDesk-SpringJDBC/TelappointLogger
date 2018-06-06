package com.telappoint.logger;

import java.util.HashMap;
import java.util.Map;

public class Test {
	private static Map<String, CustomLogger> map = new HashMap<String, CustomLogger>();
	private static String logLevel = "FATAL";

	public static void main(String[] args) throws Exception {
		String[] incomingReq = "client1".split("\\|");
		for (int i = 0; i < incomingReq.length; i++) {
			CustomLogger coLogger = null;
			if (map.containsKey(incomingReq[i])) {
				coLogger = map.get(incomingReq[i]);
			} else {
				coLogger = new CustomLogger(incomingReq[i], false, "Client:" + incomingReq[i], logLevel);
			}
		
			coLogger.fatal("FATAL :", new NullPointerException("FATAL  error"));
		}

	}
}
