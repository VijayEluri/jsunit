package net.jsunit;

import junit.framework.TestCase;
import net.jsunit.configuration.Configuration;
import net.sourceforge.jwebunit.WebTester;
import com.meterware.httpunit.HttpUnitOptions;

public class FarmServerLandingPageFunctionalTest extends TestCase {

    static {
        HttpUnitOptions.setScriptingEnabled(false);
    }

    private WebTester webTester;
    private JsUnitFarmServer server;

    protected void setUp() throws Exception {
        super.setUp();
        Configuration configuration = new Configuration(new FunctionalTestConfigurationSource(9090));
        server = new JsUnitFarmServer(configuration);
        server.start();
        webTester = new WebTester();
        webTester.getTestContext().setBaseUrl("http://localhost:9090/jsunit");
    }

    protected void tearDown() throws Exception {
        server.dispose();
        super.tearDown();
    }

    public void testSimple() throws Exception {
        webTester.beginAt("/");
        assertOnLandingPage();
    }

    private void assertOnLandingPage() {
        webTester.assertTitleEquals("JsUnit  Farm Server");
    }

}
