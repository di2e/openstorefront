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
package edu.usu.sdl.openstorefront.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dshurtleff
 */
public class EntryListingReportModel
		extends BaseReportModel
{

	private String viewLink;
	private int totalRecords;

	private EntryListingDataSet recentlyUpdated;
	private EntryListingDataSet recentlyEvaluated;

	private List<EntryListingDataSet> data = new ArrayList<>();

	public EntryListingReportModel()
	{
		recentlyUpdated = new EntryListingDataSet();
		recentlyUpdated.setTitle("Recently Updated");

		recentlyEvaluated = new EntryListingDataSet();
		recentlyEvaluated.setTitle("Recently Completed Evaluations");

	}

	@Override
	public List<EntryListingDataSet> getData()
	{
		return data;
	}

	public void setData(List<EntryListingDataSet> data)
	{
		this.data = data;
	}

	public String getViewLink()
	{
		return viewLink;
	}

	public void setViewLink(String viewLink)
	{
		this.viewLink = viewLink;
	}

	public EntryListingDataSet getRecentlyUpdated()
	{
		return recentlyUpdated;
	}

	public void setRecentlyUpdated(EntryListingDataSet recentlyUpdated)
	{
		this.recentlyUpdated = recentlyUpdated;
	}

	public EntryListingDataSet getRecentlyEvaluated()
	{
		return recentlyEvaluated;
	}

	public void setRecentlyEvaluated(EntryListingDataSet recentlyEvaluated)
	{
		this.recentlyEvaluated = recentlyEvaluated;
	}

	public int getTotalRecords()
	{
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords)
	{
		this.totalRecords = totalRecords;
	}

}
