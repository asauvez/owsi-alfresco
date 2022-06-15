package fr.openwide.alfresco.test.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

public abstract class TestsServlet extends HttpServlet {

	private static final long serialVersionUID = -8737085839531539431L;

	protected abstract Class<?> getTestClass();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		try (PrintWriter out = resp.getWriter()) {
			out.println("TestsServlet begin");
			out.flush();
			
			try {
				LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
						.selectors(DiscoverySelectors.selectClass(getTestClass())).build();
				Launcher launcher = LauncherFactory.create();
				SummaryGeneratingListener listener = new SummaryGeneratingListener();

				launcher.registerTestExecutionListeners(listener);
				launcher.execute(request);

				TestExecutionSummary summary = listener.getSummary();
				List<Failure> failures = summary.getFailures();
				
				System.out.println("getTestsSucceededCount() - " + summary.getTestsSucceededCount());
				failures.forEach(failure -> System.out.println("failure - " + failure.getException()));

				out.println("RunCount=" + summary.getTestsFoundCount());
				out.println("Successful=" + summary.getTestsSucceededCount());
				out.println("Skipped=" + summary.getTestsSkippedCount());
				if (summary.getTestsFailedCount() == 0) {
					out.println("==> Test OK");
				} else {
					out.println("FailureCount=" + summary.getTestsFailedCount());
					for (Failure failure : summary.getFailures()) {
						out.println();
						out.println("TestHeader=" + failure.getTestIdentifier());
						failure.getException().printStackTrace(out);
					}
					out.println("==> Test KO");
				}
			} catch (Exception e) {
				e.printStackTrace(out);
			}
			out.println("TestsServlet finish");
			out.flush();
		}
	}
}
