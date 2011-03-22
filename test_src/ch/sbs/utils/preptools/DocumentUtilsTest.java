package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

public class DocumentUtilsTest {
	@Test
	public void testDocUtil() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("<head>");
		sb2.append("<head>");
		sb.append("</head>\n");
		sb2.append("</head>\n");
		sb.append("<book>\n");
		sb2.append("<book>\n");
		sb.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("Café\n");
		sb.append("</book>\n");
		sb2.append("</book>\n");
		sb.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb.toString());
		sb.setLength(0);
		DocumentUtils
				.performReplacement(document, "<span.*?>(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));

	}

	@Test
	public void testDocUtilReduced() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("<head>");
		sb2.append("<head>");
		sb.append("</head>\n");
		sb2.append("</head>\n");
		sb.append("<book>\n");
		sb2.append("<book>\n");
		sb.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb.append("</book>\n");
		sb2.append("</book>\n");
		sb.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb.toString());
		sb.setLength(0);
		DocumentUtils.performReplacement(document,
				"<span\\s+brl:accents=\"reduced\">(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));

	}

	@Test
	public void testDocUtilDetailed() throws BadLocationException {
		final StringBuilder sb = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb2.append("<dtbook xmlns:brl=\"http://www.daisy.org/z3986/2009/braille/\">\n");
		sb.append("<head>");
		sb2.append("<head>");
		sb.append("</head>\n");
		sb2.append("</head>\n");
		sb.append("<book>\n");
		sb2.append("<book>\n");
		sb.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb.append("<span brl:accents=\"reduced\">Térezia</span>\n");
		sb2.append("Térezia\n");
		sb.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb.append("<span brl:accents=\"reduced\">València</span>\n");
		sb2.append("València\n");
		sb.append("<span brl:accents=\"detailed\">Café</span>\n");
		sb2.append("Café\n");
		sb.append("</book>\n");
		sb2.append("</book>\n");
		sb.append("</dtbook>\n");
		sb2.append("</dtbook>\n");
		final Document document = DocumentTestUtil.makeDocument(sb.toString());
		sb.setLength(0);
		DocumentUtils
				.performReplacement(document, "<span.*?>(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
	}
}
