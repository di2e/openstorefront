/*
 * Copyright 2017 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usu.sdl.openstorefront.ui.test.user;

import edu.usu.sdl.openstorefront.common.exception.AttachedReferencesException;
import static edu.usu.sdl.openstorefront.core.entity.ApprovalStatus.PENDING;
import edu.usu.sdl.openstorefront.core.view.ComponentAdminView;
import edu.usu.sdl.openstorefront.selenium.provider.ApplicationProvider;
import edu.usu.sdl.openstorefront.selenium.provider.AttributeProvider;
import edu.usu.sdl.openstorefront.selenium.provider.AuthenticationProvider;
import edu.usu.sdl.openstorefront.selenium.provider.ClientApiProvider;
import edu.usu.sdl.openstorefront.selenium.provider.ComponentProvider;
import edu.usu.sdl.openstorefront.selenium.provider.ComponentTypeProvider;
import edu.usu.sdl.openstorefront.selenium.provider.NotificationEventProvider;
import edu.usu.sdl.openstorefront.selenium.provider.OrganizationProvider;
import edu.usu.sdl.openstorefront.ui.test.BrowserTestBase;
import java.util.List;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author ccummings
 */
public class CreateUserSubmissionIT
		extends BrowserTestBase
{

	private static final Logger LOG = Logger.getLogger(CreateUserSubmissionIT.class.getName());
	private ClientApiProvider provider;
	private AttributeProvider attributeProvider;
	private OrganizationProvider organizationProvider;
	private ComponentProvider componentProvider;
	private ComponentTypeProvider componentTypeProvider;
	private ApplicationProvider appProvider;
	private AuthenticationProvider authProvider;
	private NotificationEventProvider notificationProvider;

	private static final String entryType = "Test Component Type";
	private String entryName = "My First Test Submission";
	private String entryOrganization = "The Singleton Factory";
	private String entryDesc = "Stop sniffing my cookies";
	private String autoApproveVal = "TRUE";
	private String configProperty = "userreview.autoapprove";
	private WebElement nextBtn = null;
	private WebElement submitReviewBtn = null;

	@Before
	public void setupTest() throws InterruptedException
	{
		authProvider = new AuthenticationProvider(properties, webDriverUtil);
		authProvider.login();
		provider = new ClientApiProvider();
		componentTypeProvider = new ComponentTypeProvider(provider.getAPIClient());
		attributeProvider = new AttributeProvider(provider.getAPIClient());
		organizationProvider = new OrganizationProvider(provider.getAPIClient());
		componentProvider = new ComponentProvider(attributeProvider, organizationProvider, componentTypeProvider, provider.getAPIClient());
		appProvider = new ApplicationProvider(provider.getAPIClient());
		appProvider.updateSystemConfigProperty(configProperty, autoApproveVal);
		notificationProvider = new NotificationEventProvider(provider.getAPIClient());

		componentTypeProvider.createComponentType(entryType);
		organizationProvider.createOrganization(entryOrganization);
	}

	@Test
	public void createUserSubmission()
	{
		for (WebDriver driver : webDriverUtil.getDrivers()) {

			fillOutForm(driver, entryName);
		}
	}

	protected void fillOutForm(WebDriver driver, String submissionName)
	{
		WebDriverWait wait = new WebDriverWait(driver, 10);

		webDriverUtil.getPage(driver, "UserTool.action");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main-menu-submissions"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='newSubmissionBtn']"))).click();

		List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".x-toolbar.x-docked.x-toolbar-default.x-docked-bottom a")));
		setButtons(buttons);

		nextBtn.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[name='componentType']"))).click();

		List<WebElement> typeOptions = driver.findElements(By.cssSelector(".x-boundlist.x-boundlist-floating.x-layer.x-boundlist-default.x-border-box li"));
		boolean found = false;
		for (WebElement option : typeOptions) {
			if (option.getText().equals("Test Component Type - test label")) {
				option.click();
				found = true;
				break;
			}
		}
		Assert.assertTrue(found);

		driver.findElement(By.cssSelector("[name='name']")).sendKeys(entryName);
		sleep(100);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-test='organizationInput'] [name='organization']"))).sendKeys(entryOrganization);
		sleep(300);
		((JavascriptExecutor) driver).executeScript("tinyMCE.activeEditor.setContent('" + entryDesc + "')");

		nextBtn.click();
		sleep(2000);
		nextBtn.click();
		sleep(2000);
		driverWait(() -> {
			setButtons(buttons);
			submitReviewBtn.click();
		}, 5);
		sleep(2000);

		ComponentAdminView compView = componentProvider.getComponentByName(entryName);
		Assert.assertEquals(compView.getComponent().getName(), entryName);
		Assert.assertEquals(PENDING, compView.getComponent().getApprovalState());
	}

	protected void setButtons(List<WebElement> buttons)
	{
		for (WebElement btn : buttons) {
			System.out.println(btn.getText());
			if (btn.getText().equals("Next")) {
				nextBtn = btn;
			} else if (btn.getText().equals("Submit For Review")) {
				submitReviewBtn = btn;
			}
		}
	}

	@After
	public void cleanupTest() throws AttachedReferencesException
	{
		componentProvider.registerComponent(componentProvider.getComponentByName(entryName).getComponent());
		componentProvider.cleanup();
		notificationProvider.cleanup();
		provider.clientDisconnect();
	}
}
