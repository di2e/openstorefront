/*
 * Copyright 2019 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.core.api.repo;

import edu.usu.sdl.openstorefront.core.entity.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dshurtleff
 */
public interface ComponentRepo
{

	/**
	 * Finds average rating for All Entries
	 *
	 * @param resultMap
	 * @return
	 */
	public Map<String, Integer> findAverageUserRatingForComponents();

	/**
	 * Groups components by Org (Only Active and Approved) Warning: Component
	 * may not by completely populated
	 *
	 * @return
	 */
	public Map<String, List<Component>> getComponentByOrganization(Set<String> componentIds);

}
