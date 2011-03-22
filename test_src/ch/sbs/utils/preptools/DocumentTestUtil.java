package ch.sbs.utils.preptools;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class DocumentTestUtil {

	public static Document makeDocument(final String content)
			throws BadLocationException {
		final Document pd = new PlainDocument();
		pd.insertString(0, content, null);
		return pd;
	}

}
