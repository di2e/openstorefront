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
package edu.usu.sdl.openstorefront.service.search;

import edu.usu.sdl.openstorefront.common.exception.OpenStorefrontRuntimeException;
import edu.usu.sdl.openstorefront.common.util.Convert;
import edu.usu.sdl.openstorefront.common.util.ReflectionUtil;
import edu.usu.sdl.openstorefront.core.api.query.GenerateStatementOption;
import edu.usu.sdl.openstorefront.core.api.query.GenerateStatementOptionBuilder;
import edu.usu.sdl.openstorefront.core.api.query.QueryByExample;
import edu.usu.sdl.openstorefront.core.api.query.SpecialOperatorModel;
import edu.usu.sdl.openstorefront.core.entity.ComponentQuestion;
import edu.usu.sdl.openstorefront.core.model.search.SearchElement;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dshurtleff
 */
public class QuestionSearchHandler
		extends BaseSearchHandler
{

	public QuestionSearchHandler(SearchElement searchElement)
	{
		super(searchElement);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ValidationResult internalValidate()
	{
		ValidationResult validationResult = new ValidationResult();

		if (StringUtils.isBlank(searchElement.getField())) {
			validationResult.getRuleResults().add(getRuleResult("field", "Required"));
		}
		boolean checkValue = true;
		Field field = ReflectionUtil.getField(new ComponentQuestion(), searchElement.getField());
		if (field == null) {
			validationResult.getRuleResults().add(getRuleResult("field", "Doesn't exist on question"));
		} else {
			Class type = field.getType();
			if (type.isAssignableFrom(String.class)) {
				//Nothing to check
			} else if (type.isAssignableFrom(Integer.class)) {
				if (StringUtils.isNumeric(searchElement.getValue()) == false) {
					validationResult.getRuleResults().add(getRuleResult("value", "Value should be an integer for this field"));
				}
			} else if (type.isAssignableFrom(Date.class)) {
				checkValue = false;
				if (searchElement.getStartDate() == null && searchElement.getEndDate() == null) {
					validationResult.getRuleResults().add(getRuleResult("startDate", "Start or End date should be entered for this field"));
					validationResult.getRuleResults().add(getRuleResult("endDate", "Start or End date should be entered for this field"));
				}
			} else if (type.isAssignableFrom(Boolean.class)) {
				//Nothing to check
			} else {
				validationResult.getRuleResults().add(getRuleResult("field", "Field type handling not supported"));
			}
		}

		if (checkValue && StringUtils.isBlank(searchElement.getValue())) {
			validationResult.getRuleResults().add(getRuleResult("value", "Required"));
		}

		return validationResult;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> processSearch()
	{

		try {
			ComponentQuestion componentQuestion = new ComponentQuestion();
			componentQuestion.setActiveStatus(ComponentQuestion.ACTIVE_STATUS);

			Field field = ReflectionUtil.getField(new ComponentQuestion(), searchElement.getField());
			field.setAccessible(true);
			QueryByExample<ComponentQuestion> queryByExample = new QueryByExample<>(componentQuestion);

			Class type = field.getType();
			if (type.isAssignableFrom(String.class)) {
				String likeValue = null;
				switch (searchElement.getStringOperation()) {
					case EQUALS:
						String value = searchElement.getValue();
						if (searchElement.getCaseInsensitive()) {
							queryByExample.getFieldOptions().put(field.getName(),
									new GenerateStatementOptionBuilder().setMethod(GenerateStatementOption.METHOD_LOWER_CASE).build());
							value = value.toLowerCase();
						}
						field.set(componentQuestion, value);
						break;
					default:
						likeValue = searchElement.getStringOperation().toQueryString(searchElement.getValue());
						break;
				}

				if (likeValue != null) {
					ComponentQuestion componentQuestionLike = new ComponentQuestion();
					if (searchElement.getCaseInsensitive()) {
						likeValue = likeValue.toLowerCase();
						queryByExample.getLikeExampleOption().setMethod(GenerateStatementOption.METHOD_LOWER_CASE);
					}
					field.set(componentQuestionLike, likeValue);
					queryByExample.setLikeExample(componentQuestionLike);
				}
			} else if (type.isAssignableFrom(Integer.class)) {
				field.set(componentQuestion, Convert.toInteger(searchElement.getValue()));
				queryByExample.getFieldOptions().put(field.getName(),
						new GenerateStatementOptionBuilder().setOperation(searchElement.getNumberOperation().toQueryOperation()).build());
			} else if (type.isAssignableFrom(Date.class)) {

				ComponentQuestion componentQuestionStartExample = new ComponentQuestion();

				field.set(componentQuestionStartExample, searchElement.getStartDate());
				SpecialOperatorModel<ComponentQuestion> specialOperatorModel = new SpecialOperatorModel<>();
				specialOperatorModel.setExample(componentQuestionStartExample);
				specialOperatorModel.getGenerateStatementOption().setOperation(GenerateStatementOption.OPERATION_GREATER_THAN);
				queryByExample.getExtraWhereCauses().add(specialOperatorModel);

				ComponentQuestion componentQuestionEndExample = new ComponentQuestion();

				field.set(componentQuestionEndExample, searchElement.getEndDate());
				specialOperatorModel = new SpecialOperatorModel<>();
				specialOperatorModel.setExample(componentQuestionEndExample);
				specialOperatorModel.getGenerateStatementOption().setOperation(GenerateStatementOption.OPERATION_LESS_THAN_EQUAL);
				specialOperatorModel.getGenerateStatementOption().setParameterSuffix(GenerateStatementOption.PARAMETER_SUFFIX_END_RANGE);
				queryByExample.getExtraWhereCauses().add(specialOperatorModel);

			} else if (type.isAssignableFrom(Boolean.class)) {
				field.set(componentQuestion, Convert.toBoolean(searchElement.getValue()));
			} else {
				throw new OpenStorefrontRuntimeException("Type: " + type.getSimpleName() + " is not support in this query handler", "Add support");
			}

			List<ComponentQuestion> questions = serviceProxy.getPersistenceService().queryByExample(queryByExample);
			List<String> results = new ArrayList<>();
			for (ComponentQuestion question : questions) {
				results.add(question.getComponentId());
			}
			return results;
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | OpenStorefrontRuntimeException e) {
			throw new OpenStorefrontRuntimeException("Unable to handle search request", e);
		}

	}

}
