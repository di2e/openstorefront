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
package edu.usu.sdl.openstorefront.report.generator;

import au.com.bytecode.opencsv.CSVWriter;
import edu.usu.sdl.openstorefront.exception.OpenStorefrontRuntimeException;
import edu.usu.sdl.openstorefront.storage.model.Report;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author dshurtleff
 */
public class CSVGenerator
		extends BaseGenerator
{

	private CSVWriter writer;

	public CSVGenerator(Report report)
	{
		super(report);
	}

	@Override
	public void init()
	{
		Objects.requireNonNull(report, "The generator requires the report to exist.");
		Objects.requireNonNull(report.getReportId(), "The report id is required.");
		try {
			writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(report.pathToReport().toFile())));
		} catch (FileNotFoundException ex) {
			throw new OpenStorefrontRuntimeException("Unable to open file to write report.", "Check file system permissions", ex);
		}
	}

	public void addLine(Object... data)
	{
		List<String> processedData = new ArrayList<>();
		for (Object dataItem : data) {
			if (dataItem == null) {
				processedData.add("");
			} else {
				processedData.add(dataItem.toString());
			}
		}

		writer.writeNext((String[]) processedData.toArray(new String[0]));
	}

	@Override
	protected void internalFinish()
	{
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException ex) {
				throw new OpenStorefrontRuntimeException("Failed to close report file. Report: " + report.pathToReport(), ex);
			}
		}

	}

}
