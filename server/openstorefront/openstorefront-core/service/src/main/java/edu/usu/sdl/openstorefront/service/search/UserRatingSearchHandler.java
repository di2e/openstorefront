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
package edu.usu.sdl.openstorefront.service.search;

import edu.usu.sdl.openstorefront.common.util.Convert;
import edu.usu.sdl.openstorefront.core.model.search.SearchElement;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dshurtleff
 */
public class UserRatingSearchHandler
		extends BaseSearchHandler
{

	public UserRatingSearchHandler(SearchElement searchElement)
	{
		super(searchElement);
	}

	@Override
	protected ValidationResult internalValidate()
	{
		ValidationResult validationResult = new ValidationResult();

		if (StringUtils.isBlank(searchElement.getValue())) {
			validationResult.getRuleResults().add(getRuleResult("value", "Required"));
		}
		if (StringUtils.isNumeric(searchElement.getValue()) == false) {
			validationResult.getRuleResults().add(getRuleResult("value", "Must be a integer number"));
		}

		return validationResult;
	}

	@Override
	public List<String> processSearch()
	{
		Integer checkValue = Convert.toInteger(searchElement.getValue());
		if (checkValue == null) {
			checkValue = 0;
		}

		List<String> results = new ArrayList<>();
		Map<Integer, List<String>> ratingMap = serviceProxy.getRepoFactory().getComponentRepo().getAverageRatingForComponents(checkValue, searchElement.getNumberOperation());
		for (List<String> values : ratingMap.values()) {
			results.addAll(values);
		}

		return results;
	}

}
