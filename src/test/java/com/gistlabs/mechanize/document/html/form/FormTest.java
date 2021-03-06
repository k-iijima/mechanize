/**
 * Copyright (C) 2012 Gist Labs, LLC. (http://gistlabs.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.gistlabs.mechanize.document.html.form;

import static com.gistlabs.mechanize.document.html.query.HtmlQueryBuilder.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.gistlabs.mechanize.MechanizeTestCase;
import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.Document;
import com.gistlabs.mechanize.document.html.form.Email;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.FormElement;
import com.gistlabs.mechanize.document.html.form.Select;

/**
 * @author Martin Kersten<Martin.Kersten.mk@gmail.com>
 */
public class FormTest extends MechanizeTestCase {
	
	@Test
	public void testEmptyFormWithGetMethod() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form")));
		agent.addPageRequest("http://test.com/form", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Resource response = form.submit();
		assertEquals("OK", response.getTitle());
		assertFalse(form.isDoPost());
	}

	@Test
	public void testEmptyFormWithPostMethod() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form", "post").id("form")));
		agent.addPageRequest("POST", "http://test.com/form", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
		assertTrue(form.isDoPost());
	}

	@Test
	public void testSimpleInputNoText() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addText("text", null)));
		agent.addPageRequest("http://test.com/form?text=", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testSimpleInputWithDefaultText() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addText("text", "Text")));
		agent.addPageRequest("http://test.com/form?text=Text", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testSimpleInputSettingValueToValueBiggerThanMaxLengthWillGetAutomaticallyTruncated() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addText("text", null, 5)));
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.get("text").set("123456789");
		assertEquals("12345", form.get("text").get());
	}
	
	@Test
	public void testEmailInputFieldWithNoText() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addInput("mail", "email", null)));
		agent.addPageRequest("http://test.com/form?mail=", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		FormElement email = form.get("mail");
		assertTrue(email instanceof Email);
		assertSame(email, form.getEmail(byName("mail")));
		assertSame(email, form.getEmailFields(byName("mail")).get(0));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testUnrecognizedInputFieldWithNoText() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addInput("unknown", "unknownType", null)));
		agent.addPageRequest("http://test.com/form?unknown=test", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		FormElement unknown = form.get("unknown");
		assertNotNull(unknown);
		assertTrue(unknown instanceof FormElement);
		unknown.setValue("test");
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}
	
	@Test
	public void testTextAreaInputWithDefaultText() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addTextArea("text", "Text")));
		agent.addPageRequest("http://test.com/form?text=Text", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	public void testTextAreaInputWithChangedValue() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addTextArea("text", "Text")));
		agent.addPageRequest("http://test.com/form?text=differentText", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getTextArea(byName("text")).setValue("differentText");
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testHiddenInput() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addHidden("hidden", "Text")));
		agent.addPageRequest("http://test.com/form?hidden=Text", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSettingValueOfSubmitButtonFails() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addSubmitButton("button", "Text")));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.get(byName("button")).setValue("shouldFail");
	}

	@Test
	public void testSimpleLoginFormWithTextAndPasswordAndSubmitButtonSubmittingByButton() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addText("user", null).addPassword("pass", null).addSubmitButton("submit", "pressed")));
		agent.addPageRequest("http://test.com/form?user=username&pass=password&submit=pressed", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.get("user").setValue("username");
		form.get("pass").setValue("password");
		Document response = form.getSubmitButton(byName("submit")).submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testSimpleLoginFormWithTextAndPasswordAndSubmitButtonSubmittingByPressingEnter() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addText("user", null).addPassword("pass", null).addSubmitButton("submit", "pressed")));
		agent.addPageRequest("http://test.com/form?user=username&pass=password", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.get("user").setValue("username");
		form.get("pass").setValue("password");
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testCheckboxForm() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addCheckbox("box", "value1").addCheckedCheckbox("box", "value2").addCheckedCheckbox("box", "value3")));
		agent.addPageRequest("http://test.com/form?box=value1&box=value3", newHtml("OK", ""));
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		assertFalse(form.getCheckbox("box", "value1").isChecked());
		assertTrue(form.getCheckbox("box", "value2").isChecked());
		assertTrue(form.getCheckbox("box", "value3").isChecked());
		assertEquals(3, form.getCheckboxes(byName("box")).size());
		
		form.getCheckbox("box", "value1").check();
		form.getCheckbox("box", "value2").uncheck();
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSettingValueOfCheckboxFails() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addCheckbox("box", "value")));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getCheckbox(byName("box")).setValue("shouldFail");
	}
	
	@Test
	public void testRadioButtonForm() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addRadioButton("button", "value1").
						addCheckedRadioButton("button", "value2").addRadioButton("button", "value3")));
		agent.addPageRequest("http://test.com/form?button=value3", newHtml("OK", ""));
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		assertFalse(form.getRadioButton("button", "value1").isChecked());
		assertTrue(form.getRadioButton("button", "value2").isChecked());
		assertFalse(form.getRadioButton("button", "value3").isChecked());
		assertEquals(3, form.getRadioButtons("button").size());
		
		form.getRadioButton("button", "value3").check();
		assertFalse(form.getRadioButton("button", "value1").isChecked());
		assertFalse(form.getRadioButton("button", "value2").isChecked());
		assertTrue(form.getRadioButton("button", "value3").isChecked());
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSettingValueOfRadioButtonFails() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addRadioButton("button", "value")));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getRadioButton("button").setValue("shouldFail");
	}
	
	@Test
	public void testSingleElementSelect() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").beginSelect("person").addOption("Peter", "1").addOption("John", "2").addSelectedOption("Susanna", "3").end()));
		agent.addPageRequest("http://test.com/form?person=1", newHtml("OK", ""));
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Select select = form.getSelect(byName("person"));
		assertFalse(select.isMultiple());
		assertFalse(select.getOption("Peter").isSelected());
		assertFalse(select.getOption("John").isSelected());
		assertTrue(select.getOption("Susanna").isSelected());
		assertEquals(3, select.getOptions().size());
		
		select.getOption("Peter").select();
		assertTrue(select.getOption("Peter").isSelected());
		assertFalse(select.getOption("John").isSelected());
		assertFalse(select.getOption("Susanna").isSelected());
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	@Test
	public void testMultipleElementSelect() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").beginMultiSelect("person").addOption("Peter", "1").addSelectedOption("John", "2").addOption("Susanna", "3").end()));
		agent.addPageRequest("http://test.com/form?person=2&person=3", newHtml("OK", ""));
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Select select = form.getSelect(byName("person"));
		assertTrue(select.isMultiple());
		assertFalse(select.getOption("Peter").isSelected());
		assertTrue(select.getOption("John").isSelected());
		assertFalse(select.getOption("Susanna").isSelected());
		assertEquals(3, select.getOptions().size());
		
		select.getOption("Peter").select();
		assertTrue(select.getOption("Peter").isSelected());
		assertTrue(select.getOption("John").isSelected());
		assertFalse(select.getOption("Susanna").isSelected());

		select.getOption("Susanna").select();
		select.getOption("Peter").unselect();
		assertFalse(select.getOption("Peter").isSelected());
		assertTrue(select.getOption("John").isSelected());
		assertTrue(select.getOption("Susanna").isSelected());
		
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testSettingValueOfSelectFails() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").beginSelect("person").addOption("Peter", "1").addOption("John", "2").addSelectedOption("Susanna", "3").end()));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getSelect(byName("person")).setValue("shouldFail");
	}
	
	@Test
	public void testImageToSubmitForm() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addSubmitImage("submitImage", "value")));
		agent.addPageRequest("http://test.com/form?submitImage=value&submitImage.x=20&submitImage.y=10", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		Document response = form.getSubmitImage(byName("submitImage")).submit(20, 10);
		assertEquals("OK", response.getTitle());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSettingValueOfSubmitImageFails() {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").id("form").addSubmitImage("submitImage", "value")));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getSubmitImage(byName("submitImage")).setValue("shouldFail");
	}

	@Test
	public void testSimpleFileUpload() throws Exception {
		File tmpFile = File.createTempFile("mechanize", "tmp");
		
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").method("post").id("form").enctype("multipart/form-data").addFileInput("fileUpload", "")));
		agent.addPageRequest("POST", "http://test.com/form", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));
		form.getUpload(byName("fileUpload")).setValue(tmpFile);
		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFileUploadWithNoFile() throws Exception {
		agent.addPageRequest("http://test.com", 
				newHtml("Test Page", newForm("form").method("post").id("form").enctype("multipart/form-data").addFileInput("fileUpload", null)));
		agent.addPageRequest("POST", "http://test.com/form", newHtml("OK", ""));
		
		Document page = agent.get("http://test.com");
		Form form = page.forms().get(byId("form"));

		Document response = form.submit();
		assertEquals("OK", response.getTitle());
	}

	
	//TODO Find a better test
//	@Test
//	public void testFileUploadingByUsingMegaFileUpload() {
//		Mechanize agent = new Mechanize();
//		Page page = agent.get("http://www.megafileupload.com/");
//		Form form = page.forms().findByName("uploadform");
//		Upload upload = form.getUpload("uploadfile_0");
//		File file = new File(FormTest.class.getResource("test.html").getFile());
//		upload.setValue(file);
//		Page response = form.submit();
//		assertTrue(response.getUri().startsWith("http://94.75.216.85/cgi-bin/upload.cgi"));
//	}
}
