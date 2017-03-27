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
package edu.usu.sdl.openstorefront.doc.model;

import edu.usu.sdl.openstorefront.core.view.LookupModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dshurtleff
 */
public class ServiceClassModel
		implements Serializable
{

	private List<LookupModel> resourceClasses = new ArrayList<>();
	private List<LookupModel> serviceClasses = new ArrayList<>();

	public ServiceClassModel()
	{
	}

	public List<LookupModel> getResourceClasses()
	{
		return resourceClasses;
	}

	public void setResourceClasses(List<LookupModel> resourceClasses)
	{
		this.resourceClasses = resourceClasses;
	}

	public List<LookupModel> getServiceClasses()
	{
		return serviceClasses;
	}

	public void setServiceClasses(List<LookupModel> serviceClasses)
	{
		this.serviceClasses = serviceClasses;
	}

}