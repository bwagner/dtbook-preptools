package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

import ch.sbs.plugin.preptools.VFormActionHelper;

/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class MetaUtilsTest {

	private final static String PLACE_HOLDER = "_____";

	@Test
	public void testMetaUtilsNoMeta() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("<head>");
		sb.append(PLACE_HOLDER);
		sb.append("</head>\n");
		sb.append("<book>\n");
		sb.append("</book></dtbook>\n");
		final String TEMPLATE = sb.toString();
		final Document document = DocumentTestUtil.makeDocument(TEMPLATE.replace(PLACE_HOLDER,
				""));
		MetaUtils.insertPrepToolInfo(document, VFormActionHelper.VFORM_TAG);
		sb.setLength(0);
		sb.append("<meta name=\"prod:PrepTool:");
		sb.append(VFormActionHelper.VFORM_TAG);
		sb.append("\" content=\"done\"/>");
		assertEquals(TEMPLATE.replace(PLACE_HOLDER, sb.toString()),
				document.getText(0, document.getLength()));
	}

	@Test
	public void testMetaUtilsWithMeta() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("<head>\n");
		sb.append("<meta />\n");
		sb.append(PLACE_HOLDER);
		sb.append("</head>\n");
		sb.append("<book>\n");
		sb.append("</book></dtbook>\n");
		final String TEMPLATE = sb.toString();
		final Document document = DocumentTestUtil.makeDocument(TEMPLATE.replace(PLACE_HOLDER,
				""));
		MetaUtils.insertPrepToolInfo(document, VFormActionHelper.VFORM_TAG);
		assertEquals(
				TEMPLATE.replace(PLACE_HOLDER,
						"<meta name=\"prod:PrepTool:brl:v-form\" content=\"done\"/>\n"),
				document.getText(0, document.getLength()));
	}

	@Test
	public void testMetaUtilsWithMeta2() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("	<head>\n");
		sb.append("		<meta name=\"dc:Title\" content=\"Hundenovelle\"/>\n");
		sb.append(PLACE_HOLDER);
		sb.append("	</head>\n");
		sb.append("<book>\n");
		sb.append("</book></dtbook>\n");
		final String TEMPLATE = sb.toString();
		final Document document = DocumentTestUtil.makeDocument(TEMPLATE.replace(PLACE_HOLDER,
				""));
		MetaUtils.insertPrepToolInfo(document, VFormActionHelper.VFORM_TAG);
		assertEquals(
				TEMPLATE.replace(PLACE_HOLDER,
						"		<meta name=\"prod:PrepTool:brl:v-form\" content=\"done\"/>\n"),
				document.getText(0, document.getLength()));
	}
}
