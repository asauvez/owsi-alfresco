package fr.openwide.alfresco.test.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public abstract class TestsServlet extends HttpServlet {

	protected abstract Class<?> getTestClass();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain");
		try (PrintWriter out = resp.getWriter()) {
			out.println("TestsServlet begin");
			out.flush();
			
			try {
				Result result = new JUnitCore().run(getTestClass());
				
				out.println("RunCount=" + result.getRunCount());
				out.println("Successful=" + result.wasSuccessful());
				out.println("IgnoreCount=" + result.getIgnoreCount());
				if (result.getFailureCount() == 0) {
					out.println("==> Test OK");
				} else {
					out.println("FailureCount=" + result.getFailureCount());
					for (Failure failure : result.getFailures()) {
						out.println();
						out.println("TestHeader=" + failure.getTestHeader());
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
