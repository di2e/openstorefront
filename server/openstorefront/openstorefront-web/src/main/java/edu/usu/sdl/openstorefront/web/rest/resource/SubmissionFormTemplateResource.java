/*
 * Copyright 2018 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * See NOTICE.txt for more information.
 */
package edu.usu.sdl.openstorefront.web.rest.resource;

import edu.usu.sdl.openstorefront.core.annotation.APIDescription;
import edu.usu.sdl.openstorefront.core.annotation.DataType;
import edu.usu.sdl.openstorefront.core.entity.SecurityPermission;
import edu.usu.sdl.openstorefront.core.entity.SubmissionFormResource;
import edu.usu.sdl.openstorefront.core.entity.SubmissionFormTemplate;
import edu.usu.sdl.openstorefront.core.view.SubmissionFormTemplateView;
import edu.usu.sdl.openstorefront.doc.security.RequireSecurity;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dshurtleff
 */
@Path("v1/resource/submissiontemplates")
@APIDescription("Submission Template Resource")
public class SubmissionFormTemplateResource
		extends BaseResource
{

	@GET
	@APIDescription("Gets Submission Templates")
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(SubmissionFormTemplateView.class)
	public List<SubmissionFormTemplateView> getSubmissionFormTemplates(
			@QueryParam("status") String status
	)
	{
		SubmissionFormTemplate submissionFormTemplate = new SubmissionFormTemplate();
		if (StringUtils.isNotBlank(status)) {
			submissionFormTemplate.setActiveStatus(status);
		}
		return SubmissionFormTemplateView.toView(submissionFormTemplate.findByExample());
	}

	@GET
	@APIDescription("Gets Template")
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(SubmissionFormTemplateView.class)
	@Path("/{templateId}")
	public Response getSubmissionFormTemplate(
			@PathParam("templateId") String templateId
	)
	{
		SubmissionFormTemplate submissionFormTemplate = new SubmissionFormTemplate();
		submissionFormTemplate.setSubmissionTemplateId(templateId);
		submissionFormTemplate = submissionFormTemplate.find();

		SubmissionFormTemplateView view = null;
		if (submissionFormTemplate != null) {
			view = SubmissionFormTemplateView.toView(submissionFormTemplate);
		}
		return sendSingleEntityResponse(view);
	}

	@POST
	@APIDescription("Creates a new Submission Template")
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@DataType(SubmissionFormTemplate.class)
	public Response createSubmissionTemplate(
			SubmissionFormTemplate submissionFormTemplate
	)
	{
		return handleSaveSubmissionTemplate(submissionFormTemplate, true);
	}

	@PUT
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@APIDescription("Updates an Submission Template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(SubmissionFormTemplate.class)
	@Path("/{templateId}")
	public Response updateSubmissionTemplate(
			@PathParam("templateId") String templateId,
			SubmissionFormTemplate submissionFormTemplate)
	{
		SubmissionFormTemplate existing = new SubmissionFormTemplate();
		existing.setSubmissionTemplateId(templateId);
		existing = existing.find();

		Response response = Response.status(Response.Status.NOT_FOUND).build();
		if (existing != null) {
			submissionFormTemplate.setSubmissionTemplateId(templateId);
			response = handleSaveSubmissionTemplate(existing, false);
		}
		return response;
	}

	private Response handleSaveSubmissionTemplate(SubmissionFormTemplate submissionFormTemplate, boolean post)
	{
		ValidationResult validationResult = submissionFormTemplate.validate();
		if (validationResult.valid()) {
			submissionFormTemplate = service.getSubmissionFormService().saveSubmissionFormTemplate(submissionFormTemplate);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/usersubmissions/" + submissionFormTemplate.getSubmissionTemplateId())).entity(submissionFormTemplate).build();
		} else {
			return Response.ok(submissionFormTemplate).build();
		}
	}

	@PUT
	@APIDescription("Activate Form template")
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{templateId}/activate")
	public Response activateTemplate(
			@PathParam("templateId") String templateId
	)
	{
		return updateStatus(templateId, SubmissionFormTemplate.ACTIVE_STATUS);
	}

	@PUT
	@APIDescription("Activate Form template")
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{templateId}/inactivate")
	public Response inactivateTemplate(
			@PathParam("templateId") String templateId
	)
	{
		return updateStatus(templateId, SubmissionFormTemplate.INACTIVE_STATUS);
	}

	private Response updateStatus(String templateId, String newStatus)
	{
		SubmissionFormTemplate submissionFormTemplate = new SubmissionFormTemplate();
		submissionFormTemplate.setSubmissionTemplateId(templateId);
		submissionFormTemplate = submissionFormTemplate.find();
		if (submissionFormTemplate != null) {
			submissionFormTemplate.setActiveStatus(newStatus);
			submissionFormTemplate.save();
		}

		return sendSingleEntityResponse(submissionFormTemplate);
	}

	@DELETE
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@APIDescription("Deletes a submission template")
	@Path("/{templateId}")
	public void deleteUserSubmission(
			@PathParam("templateId") String templateId
	)
	{
		service.getSubmissionFormService().deleteSubmissionFormTemplate(templateId);
	}

	@DELETE
	@RequireSecurity(SecurityPermission.ADMIN_SUBMISSION_FORM_TEMPLATE)
	@APIDescription("Deletes a submission template resource")
	@Path("/{templateId}/resource/{resourceId}")
	public void deleteUserSubmission(
			@PathParam("templateId") String templateId,
			@PathParam("resourceId") String resourceId
	)
	{
		SubmissionFormResource submissionFormResource = new SubmissionFormResource();
		submissionFormResource.setResourceId(resourceId);
		submissionFormResource.setTemplateId(templateId);
		submissionFormResource = submissionFormResource.find();

		if (submissionFormResource != null) {
			service.getSubmissionFormService().deleteSubmissionFormResource(resourceId);
		}
	}

}