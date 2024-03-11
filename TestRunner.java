	`3package test.SubscriptionApiTest;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {
				"src/main/resources/features/features_API",
		},
		glue={"StepDefinitions"},//the path of the step definition files
		//tags = {"~@Ignore"},
		plugin = {"pretty",
				"html:target/cucumber-reports/cucumber-pretty.html",
				"rerun:target/cucumber-reports/rerun.txt",
				"json:target/cucumber-reports/CucumberTestReport.json"},
		//to generate different type of reporting
		monochrome = true, //display the console o/p in proper readable format
		dryRun = false,//to check the mapping is proper between feature file and step def file
		stepNotifications = true
)
public class TestRunner {
}