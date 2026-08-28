
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

import java.io.InputStream;
import java.util.Properties;

public class PWTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    private boolean headless = false;

    private String appUrl;

    public void setup() throws Exception {

        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "application.properties not found");
            }

            properties.load(input);
        }

        appUrl = properties.getProperty("app.url");

        if (appUrl == null || appUrl.isBlank()) {
            throw new RuntimeException(
                    "app.url is missing in application.properties");
        }

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(headless)
                        .setSlowMo(1000));
        ;

        context = browser.newContext();

        page = context.newPage();
        System.out.println("Browser started");
    }

    @Test
    void openApplication() throws Exception {

        setup();

        // Open application
        page.navigate(appUrl);

        System.out.println("Page opened: " + page.url());

        // -------------------------------------------------
        // Verify application is loaded
        // Instead of page.content().contains("rbc")
        // wait for the Maker button
        // -------------------------------------------------

        Locator makerButton = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Maker"));

        makerButton.waitFor();

        assertTrue(
                makerButton.isVisible(),
                "Maker button is not visible");

        System.out.println("Maker button is visible");

        // -------------------------------------------------
        // MAKER
        // -------------------------------------------------

        makerButton.click();

        // -------------------------------------------------
        // AMOUNT
        // -------------------------------------------------

        Locator amount = page.getByRole(
                AriaRole.TEXTBOX,
                new Page.GetByRoleOptions()
                        .setName("Amount"));

        amount.waitFor();
        amount.fill("800");

        // -------------------------------------------------
        // REASON
        // -------------------------------------------------

        Locator reason = page.getByRole(
                AriaRole.TEXTBOX,
                new Page.GetByRoleOptions()
                        .setName("Reason"));

        reason.waitFor();
        reason.fill("Toxic Ticket");

        // -------------------------------------------------
        // INITIATE
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("initiate"))
                .click();

        // -------------------------------------------------
        // ACCOUNT
        // -------------------------------------------------

        page.locator(
                "#Checke__Accounts__el_txt_1_0").waitFor();

        page.locator(
                "#Checke__Accounts__el_txt_1_0").click();

        // -------------------------------------------------
        // BENEFICIARY
        // -------------------------------------------------

        page.locator(
                "#Checke__AllBeneficiary__el_txt_1_0").waitFor();

        page.locator(
                "#Checke__AllBeneficiary__el_txt_1_0").click();

        // -------------------------------------------------
        // OK
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Ok"))
                .click();

        // -------------------------------------------------
        // HISTORY
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("history"))
                .click();

        // -------------------------------------------------
        // ALL BENEFICIARY
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("All Beneficiary"))
                .click();

        // -------------------------------------------------
        // CREATE BENEFICIARY
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("create Beneficiary"))
                .click();

        // -------------------------------------------------
        // GO
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Go"))
                .click();

        // -------------------------------------------------
        // OK
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Ok"))
                .click();

        // -------------------------------------------------
        // ACCOUNTS
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Accounts"))
                .click();

        // -------------------------------------------------
        // CHECKER
        // -------------------------------------------------
        page.navigate(appUrl);

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Checker"))
                .click();

        // -------------------------------------------------
        // PENDING
        // -------------------------------------------------

        page.locator(
                "#Checke__Pending__el_txt_2_3").waitFor();

        page.locator(
                "#Checke__Pending__el_txt_2_3").click();

        // -------------------------------------------------
        // OK
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Ok"))
                .click();

        // -------------------------------------------------
        // REJECTION REASON
        // -------------------------------------------------

        Locator rejectionReason = page.locator(
                "#Checke__Reject__i__Checke__Reject_Req__rejectionReason_0");

        rejectionReason.waitFor();

        rejectionReason.fill("Not Possible");

        // -------------------------------------------------
        // PENDING
        // -------------------------------------------------

        page.locator(
                "#Checke__Pending__el_txt_1_0").waitFor();

        page.locator(
                "#Checke__Pending__el_txt_1_0").click();

        // -------------------------------------------------
        // OK
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Ok"))
                .click();

        // -------------------------------------------------
        // OPEN APPLICATION AGAIN
        // -------------------------------------------------

        page.navigate(appUrl);

        // -------------------------------------------------
        // MAKER AGAIN
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Maker"))
                .waitFor();

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Maker"))
                .click();

        // -------------------------------------------------
        // HISTORY AGAIN
        // -------------------------------------------------

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("history"))
                .click();

        System.out.println("--------------------------------------");
        System.out.println("TEST PASSED");
        System.out.println("--------------------------------------");

        // -------------------------------------------------
        // CLOSE
        // -------------------------------------------------

        browser.close();
        playwright.close();
    }
}
