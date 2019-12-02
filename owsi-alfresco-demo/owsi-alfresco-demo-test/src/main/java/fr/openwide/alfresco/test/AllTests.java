package fr.openwide.alfresco.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	ClassificationTest.class,
})
public class AllTests {

	public static void main(String[] args) {
		JUnitCore.main(AllTests.class.getName());
	}

}
