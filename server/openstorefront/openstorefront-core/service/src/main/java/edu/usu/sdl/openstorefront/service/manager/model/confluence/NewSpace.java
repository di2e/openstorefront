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
package edu.usu.sdl.openstorefront.service.manager.model.confluence;

/**
 *
 * @author dshurtleff
 */
public class NewSpace
{

	private String id;
	private String key;
	private String name;
	private Description description;
	private Metadata metadata;
	private boolean privateSpace;

	public NewSpace()
	{
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Description getDescription()
	{
		return description;
	}

	public void setDescription(Description description)
	{
		this.description = description;
	}

	public Metadata getMetadata()
	{
		return metadata;
	}

	public void setMetadata(Metadata metadata)
	{
		this.metadata = metadata;
	}

	public boolean getPrivateSpace()
	{
		return privateSpace;
	}

	public void setPrivateSpace(boolean privateSpace)
	{
		this.privateSpace = privateSpace;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

}
