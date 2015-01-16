/*
 * Copyright 2014 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.web.rest.service;

import edu.usu.sdl.openstorefront.doc.APIDescription;
import edu.usu.sdl.openstorefront.doc.DataType;
import edu.usu.sdl.openstorefront.doc.RequireAdmin;
import edu.usu.sdl.openstorefront.doc.RequiredParam;
import edu.usu.sdl.openstorefront.service.manager.model.TaskRequest;
import edu.usu.sdl.openstorefront.service.query.QueryByExample;
import edu.usu.sdl.openstorefront.service.query.QueryType;
import edu.usu.sdl.openstorefront.sort.RecentlyAddedViewComparator;
import edu.usu.sdl.openstorefront.storage.model.AttributeCode;
import edu.usu.sdl.openstorefront.storage.model.AttributeCodePk;
import edu.usu.sdl.openstorefront.storage.model.Component;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import edu.usu.sdl.openstorefront.web.rest.model.ArticleView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentSearchView;
import edu.usu.sdl.openstorefront.web.rest.model.FilterQueryParams;
import edu.usu.sdl.openstorefront.web.rest.model.ListingStats;
import edu.usu.sdl.openstorefront.web.rest.model.RecentlyAddedView;
import edu.usu.sdl.openstorefront.web.rest.model.SearchQuery;
import edu.usu.sdl.openstorefront.web.rest.resource.BaseResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Search Service
 *
 * @author dshurtleff
 */
@Path("v1/service/search")
@APIDescription("Provides access to search listing in the application")
public class Search
		extends BaseResource
{

	public class CustomComparator
			implements Comparator<ComponentSearchView>
	{

		@Override
		public int compare(ComponentSearchView o1, ComponentSearchView o2)
		{
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}
	}

	@GET
	@APIDescription("Searches listing according to parameters.  (Components, Articles)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentSearchView.class)
	public Response searchListing(
			@BeanParam SearchQuery query,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		List<ComponentSearchView> result = service.getSearchService().getSearchItems(query, filterQueryParams);
		Collections.sort(result, new CustomComparator());
		GenericEntity<List<ComponentSearchView>> entity = new GenericEntity<List<ComponentSearchView>>(result)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes all indexes from Solr")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/clearSolr")
	public Response clearSolr()
	{
		service.getSearchService().deleteAll();
		return Response.noContent().build();
	}

	@POST
	@RequireAdmin
	@APIDescription("Removes all indexes from Solr and then re-indexes current components and articles")
	@Path("/resetSolr")
	public Response resetSolr()
	{
		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setAllowMultiple(false);
		taskRequest.setName("Resetting Indexer");

		service.getAyncProxy(service.getSearchService(), taskRequest).resetIndexer();
		return Response.ok().build();
	}

	@GET
	@APIDescription("Searches listing according to parameters.  (Components, Articles)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentSearchView.class)
	@Path("/attribute/{type}/{code}")
	public Response searchListing(
			@PathParam("type")
			@RequiredParam String type,
			@PathParam("code")
			@RequiredParam String code,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		AttributeCodePk pk = new AttributeCodePk();

		pk.setAttributeCode(code);
		pk.setAttributeType(type);

		List<ComponentSearchView> result = service.getSearchService().architectureSearch(pk, filterQueryParams);
		Collections.sort(result, new CustomComparator());
		GenericEntity<List<ComponentSearchView>> entity = new GenericEntity<List<ComponentSearchView>>(result)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Used to retrieve all possible search results.")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(Component.class)
	@Path("/all")
	public List<ComponentSearchView> getAllForSearch()
	{
		List<ComponentSearchView> result = service.getSearchService().getAll();
		if (result != null) {
			Collections.sort(result, new CustomComparator());
			return result;
		} else {
			return null;
		}
	}

	@GET
	@APIDescription("Gets the recently added items")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(RecentlyAddedView.class)
	@Path("/recent")
	public List<RecentlyAddedView> getRecentlyAdded(
			@DefaultValue("5")
			@QueryParam("max") int maxResults)
	{
		List<RecentlyAddedView> recentlyAddedViews = new ArrayList<>();

		List<Component> components = service.getComponentService().findRecentlyAdded(maxResults);
		List<AttributeCode> attributeCodes = service.getAttributeService().findRecentlyAddedArticles(maxResults);

		for (Component component : components) {
			RecentlyAddedView recentlyAddedView = new RecentlyAddedView();
			recentlyAddedView.setListingType(OpenStorefrontConstant.ListingType.COMPONENT);
			recentlyAddedView.setComponentId(component.getComponentId());
			recentlyAddedView.setName(component.getName());
			recentlyAddedView.setDescription(component.getDescription());
			recentlyAddedView.setAddedDts(component.getApprovedDts());
			recentlyAddedViews.add(recentlyAddedView);
		}

		for (AttributeCode attributeCode : attributeCodes) {
			RecentlyAddedView recentlyAddedView = new RecentlyAddedView();
			recentlyAddedView.setListingType(OpenStorefrontConstant.ListingType.ARTICLE);
			recentlyAddedView.setArticleAttributeType(attributeCode.getAttributeCodePk().getAttributeType());
			recentlyAddedView.setArticleAttributeCode(attributeCode.getAttributeCodePk().getAttributeCode());

			ArticleView articleView = ArticleView.toView(attributeCode);
			recentlyAddedView.setDescription(articleView.getDescription());
			recentlyAddedView.setName(articleView.getTitle());
			recentlyAddedView.setAddedDts(attributeCode.getUpdateDts());
			recentlyAddedViews.add(recentlyAddedView);
		}

		recentlyAddedViews.sort(new RecentlyAddedViewComparator<>());
		if (recentlyAddedViews.size() > maxResults) {
			recentlyAddedViews = recentlyAddedViews.subList(0, maxResults);
		}

		return recentlyAddedViews;
	}

	@GET
	@APIDescription("Get Listing Stats")
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/stats")
	public Response getListingStats()
	{
		ListingStats listingStats = new ListingStats();

		Component componentExample = new Component();
		componentExample.setActiveStatus(Component.ACTIVE_STATUS);
		long numberOfActiveComponents = service.getPersistenceService().countByExample(new QueryByExample(QueryType.COUNT, componentExample));
		listingStats.setNumberOfComponents(numberOfActiveComponents);

		List<AttributeCode> articles = service.getAttributeService().findRecentlyAddedArticles(Integer.MAX_VALUE);
		listingStats.setNumberOfArticles(articles.size());

		return Response.ok(listingStats).build();
	}

}
