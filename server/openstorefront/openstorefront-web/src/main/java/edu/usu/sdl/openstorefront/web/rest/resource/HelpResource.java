/*
 * Copyright 2015 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.web.rest.resource;

import edu.usu.sdl.openstorefront.core.annotation.APIDescription;
import edu.usu.sdl.openstorefront.core.annotation.DataType;
import edu.usu.sdl.openstorefront.core.model.HelpSectionAll;
import edu.usu.sdl.openstorefront.security.SecurityUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author dshurtleff
 */
@Path("v1/resource/help")
@APIDescription("This allows access to help documentation")
public class HelpResource
		extends BaseResource
{

	@GET
	@APIDescription("Gets all help sections available for a user")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(HelpSectionAll.class)
	public Response getAllHelp()
	{
		Boolean adminHelp = Boolean.FALSE;
		if (SecurityUtil.isEntryAdminUser()) {
			adminHelp = null;
		}
		HelpSectionAll helpSectionAll = service.getSystemService().getAllHelp(adminHelp);
		return sendSingleEntityResponse(helpSectionAll);
	}

}
