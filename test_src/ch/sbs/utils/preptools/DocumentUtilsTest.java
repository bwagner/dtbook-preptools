package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

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

public class DocumentUtilsTest {
	@Test
	public void testDocUtil() throws BadLocationException {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb1.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb1.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb1.append("<head>");
		sb2.append("<head>");
		sb1.append("</head>\n");
		sb2.append("</head>\n");
		sb1.append("<book>\n");
		sb2.append("<book>\n");
		sb1.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb1.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb1.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("Café\n");
		sb1.append("</book>\n");
		sb2.append("</book>\n");
		sb1.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb1.toString());
		sb1.setLength(0);
		final int count = DocumentUtils.performMultipleReplacements(document,
				"<span.*?>(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(3, count);
	}

	@Test
	public void testDocUtilReduced() throws BadLocationException {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb1.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb1.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb1.append("<head>");
		sb2.append("<head>");
		sb1.append("</head>\n");
		sb2.append("</head>\n");
		sb1.append("<book>\n");
		sb2.append("<book>\n");
		sb1.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb1.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb1.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb1.append("</book>\n");
		sb2.append("</book>\n");
		sb1.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb1.toString());
		sb1.setLength(0);
		final int count = DocumentUtils.performMultipleReplacements(document,
				"<span\\s+brl:accents=\"reduced\">(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(2, count);

	}

	@Test
	public void testDocUtilDetailed() throws BadLocationException {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb1.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb1.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb1.append("<head>");
		sb2.append("<head>");
		sb1.append("</head>\n");
		sb2.append("</head>\n");
		sb1.append("<book>\n");
		sb2.append("<book>\n");
		sb1.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb1.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb1.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb1.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb1.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("Café\n");
		sb1.append("</book>\n");
		sb2.append("</book>\n");
		sb1.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb1.toString());
		sb1.setLength(0);
		final int count = DocumentUtils.performMultipleReplacements(document,
				"<span.*?>(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(5, count);
	}

	@Test
	public void testUnProtect() throws BadLocationException {
		final Document document = DocumentTestUtil
				.makeDocument("Dieser Text enthält leider keine Klammer. Das tut ihm Leid.Restlos.");
		final String regex = "leid.r";
		final int count = DocumentUtils.performMultipleReplacements(document,
				TextUtils.wrapI(regex), "Leid. R");
		assertEquals(
				"Dieser Text enthält Leid. R keine Klammer. Das tut ihm Leid. Restlos.",
				document.getText(0, document.getLength()));
		assertEquals(2, count);
	}

	@Test
	public void testProtect() throws BadLocationException {
		final Document document = DocumentTestUtil
				.makeDocument("Dieser Text enthält leider keine Klammer. Das tut ihm Leid.Restlos.");
		final String regex = "leid.r";
		final int count = DocumentUtils.performMultipleReplacements(document,
				TextUtils.wrapI(TextUtils.quoteRegexMeta(regex)), "Leid. R");

		assertEquals(
				"Dieser Text enthält leider keine Klammer. Das tut ihm Leid. Restlos.",
				document.getText(0, document.getLength()));
		assertEquals(1, count);
	}
}
