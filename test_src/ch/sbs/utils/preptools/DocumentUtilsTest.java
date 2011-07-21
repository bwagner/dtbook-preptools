package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

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
		final int count = DocumentUtils.performReplacement(document,
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
		final int count = DocumentUtils.performReplacement(document,
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
		final int count = DocumentUtils.performReplacement(document,
				"<span.*?>(.*)</span>", "$1");
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(5, count);
	}

	@Test
	public void testUnProtect() throws BadLocationException {
		final Document document = DocumentTestUtil
				.makeDocument("Dieser Text enthält leider keine Klammer. Das tut ihm Leid.Restlos.");
		final String regex = "leid.r";
		final int count = DocumentUtils.performReplacement(document,
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
		final int count = DocumentUtils.performReplacement(document,
				TextUtils.wrapI(TextUtils.quoteRegexMeta(regex)), "Leid. R");

		assertEquals(
				"Dieser Text enthält leider keine Klammer. Das tut ihm Leid. Restlos.",
				document.getText(0, document.getLength()));
		assertEquals(1, count);
	}
}
