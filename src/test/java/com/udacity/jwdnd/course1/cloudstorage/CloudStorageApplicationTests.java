package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1400,1200");
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    void getLoginPage() {
        driver.get(getBaseUrl() + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    /**
     * PLEASE DO NOT DELETE THIS method.
     * Helper method for Udacity-supplied sanity checks.
     **/
    private void doMockSignUp(String firstName, String lastName, String userName, String password) {
        driver.get(getBaseUrl() + "/signup");
        wait.until(ExpectedConditions.titleContains("Sign Up"));

        type(By.id("inputFirstName"), firstName);
        type(By.id("inputLastName"), lastName);
        type(By.id("inputUsername"), userName);
        type(By.id("inputPassword"), password);
        click(By.id("buttonSignUp"));

        wait.until(ExpectedConditions.urlToBe(getBaseUrl() + "/login?signupSuccess"));
        Assertions.assertTrue(driver.getPageSource().contains("You successfully signed up!"));
    }

    /**
     * PLEASE DO NOT DELETE THIS method.
     * Helper method for Udacity-supplied sanity checks.
     **/
    private void doLogIn(String userName, String password) {
        driver.get(getBaseUrl() + "/login");
        type(By.id("inputUsername"), userName);
        type(By.id("inputPassword"), password);
        click(By.id("login-button"));
        wait.until(ExpectedConditions.titleContains("Home"));
    }

    @Test
    void testUnauthorizedAccessRestrictions() {
        driver.get(getBaseUrl() + "/login");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get(getBaseUrl() + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        driver.get(getBaseUrl() + "/home");
        wait.until(ExpectedConditions.titleContains("Login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));

        driver.get(getBaseUrl() + "/note");
        wait.until(ExpectedConditions.titleContains("Login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void testSignupLoginLogoutFlow() {
        String username = unique("user");
        doMockSignUp("First", "Last", username, "password");

        doLogIn(username, "password");
        Assertions.assertEquals("Home", driver.getTitle());

        click(By.id("logout-button"));
        wait.until(ExpectedConditions.titleContains("Login"));

        driver.get(getBaseUrl() + "/home");
        wait.until(ExpectedConditions.titleContains("Login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void testNoteCrud() {
        String username = unique("note");
        doMockSignUp("Note", "Tester", username, "password");
        doLogIn(username, "password");

        openNotesTab();
        clickAddNoteButton();
        type(By.id("note-title"), "First Note");
        type(By.id("note-description"), "Initial Description");
        submitNoteModal();
        goHome("notes");

        openNotesTab();
        waitForText(By.id("userTable"), "First Note");
        waitForText(By.id("userTable"), "Initial Description");

        clickFirstNoteEditButton();
        Assertions.assertEquals("First Note", driver.findElement(By.id("note-title")).getAttribute("value"));
        Assertions.assertEquals("Initial Description", driver.findElement(By.id("note-description")).getAttribute("value"));
        closeNoteModal();

        clickFirstNoteDeleteButton();
        goHome("notes");

        openNotesTab();
        Assertions.assertFalse(driver.findElement(By.id("userTable")).getText().contains("First Note"));
    }

    @Test
    void testCredentialCrud() {
        String username = unique("cred");
        doMockSignUp("Credential", "Tester", username, "password");
        doLogIn(username, "password");

        openCredentialsTab();
        clickAddCredentialButton();
        type(By.id("credential-url"), "https://example.com");
        type(By.id("credential-username"), "example-user");
        type(By.id("credential-password"), "plain-secret");
        submitCredentialModal();
        goHome("credentials");

        openCredentialsTab();
        waitForText(By.id("credentialTable"), "https://example.com");
        waitForText(By.id("credentialTable"), "example-user");
        WebElement credentialTable = driver.findElement(By.id("credentialTable"));
        String tableText = credentialTable.getText();
        Assertions.assertTrue(tableText.contains("https://example.com"));
        Assertions.assertTrue(tableText.contains("example-user"));
        Assertions.assertFalse(tableText.contains("plain-secret"));

        clickFirstCredentialEditButton();
        Assertions.assertEquals("plain-secret", driver.findElement(By.id("credential-password")).getAttribute("value"));
        closeCredentialModal();

        clickFirstCredentialDeleteButton();
        goHome("credentials");

        openCredentialsTab();
        Assertions.assertFalse(driver.findElement(By.id("credentialTable")).getText().contains("example-user"));
    }

    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     *
     * If this test is failing, please ensure that you are handling redirecting users
     * back to the login page after a succesful sign up.
     * Read more about the requirement in the rubric:
     * https://review.udacity.com/#!/rubrics/2724/view
     */
    @Test
    void testRedirection() {
        doMockSignUp("Redirection", "Test", unique("rt"), "123");
        Assertions.assertTrue(driver.getCurrentUrl().startsWith(getBaseUrl() + "/login"));
    }

    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     *
     * If this test is failing, please ensure that you are handling bad URLs
     * gracefully, for example with a custom error page.
     *
     * Read more about custom error pages at:
     * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
     */
    @Test
    void testBadUrl() {
        String username = unique("ut");
        doMockSignUp("URL", "Test", username, "123");
        doLogIn(username, "123");

        driver.get(getBaseUrl() + "/some-random-page");
        Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     *
     * If this test is failing, please ensure that you are handling uploading large files (>1MB),
     * gracefully in your code.
     *
     * Read more about file size limits here:
     * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
     */
    @Test
    void testLargeUpload() {
        String username = unique("lft");
        doMockSignUp("Large", "File", username, "123");
        doLogIn(username, "123");

        WebElement fileSelectButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
        fileSelectButton.sendKeys(new File("upload5m.zip").getAbsolutePath());
        click(By.id("uploadButton"));
        waitForBrowserSettling();
        Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403"));
    }

    private String getBaseUrl() {
        return "http://localhost:" + this.port;
    }

    private String unique(String prefix) {
        String suffix = String.valueOf(System.nanoTime());
        String combined = prefix + suffix;
        return combined.substring(0, Math.min(combined.length(), 20));
    }

    private void type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    private void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    private void jsClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void continueToHome() {
        click(By.id("continue-link"));
        wait.until(ExpectedConditions.titleContains("Home"));
    }

    private void goHome(String tab) {
        driver.get(getBaseUrl() + "/home?tab=" + tab + "&ts=" + System.nanoTime());
        wait.until(ExpectedConditions.titleContains("Home"));
    }

    private void waitForText(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    private void waitForBrowserSettling() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void openNotesTab() {
        jsClick(By.id("nav-notes-tab"));
        wait.until(ExpectedConditions.attributeContains(By.id("nav-notes-tab"), "class", "active"));
    }

    private void openCredentialsTab() {
        jsClick(By.id("nav-credentials-tab"));
        wait.until(ExpectedConditions.attributeContains(By.id("nav-credentials-tab"), "class", "active"));
    }

    private void clickAddNoteButton() {
        jsClick(By.xpath("//button[contains(., '+ Add a New Note')]"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteModal")));
    }

    private void submitNoteModal() {
        click(By.xpath("//div[@id='noteModal']//button[contains(., 'Save changes')]"));
        wait.until(ExpectedConditions.titleContains("Result"));
    }

    private void closeNoteModal() {
        click(By.xpath("//div[@id='noteModal']//button[contains(., 'Close')]"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("noteModal")));
    }

    private void clickFirstNoteEditButton() {
        String noteId = driver.findElement(By.cssSelector("#userTable tbody tr:first-child input[name='noteId']")).getAttribute("value");
        jsClick(By.cssSelector("#userTable tbody tr:first-child button.btn-success"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteModal")));
        ((JavascriptExecutor) driver).executeScript("document.getElementById('note-id').value = arguments[0];", noteId);
    }

    private void clickFirstNoteDeleteButton() {
        jsClick(By.cssSelector("#userTable tbody tr:first-child button.btn-danger"));
        wait.until(ExpectedConditions.titleContains("Result"));
    }

    private void clickAddCredentialButton() {
        jsClick(By.xpath("//button[contains(., '+ Add a New Credential')]"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialModal")));
    }

    private void submitCredentialModal() {
        click(By.xpath("//div[@id='credentialModal']//button[contains(., 'Save changes')]"));
        wait.until(ExpectedConditions.titleContains("Result"));
    }

    private void clickFirstCredentialEditButton() {
        String credentialId = driver.findElement(By.cssSelector("#credentialTable tbody tr:first-child input[name='credentialId']")).getAttribute("value");
        jsClick(By.cssSelector("#credentialTable tbody tr:first-child button.btn-success"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialModal")));
        ((JavascriptExecutor) driver).executeScript("document.getElementById('credential-id').value = arguments[0];", credentialId);
    }

    private void closeCredentialModal() {
        click(By.xpath("//div[@id='credentialModal']//button[contains(., 'Close')]"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("credentialModal")));
    }

    private void clickFirstCredentialDeleteButton() {
        jsClick(By.cssSelector("#credentialTable tbody tr:first-child button.btn-danger"));
        wait.until(ExpectedConditions.titleContains("Result"));
    }
}
