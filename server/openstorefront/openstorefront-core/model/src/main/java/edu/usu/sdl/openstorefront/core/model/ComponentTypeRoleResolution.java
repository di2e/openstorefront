/*
 * Copyright 2018 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dshurtleff
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComponentTypeRoleResolution
		implements Serializable
{

	private static final long serialVersionUID = 1L;

	private List<String> roles = new ArrayList<>();
	private String ancestorComponentType;
	private String ancestorComponentTypeLabel;
	private boolean cameFromAncestor;

	@SuppressWarnings({"squid:S2637", "squid:S1186"})
	public ComponentTypeRoleResolution()
	{
	}

	public List<String> getRoles()
	{
		return roles;
	}

	public void setRoles(List<String> roles)
	{
		this.roles = roles;
	}

	public String getAncestorComponentType()
	{
		return ancestorComponentType;
	}

	public void setAncestorComponentType(String ancestorComponentType)
	{
		this.ancestorComponentType = ancestorComponentType;
	}

	public String getAncestorComponentTypeLabel()
	{
		return ancestorComponentTypeLabel;
	}

	public void setAncestorComponentTypeLabel(String ancestorComponentTypeLabel)
	{
		this.ancestorComponentTypeLabel = ancestorComponentTypeLabel;
	}

	public boolean getCameFromAncestor()
	{
		return cameFromAncestor;
	}

	public void setCameFromAncestor(boolean cameFromAncestor)
	{
		this.cameFromAncestor = cameFromAncestor;
	}

}
