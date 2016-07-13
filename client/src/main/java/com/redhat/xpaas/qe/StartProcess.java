package com.redhat.xpaas.qe;

import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;

public class StartProcess {
	private static final String ENDPOINT = "http://kie-app-ldap-demo.apps.latest.xpaas/kie-server/services/rest/server";
	private static final String USERNAME = "kieserver";
	private static final String PASSWORD = "kieserver1!";
	static final String CONTAINER_ID = "LDAPTimer";

	public static void main(String[] args) {
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(ENDPOINT, USERNAME, PASSWORD);

		KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

		final ProcessServicesClient processServicesClient = client.getServicesClient(ProcessServicesClient.class);

		Long processInstanceId = processServicesClient.startProcess(CONTAINER_ID, "LDAPTimer.MultiuserProcess");

		System.out.println(String.format("Process instance with id %s started.", processInstanceId));
	}
}
