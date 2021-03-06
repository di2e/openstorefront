/*
 * Copyright 2016 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.web.test.branding;

import edu.usu.sdl.openstorefront.core.entity.Branding;
import edu.usu.sdl.openstorefront.web.test.BaseTestCase;

/**
 *
 * @author ccummings
 */
public class BrandingServiceTest extends BaseTestCase
{

	private Branding brandingTest1 = null;
	private Branding brandingTest2 = null;

	@Override
	protected void runInternalTest()
	{
		brandingTest1 = new Branding();
		brandingTest1.setName("test_branding_1");
		brandingTest1 = service.getBrandingService().saveBranding(brandingTest1);
		service.getBrandingService().setBrandingAsCurrent(brandingTest1.getBrandingId());

		brandingTest2 = new Branding();
		brandingTest2.setName("test_branding_2");
		brandingTest2 = service.getBrandingService().saveBranding(brandingTest2);

		// Check to make sure brandingTest1 is still current branding
		Branding brandingCheck = service.getBrandingService().getCurrentBrandingView();
		if (brandingCheck.getBrandingId().equals(brandingTest1.getBrandingId())) {
			results.append("Current branding:  brandingTest1<br>");
		} else {
			failureReason.append("Current branding has unexpectedly changed<br>");
		}

		service.getBrandingService().setBrandingAsCurrent(brandingTest2.getBrandingId());

		brandingTest2 = lookupBranding(brandingTest2.getBrandingId());
		brandingTest1 = lookupBranding(brandingTest1.getBrandingId());

		brandingCheck = service.getBrandingService().getCurrentBrandingView();
		results.append("Changing current branding...<br>");
		if (brandingCheck.getBrandingId().equals(brandingTest2.getBrandingId())) {
			results.append("Current branding:  brandingTest2<br><br>");
		} else {
			failureReason.append("Change current branding:  Test Failed - unable to change branding<br><br>");
		}
	}

	@Override
	protected void cleanupTest()
	{
		super.cleanupTest();
		service.getBrandingService().setBrandingAsCurrent(null);
		if (brandingTest1 != null) {
			service.getBrandingService().deleteBranding(brandingTest1.getBrandingId());
		}
		if (brandingTest2 != null) {
			service.getBrandingService().deleteBranding(brandingTest2.getBrandingId());
		}
	}

	public Branding lookupBranding(String brandingId)
	{
		Branding branding = service.getPersistenceService().findById(Branding.class, brandingId);

		return branding;
	}

	@Override
	public String getDescription()
	{
		return "Branding Test";
	}
}
