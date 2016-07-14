package com.redhat.xpaas.qe;

import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CompleteTask {
	private static final String ENDPOINT = "http://kie-app-ldap-demo.apps.latest.xpaas/kie-server/services/rest/server";

	public static void main(final String[] args) throws InterruptedException {
		final String username = args[0];
		final String password = args[1];
		final KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(ENDPOINT, username, password);

		final KieServicesClient client = KieServicesFactory.newKieServicesClient(config);

		final UserTaskServicesClient userTaskClient = client.getServicesClient(UserTaskServicesClient.class);
		final ProcessServicesClient processClient = client.getServicesClient(ProcessServicesClient.class);

		final List<TaskSummary> tasks = userTaskClient.findTasksAssignedAsPotentialOwner(
				username, 0, 50);

		System.out.println(String.format("Available tasks for user '%s': %s",
				username, tasks));

		final TaskSummary task = tasks.iterator().next();

		userTaskClient.claimTask(StartProcess.CONTAINER_ID, task.getId(), username);
		System.out.println(String.format("%s claimed by user %s", task.getName(), username));

		userTaskClient.startTask(StartProcess.CONTAINER_ID, task.getId(), username);
		System.out.println(String.format("%s started by user %s", task.getName(), username));

		userTaskClient.completeTask(StartProcess.CONTAINER_ID, task.getId(), username, new HashMap<>());
		System.out.println(String.format("%s completed by user %s", task.getName(), username));

		final ProcessInstance processInstance = processClient.getProcessInstance(
				StartProcess.CONTAINER_ID, task.getProcessInstanceId());

		if (userTaskClient.findTasksByStatusByProcessInstanceId(
				processInstance.getId(),
				Arrays.asList(new String[] {"Ready"}), 0, 50).isEmpty()) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			while (true) {
				System.out.println("\nTimer: "
						+ LocalDateTime.now().format(formatter)
						+ " Checking process instance state...");
				try {
					while (!processClient
							.getProcessInstance(StartProcess.CONTAINER_ID,
									task.getProcessInstanceId()).getState()
							.equals(Integer.valueOf(2))) {
						System.out
								.println("Process instance is not completed.");
						Thread.sleep(5_000);
						System.out.println("\nTimer: "
								+ LocalDateTime.now().format(formatter)
								+ " Checking process instance state...");
					}
					System.out.println("Process instance is completed");
					System.exit(0);
				} catch (final Exception e) {
					System.out.println("Checking process state failed.");
					Thread.sleep(5_000);
				}
			}
		}

	}
}
