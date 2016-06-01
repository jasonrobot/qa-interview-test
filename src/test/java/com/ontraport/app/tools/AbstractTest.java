package com.ontraport.app.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * Abstract superclass for all tests. Holds code related to test startup and shutdown,
 * as well as (eventually) logging in, recovering additional test logs, and taking screenshots
 * of failed tests.
 *
 * @author jason
 * @since 5/24/16
 */
public abstract class AbstractTest extends AbstractBase
{
    private static int errorCount = 0;
    private long UNIQUE = System.nanoTime();

    private String headerString = "OntraportStaging";

    private static String methodName;

    /**
     * Set up the webdriver instance
     */
    protected static void setupDriver () throws IOException
    {
        FirefoxProfile profile = new FirefoxProfile();

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        driver = new FirefoxDriver(capabilities);

        driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT, TimeUnit.SECONDS);
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1920, 1000));

        //FIXME make me a method please
        wait = new WebDriverWait(driver, AbstractBase.DEFAULT_WAIT);
    }

    /**
     * Set up the WebDriver instance. Might want login code to be called eventually too.
     * Really anything that needs to be done before each and every test can be done here.
     */
    @BeforeClass
    public static void setup () throws IOException
    {
        setupDriver();
    }

    @Rule
    public TestRule watcher = new TestWatcher()
    {
        @Override
        protected void starting (Description description)
        {
            methodName = description.getMethodName();
            System.out.println("Starting test: " + methodName);
        }

        @Override
        protected void finished (Description description)
        {
            System.out.println("Finished test: " + methodName);
        }
    };

    @Rule
    public Screenshot screenshotTestRule = new Screenshot();

    class Screenshot implements MethodRule
    {
        @Override
        public Statement apply (final Statement statement, final FrameworkMethod frameworkMethod, final Object o)
        {
            return new Statement()
            {
                @Override
                public void evaluate () throws Throwable
                {
                    try
                    {
                        statement.evaluate();
                    }
                    catch (Throwable t)
                    {
                        takeScreenshot("screenshots/failure-" + methodName + "-" + errorCount++ + ".png");
                        throw t;
                    }
                }
            };
        }
    }

    /**
     * Called after every test method in the class has been executed.
     */
    @AfterClass
    public static void cleanup ()
    {
        driver.quit();
    }

    /**
     * Takes a screenshot of the browser. This supports taking multiple screenshots without the files
     * overwriting each other
     *
     * @param name the base name of the screenshot
     */
    public static void takeScreenshot (String name)
    {
        try
        {
            FileOutputStream out = new FileOutputStream(String.format("screenshots/failure-%s-%d.png", name, errorCount++));
            out.write(driver.getScreenshotAs(OutputType.BYTES));
            out.close();
        }
        catch (Exception e)
        {
            //catch exceptions here so they dont add noise to the test reporting
            System.err.println(String.format("Unable to take screenshot number %d for %s", errorCount, name));
        }
    }
}