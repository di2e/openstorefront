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

import edu.usu.sdl.openstorefront.core.api.query.GenerateStatementOption;
import edu.usu.sdl.openstorefront.core.api.query.GenerateStatementOptionBuilder;
import edu.usu.sdl.openstorefront.core.api.query.QueryByExample;
import edu.usu.sdl.openstorefront.core.entity.ComponentTag;
import edu.usu.sdl.openstorefront.core.model.search.SearchElement;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dshurtleff
 */
public class TagSearchHandler
		extends BaseSearchHandler
{

	public TagSearchHandler(SearchElement searchElement)
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

		return validationResult;
	}

	@Override
	public List<String> processSearch()
	{

		ComponentTag componentTag = new ComponentTag();
		componentTag.setActiveStatus(ComponentTag.ACTIVE_STATUS);
		QueryByExample<ComponentTag> queryByExample = new QueryByExample<>(componentTag);

		String tagValue = searchElement.getValue();
		if (searchElement.getCaseInsensitive()) {
			tagValue = tagValue.toLowerCase();
			queryByExample.getFieldOptions().put(ComponentTag.FIELD_TEXT,
					new GenerateStatementOptionBuilder().setMethod(GenerateStatementOption.METHOD_LOWER_CASE).build());

		}
		String likeValue = null;
		switch (searchElement.getStringOperation()) {
			case EQUALS:
				componentTag.setText(tagValue);
				break;
			default:
				likeValue = searchElement.getStringOperation().toQueryString(searchElement.getValue());
				break;
		}

		if (likeValue != null) {
			ComponentTag componentTagLike = new ComponentTag();

			if (searchElement.getCaseInsensitive()) {
				likeValue = likeValue.toLowerCase();
				queryByExample.getFieldOptions().clear();
				queryByExample.getLikeExampleOption().setMethod(GenerateStatementOption.METHOD_LOWER_CASE);
			}
			componentTagLike.setText(likeValue);

			queryByExample.setLikeExample(componentTagLike);
		}

		List<ComponentTag> componentTags = serviceProxy.getPersistenceService().queryByExample(queryByExample);
		List<String> results = new ArrayList<>();
		for (ComponentTag tag : componentTags) {
			results.add(tag.getComponentId());
		}
		return results;
	}

}
